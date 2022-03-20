package se.haxtrams.launchpad.backend.controller.api.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.haxtrams.launchpad.backend.converter.ApiConverter;
import se.haxtrams.launchpad.backend.exceptions.NotFoundException;
import se.haxtrams.launchpad.backend.exceptions.NotSupportedException;
import se.haxtrams.launchpad.backend.integration.video.player.VideoPlayer;
import se.haxtrams.launchpad.backend.model.api.response.video.VideoFileResponse;
import se.haxtrams.launchpad.backend.model.api.response.video.player.VideoPlayerExtensionsResponse;
import se.haxtrams.launchpad.backend.model.api.response.video.player.VideoPlayerStateResponse;
import se.haxtrams.launchpad.backend.service.VideoService;

import java.util.Optional;

import static se.haxtrams.launchpad.backend.helper.ResponseHelper.createSimpleResponse;

@RestController
@RequestMapping("/api/v1/video/player")
public class VideoPlayerController {
    private final VideoPlayer videoPlayer;
    private final VideoService videoService;
    private final ApiConverter apiConverter;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public VideoPlayerController(VideoPlayer videoPlayer, VideoService videoService, ApiConverter apiConverter) {
        this.videoPlayer = videoPlayer;
        this.videoService = videoService;
        this.apiConverter = apiConverter;
    }

    @GetMapping
    public VideoPlayerStateResponse getVideoPlayerState() {
        return apiConverter.toVideoPlayerStateResponse(videoPlayer);
    }

    @PostMapping("/open/{id}")
    public VideoFileResponse openVideo(@PathVariable("id") Long id) {
        var video = videoPlayer.openVideo(videoService.findVideoById(id));

        return apiConverter.toVideoFileResponse(video);
    }

    @PostMapping("/open/random")
    public ResponseEntity<VideoFileResponse> openRandomVideo(@RequestParam(value = "filter", required = false) Optional<String> filter) {
        var video = videoService.findRandomVideo(filter.orElse(""));
        videoPlayer.openVideo(video);

        return ResponseEntity.ok(apiConverter.toVideoFileResponse(video));
    }

    @PostMapping("/close")
    public VideoPlayerStateResponse closeVideoPlayer() {
        videoPlayer.closePlayer();
        return apiConverter.toVideoPlayerStateResponse(videoPlayer);
    }

    @GetMapping("/extension")
    public VideoPlayerExtensionsResponse getVideoPlayerExtendedFeatures() {
        return apiConverter.toVideoPlayerFeatureResponse(videoPlayer.getExtendedFeatures());
    }

    @PostMapping("/extension/pause-resume")
    public VideoPlayerStateResponse pauseResume() {
        videoPlayer.pauseResume();
        return apiConverter.toVideoPlayerStateResponse(videoPlayer);
    }

    @PostMapping("/extension/skip-forward")
    public VideoPlayerStateResponse skipForward() {
        videoPlayer.skipForward();
        return apiConverter.toVideoPlayerStateResponse(videoPlayer);
    }

    @PostMapping("/extension/skip-backward")
    public VideoPlayerStateResponse skipBackward() {
        videoPlayer.skipBackward();
        return apiConverter.toVideoPlayerStateResponse(videoPlayer);
    }

    @PostMapping("/extension/cycle-audio-track")
    public VideoPlayerStateResponse cycleAudioTrack() {
        videoPlayer.nextAudioTrack();
        return apiConverter.toVideoPlayerStateResponse(videoPlayer);
    }

    @PostMapping("/extension/cycle-subtitles")
    public VideoPlayerStateResponse cycleSubtitles() {
        videoPlayer.nextSubtitle();
        return apiConverter.toVideoPlayerStateResponse(videoPlayer);
    }

    @PostMapping("/extension/toggle-subtitles")
    public VideoPlayerStateResponse toggleSubtitles() {
        videoPlayer.toggleSubtitles();
        return apiConverter.toVideoPlayerStateResponse(videoPlayer);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleNotSupportedException(NotSupportedException e) {
        log.warn("Request for unsupported feature in VideoPlayerController", e);
        return createSimpleResponse(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        log.warn("File not found request inVideoPlayerController", e);
        return createSimpleResponse(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleException(Exception e) {
        log.error("Unexpected exception inVideoPlayerController", e);
        return createSimpleResponse(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
