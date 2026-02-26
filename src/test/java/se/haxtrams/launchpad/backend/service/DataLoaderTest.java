package se.haxtrams.launchpad.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DataLoaderTest {

    @TempDir
    Path tempDir;

    private DataLoader dataLoader;

    @BeforeEach
    void setUp() {
        dataLoader = new DataLoader(new ObjectMapper());
    }

    @Test
    void processFilesIn_findsFilesInFlatDirectory() throws Exception {
        Files.createFile(tempDir.resolve("movie.mkv"));
        Files.createFile(tempDir.resolve("series.mp4"));

        var found = collectFiles(tempDir.toString(), true);

        assertThat(found).hasSize(2);
    }

    @Test
    void processFilesIn_recursivelyFindsFilesInSubdirectories() throws Exception {
        Files.createFile(tempDir.resolve("movie.mkv"));
        Path subDir = Files.createDirectory(tempDir.resolve("subdir"));
        Files.createFile(subDir.resolve("episode.mp4"));

        var found = collectFiles(tempDir.toString(), true);

        assertThat(found).hasSize(2);
    }

    @Test
    void processFilesIn_nonRecursive_doesNotDescendIntoSubdirectories() throws Exception {
        Files.createFile(tempDir.resolve("movie.mkv"));
        Path subDir = Files.createDirectory(tempDir.resolve("subdir"));
        Files.createFile(subDir.resolve("episode.mp4"));

        var found = collectFiles(tempDir.toString(), false);

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("movie.mkv");
    }

    @Test
    void processFilesIn_emptyDirectory_returnsNoFiles() {
        var found = collectFiles(tempDir.toString(), true);

        assertThat(found).isEmpty();
    }

    @Test
    void processFilesIn_doesNotPassDirectoriesToProcessor() throws Exception {
        Files.createDirectory(tempDir.resolve("subdir"));

        var found = collectFiles(tempDir.toString(), true);

        assertThat(found).isEmpty();
    }

    @Test
    void processFilesIn_returnsCorrectAbsolutePath() throws Exception {
        Path file = Files.createFile(tempDir.resolve("movie.mkv"));

        var found = collectFiles(tempDir.toString(), true);

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getAbsolutePath()).isEqualTo(file.toAbsolutePath().toString());
    }

    @Test
    void processFilesIn_handlesMultipleNestedDirectories() throws Exception {
        Path sub1 = Files.createDirectory(tempDir.resolve("sub1"));
        Path sub2 = Files.createDirectory(sub1.resolve("sub2"));
        Files.createFile(tempDir.resolve("a.mkv"));
        Files.createFile(sub1.resolve("b.mkv"));
        Files.createFile(sub2.resolve("c.mkv"));

        var found = collectFiles(tempDir.toString(), true);

        assertThat(found).hasSize(3);
    }

    @Test
    void batchProcessFilesIn_callsProcessorWithAllFilesInDirectory() throws Exception {
        Files.createFile(tempDir.resolve("movie.mkv"));
        Files.createFile(tempDir.resolve("series.mp4"));
        Files.createFile(tempDir.resolve("episode.avi"));

        List<File> allProcessed = new ArrayList<>();
        dataLoader.batchProcessFilesIn(tempDir.toString(), allProcessed::addAll);

        assertThat(allProcessed).hasSize(3);
    }

    @Test
    void batchProcessFilesIn_recursivelyProcessesSubdirectories() throws Exception {
        Files.createFile(tempDir.resolve("movie.mkv"));
        Path subDir = Files.createDirectory(tempDir.resolve("subdir"));
        Files.createFile(subDir.resolve("episode.mp4"));

        List<File> allProcessed = new ArrayList<>();
        dataLoader.batchProcessFilesIn(tempDir.toString(), allProcessed::addAll);

        assertThat(allProcessed).hasSize(2);
    }

    @Test
    void batchProcessFilesIn_doesNotIncludeDirectoriesInBatch() throws Exception {
        Files.createDirectory(tempDir.resolve("subdir"));
        Files.createFile(tempDir.resolve("movie.mkv"));

        List<File> allProcessed = new ArrayList<>();
        dataLoader.batchProcessFilesIn(tempDir.toString(), allProcessed::addAll);

        assertThat(allProcessed).allMatch(File::isFile);
    }

    private List<File> collectFiles(String directory, boolean recursive) {
        List<File> found = new ArrayList<>();
        dataLoader.processFilesIn(directory, recursive, found::add);
        return found;
    }
}
