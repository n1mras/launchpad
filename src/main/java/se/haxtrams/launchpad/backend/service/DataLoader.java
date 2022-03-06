package se.haxtrams.launchpad.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import se.haxtrams.launchpad.backend.helper.Utils;
import se.haxtrams.launchpad.backend.model.domain.settings.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public List<File> findAllFilesIn(final String directory, final Boolean recursive) {
        return findAllFilesIn(new File(directory), recursive, new ArrayList<>());
    }

    private List<File> findAllFilesIn(final File directory, final Boolean recursive, List<File> output) {
        Arrays.stream(Utils.deNullify(directory.listFiles(), new File[0])).forEach(file -> {
            if (file.isFile()) {
                output.add(file);
            } else if (recursive && file.isDirectory()) {
                findAllFilesIn(file, true, output);
            }
        });

        return output;
    }
}
