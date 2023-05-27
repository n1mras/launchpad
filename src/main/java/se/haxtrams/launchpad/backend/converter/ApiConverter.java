package se.haxtrams.launchpad.backend.converter;

import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import se.haxtrams.launchpad.backend.integration.video.player.ExtendedFeatures;
import se.haxtrams.launchpad.backend.integration.video.player.VideoPlayer;
import se.haxtrams.launchpad.backend.model.api.response.PageResponse;
import se.haxtrams.launchpad.backend.model.api.response.video.VideoFileResponse;
import se.haxtrams.launchpad.backend.model.api.response.video.player.VideoPlayerExtensionsResponse;
import se.haxtrams.launchpad.backend.model.api.response.video.player.VideoPlayerStateResponse;
import se.haxtrams.launchpad.backend.model.domain.VideoFile;

@Component
public class ApiConverter {

    public VideoPlayerStateResponse toVideoPlayerStateResponse(VideoPlayer videoPlayer) {
        return new VideoPlayerStateResponse(videoPlayer.getName());
    }

    public VideoPlayerExtensionsResponse toVideoPlayerFeatureResponse(final Set<ExtendedFeatures> extensions) {
        return new VideoPlayerExtensionsResponse(
                extensions.contains(ExtendedFeatures.PAUSE_RESUME),
                extensions.contains(ExtendedFeatures.SKIP_FORWARD),
                extensions.contains(ExtendedFeatures.SKIP_BACKWARD),
                extensions.contains(ExtendedFeatures.NEXT_AUDIO_TRACK),
                extensions.contains(ExtendedFeatures.NEXT_SUBTITLE),
                extensions.contains(ExtendedFeatures.TOGGLE_SUBTITLES));
    }

    public VideoFileResponse toVideoFileResponse(final VideoFile videoFile) {
        return new VideoFileResponse(videoFile.id(), videoFile.name(), videoFile.fileName());
    }

    public <T> PageResponse<T> toPageResponse(Page<T> input) {
        return new PageResponse<>(input.getContent(), input.getNumber(), input.getTotalPages(), input.isEmpty());
    }
}
