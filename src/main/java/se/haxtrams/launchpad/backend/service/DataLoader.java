package se.haxtrams.launchpad.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;
import se.haxtrams.launchpad.backend.model.domain.settings.Settings;
import se.haxtrams.launchpad.backend.util.Utils;

@SuppressWarnings("ClassCanBeRecord")
@Service
public class DataLoader {
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
        processFilesIn(new File(directory), recursive, processor);
    }

    private void processFilesIn(final File directory, final Boolean recursive, Consumer<File> processor) {
        Arrays.stream(Utils.deNullify(directory.listFiles(), new File[0])).forEach(file -> {
            if (file.isFile()) {
                processor.accept(file);
            } else if (recursive && file.isDirectory()) {
                processFilesIn(file, true, processor);
            }
        });
    }
}
