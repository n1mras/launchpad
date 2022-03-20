package se.haxtrams.launchpad.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import se.haxtrams.launchpad.backend.helper.Utils;
import se.haxtrams.launchpad.backend.model.domain.settings.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

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

    public void processAllFilesIn(final String directory, final Boolean recursive, Consumer<File> processor) {
        processAllFilesIn(new File(directory), recursive, processor);
    }

    private void processAllFilesIn(final File directory, final Boolean recursive, Consumer<File> processor) {
        Arrays.stream(Utils.deNullify(directory.listFiles(), new File[0])).forEach(file -> {
            if (file.isFile()) {
                processor.accept(file);
            } else if (recursive && file.isDirectory()) {
                processAllFilesIn(file, true, processor);
            }
        });
    }
}
