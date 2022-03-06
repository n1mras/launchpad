package se.haxtrams.launchpad.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.haxtrams.launchpad.backend.model.domain.MediaFile;
import se.haxtrams.launchpad.backend.model.domain.VideoFile;
import se.haxtrams.launchpad.backend.model.domain.settings.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SystemService {
    private final Settings settings;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Process videoProcess;

    public SystemService(Settings settings) {
        this.settings = settings;
    }

    public synchronized void openVideo(final VideoFile videoFile) {
        try {
            final var videoSettings = settings.getVideoSettings();
            final List<String> cmd = new ArrayList<>();
            cmd.add(videoSettings.getPath());
            cmd.addAll(videoSettings.getArgs());
            cmd.add(videoFile.filePath());

            if (Objects.nonNull(videoProcess)) {
                log.info("Killing previous video process");
                videoProcess.destroy();
                videoProcess = null;
            }

            videoProcess = new ProcessBuilder(cmd).start();
        } catch (Exception e) {
            throw new RuntimeException("Error while launching video app", e);
        }
    }

    public void openFileLocation(final MediaFile mediaFile) {
        try {
            var directory = mediaFile.fileDirectory();
            var cmd = List.of("/usr/bin/xdg-open", directory);
            log.info(String.format("Opening directory: %s", directory));

            new ProcessBuilder(cmd).start();
        } catch (Exception e) {
            throw new RuntimeException("Could not open directory", e);
        }
    }

}
