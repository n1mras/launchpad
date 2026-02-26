package se.haxtrams.launchpad.backend.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import se.haxtrams.launchpad.backend.model.domain.settings.Settings;
import se.haxtrams.launchpad.backend.model.domain.settings.VideoSettings;
import se.haxtrams.launchpad.backend.model.repository.FileEntity;
import se.haxtrams.launchpad.backend.model.repository.VideoEntity;
import se.haxtrams.launchpad.backend.repository.FileRepository;
import se.haxtrams.launchpad.backend.repository.VideoRepository;

@ExtendWith(MockitoExtension.class)
class SyncServiceTest {

    @TempDir
    Path tempDir;

    @Mock
    FileRepository fileRepository;

    @Mock
    VideoRepository videoRepository;

    @Mock
    DataLoader dataLoader;

    @Mock
    Settings settings;

    @Mock
    VideoSettings videoSettings;

    @InjectMocks
    SyncService syncService;

    @BeforeEach
    void setUp() {
        lenient().when(settings.getVideoSettings()).thenReturn(videoSettings);
        lenient().when(videoSettings.getFileTypes()).thenReturn(Set.of("mkv", "mp4", "avi"));
    }

    @Test
    void sync_savesNewVideoFile() {
        var folder = tempDir.toString();
        var file = new File(folder + "/movie.mkv");
        var fileEntity = new FileEntity(file.getName(), file.getAbsolutePath(), file.getParent());

        when(videoSettings.getFolders()).thenReturn(List.of(folder));
        when(fileRepository.findAllPaths()).thenReturn(List.of());
        when(fileRepository.streamAllBy()).thenReturn(Stream.empty());
        simulateTraversal(folder, file);
        when(fileRepository.saveAll(anyList())).thenReturn(List.of(fileEntity));
        when(videoRepository.saveAll(anyList())).thenReturn(List.of(new VideoEntity("movie", fileEntity)));

        syncService.sync();

        verify(fileRepository).saveAll(anyList());
        verify(videoRepository).saveAll(anyList());
    }

    @Test
    void sync_skipsNonVideoFiles() {
        var folder = tempDir.toString();
        var file = new File(folder + "/document.pdf");

        when(videoSettings.getFolders()).thenReturn(List.of(folder));
        when(fileRepository.findAllPaths()).thenReturn(List.of());
        when(fileRepository.streamAllBy()).thenReturn(Stream.empty());
        simulateTraversal(folder, file);

        syncService.sync();

        verify(fileRepository, never()).saveAll(anyList());
        verify(videoRepository, never()).saveAll(anyList());
    }

    @Test
    void sync_skipsAlreadyIndexedFile() {
        var folder = tempDir.toString();
        var file = new File(folder + "/movie.mkv");

        when(videoSettings.getFolders()).thenReturn(List.of(folder));
        when(fileRepository.findAllPaths()).thenReturn(List.of(file.getAbsolutePath()));
        when(fileRepository.streamAllBy()).thenReturn(Stream.empty());
        simulateTraversal(folder, file);

        syncService.sync();

        verify(fileRepository, never()).saveAll(anyList());
        verify(videoRepository, never()).saveAll(anyList());
    }

    @Test
    void sync_removesEntryWhenFileNoLongerExistsOnDisk() {
        var missingPath = "/nonexistent/path/movie.mkv";
        var fileEntity = new FileEntity("movie.mkv", missingPath, "/nonexistent/path");
        ReflectionTestUtils.setField(fileEntity, "id", 1L);

        when(videoSettings.getFolders()).thenReturn(List.of());
        when(fileRepository.streamAllBy()).thenReturn(Stream.of(fileEntity));

        syncService.sync();

        verify(fileRepository).deleteAllByIdInBatch(List.of(1L));
    }

    @Test
    void sync_removesEntryWhenFileExtensionIsNotInWhitelist() throws Exception {
        var file = Files.createFile(tempDir.resolve("subtitle.srt")).toFile();
        var fileEntity = new FileEntity(file.getName(), file.getAbsolutePath(), file.getParent());
        ReflectionTestUtils.setField(fileEntity, "id", 2L);

        when(videoSettings.getFolders()).thenReturn(List.of());
        when(fileRepository.streamAllBy()).thenReturn(Stream.of(fileEntity));

        syncService.sync();

        verify(fileRepository).deleteAllByIdInBatch(List.of(2L));
    }

    @Test
    void sync_keepsValidExistingEntry() throws Exception {
        var file = Files.createFile(tempDir.resolve("movie.mkv")).toFile();
        var fileEntity = new FileEntity(file.getName(), file.getAbsolutePath(), file.getParent());
        ReflectionTestUtils.setField(fileEntity, "id", 3L);

        when(videoSettings.getFolders()).thenReturn(List.of());
        when(fileRepository.streamAllBy()).thenReturn(Stream.of(fileEntity));

        syncService.sync();

        verify(fileRepository).deleteAllByIdInBatch(List.of());
    }

    @Test
    void sync_scansEachConfiguredFolder() {
        var folder1 = "/media/movies";
        var folder2 = "/media/series";

        when(videoSettings.getFolders()).thenReturn(List.of(folder1, folder2));
        when(fileRepository.findAllPaths()).thenReturn(List.of());
        when(fileRepository.streamAllBy()).thenReturn(Stream.empty());

        syncService.sync();

        verify(dataLoader).processFilesIn(eq(folder1), eq(true), any());
        verify(dataLoader).processFilesIn(eq(folder2), eq(true), any());
    }

    @Test
    void sync_doesNotStartIfAlreadyInProgress() {
        ReflectionTestUtils.setField(
                syncService, "inProgress", new java.util.concurrent.atomic.AtomicBoolean(true));

        syncService.sync();

        verify(dataLoader, never()).processFilesIn(anyString(), anyBoolean(), any());
        verify(fileRepository, never()).streamAllBy();
    }

    @SuppressWarnings("unchecked")
    private void simulateTraversal(String folder, File... files) {
        doAnswer(inv -> {
                    Consumer<File> consumer = inv.getArgument(2);
                    for (File file : files) {
                        consumer.accept(file);
                    }
                    return null;
                })
                .when(dataLoader)
                .processFilesIn(eq(folder), eq(true), any());
    }
}
