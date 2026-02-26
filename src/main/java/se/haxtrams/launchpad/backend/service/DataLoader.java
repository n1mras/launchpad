package se.haxtrams.launchpad.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.haxtrams.launchpad.backend.model.domain.settings.Settings;
import se.haxtrams.launchpad.backend.util.Utils;

@SuppressWarnings("ClassCanBeRecord")
@Service
public class DataLoader {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper;

    public DataLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Settings loadSettings(final String settingsPath) {
        try {
            final var file = new File(settingsPath);
            if (!file.exists()) {
                throw new RuntimeException("Settings file not found");
            }

            return objectMapper.readValue(file, Settings.class);
        } catch (Exception e) {
            throw new RuntimeException("Error while loading settings", e);
        }
    }

    public void batchProcessFilesIn(final String directory, Consumer<List<File>> batchProcessor) {
        this.batchProcessFilesIn(new File(directory), batchProcessor);
    }

    public void batchProcessFilesIn(final File directory, Consumer<List<File>> batchProcessor) {
        var files = Utils.deNullify(directory.listFiles(), new File[0]);
        batchProcessor.accept(Arrays.stream(files).filter(File::isFile).toList());

        Arrays.stream(files).filter(File::isDirectory).forEach(dir -> batchProcessFilesIn(dir, batchProcessor));
    }

    public void processFilesIn(final String directory, final Boolean recursive, Consumer<File> processor) {
        var root = Path.of(directory);
        try (var stream = recursive ? Files.walk(root) : Files.walk(root, 1)) {
            stream.filter(p -> !Files.isDirectory(p)).map(Path::toFile).forEach(processor);
        } catch (IOException e) {
            log.warn("Error traversing directory {}: {}", directory, e.getMessage());
        }
    }
}
