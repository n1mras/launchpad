package se.haxtrams.launchpad.backend.model.api.response.video.player;

public record VideoPlayerExtensionsResponse(
        boolean pauseResume,
        boolean skipForward,
        boolean skipBackward,
        boolean cycleAudioTrack,
        boolean cycleSubtitles,
        boolean toggleSubtitles) {}
