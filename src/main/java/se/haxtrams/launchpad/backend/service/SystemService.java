package se.haxtrams.launchpad.backend.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.haxtrams.launchpad.backend.integration.video.player.VideoPlayer;
import se.haxtrams.launchpad.backend.integration.video.player.mplayer.MPlayer;
import se.haxtrams.launchpad.backend.model.domain.MediaFile;
import se.haxtrams.launchpad.backend.model.domain.settings.Settings;

@Service
public class SystemService {
    private final Settings settings;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final VideoPlayer videoPlayer;

    public SystemService(Settings settings) {
        this.settings = settings;
        this.videoPlayer = new MPlayer(settings.getVideoSettings());
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
