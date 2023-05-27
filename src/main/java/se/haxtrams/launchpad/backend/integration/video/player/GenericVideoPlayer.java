package se.haxtrams.launchpad.backend.integration.video.player;

import java.io.IOException;
import java.util.Set;
import se.haxtrams.launchpad.backend.model.domain.VideoFile;
import se.haxtrams.launchpad.backend.model.domain.settings.VideoSettings;

public class GenericVideoPlayer extends VideoPlayer {
    public GenericVideoPlayer(VideoSettings videoSettings) {
        super(videoSettings);
    }

    @Override
    public String getName() {
        return "Generic player";
    }

    @Override
    public Set<ExtendedFeatures> getExtendedFeatures() {
        return Set.of();
    }

    @Override
    public VideoFile openVideo(VideoFile videoFile) {
        lockProcessAndExecute(process -> {
            try {
                process.filter(Process::isAlive).ifPresent(VideoPlayer::killVideoProcess);

                this.videoProcess = new ProcessBuilder(buildLaunchCommand(videoFile))
                        .inheritIO()
                        .start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return videoFile;
    }

    @Override
    public void closePlayer() {
        lockProcessAndExecuteIfAlive(VideoPlayer::killVideoProcess);
    }
}
