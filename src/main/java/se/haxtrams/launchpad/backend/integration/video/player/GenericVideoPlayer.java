package se.haxtrams.launchpad.backend.integration.video.player;

import java.util.Set;
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
}
