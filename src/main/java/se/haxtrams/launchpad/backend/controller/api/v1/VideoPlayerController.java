package se.haxtrams.launchpad.backend.controller.api.v1;

import static se.haxtrams.launchpad.backend.util.ResponseUtil.createSimpleResponse;

import java.util.Optional;
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
import se.haxtrams.launchpad.backend.service.VideoLibraryService;

@RestController
@RequestMapping("/api/v1/video/player")
public class VideoPlayerController {
    private final VideoPlayer videoPlayer;
    private final VideoLibraryService videoService;
    private final ApiConverter apiConverter;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public VideoPlayerController(VideoPlayer videoPlayer, VideoLibraryService videoService, ApiConverter apiConverter) {
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
    public ResponseEntity<VideoFileResponse> openRandomVideo(
            @RequestParam(value = "filter", required = false) Optional<String> filter) {
        var video = videoService.findRandomVideo(filter.orElse(""));
        videoPlayer.openVideo(video);

        return ResponseEntity.ok(apiConverter.toVideoFileResponse(video));
    }

    @PostMapping("/close")
    public ResponseEntity<String> closeVideoPlayer() {
        videoPlayer.closePlayer();
        return createSimpleResponse(HttpStatus.OK);
    }

    @GetMapping("/extension")
    public VideoPlayerExtensionsResponse getVideoPlayerExtendedFeatures() {
        return apiConverter.toVideoPlayerFeatureResponse(videoPlayer.getExtendedFeatures());
    }

    @PostMapping("/extension/pause-resume")
    public ResponseEntity<String> pauseResume() {
        videoPlayer.pauseResume();
        return createSimpleResponse(HttpStatus.OK);
    }

    @PostMapping("/extension/skip-forward")
    public ResponseEntity<String> skipForward() {
        videoPlayer.skipForward();
        return createSimpleResponse(HttpStatus.OK);
    }

    @PostMapping("/extension/skip-backward")
    public ResponseEntity<String> skipBackward() {
        videoPlayer.skipBackward();
        return createSimpleResponse(HttpStatus.OK);
    }

    @PostMapping("/extension/cycle-audio-track")
    public ResponseEntity<String> cycleAudioTrack() {
        videoPlayer.nextAudioTrack();
        return createSimpleResponse(HttpStatus.OK);
    }

    @PostMapping("/extension/cycle-subtitles")
    public ResponseEntity<String> cycleSubtitles() {
        videoPlayer.nextSubtitle();
        return createSimpleResponse(HttpStatus.OK);
    }

    @PostMapping("/extension/toggle-subtitles")
    public ResponseEntity<String> toggleSubtitles() {
        videoPlayer.toggleSubtitles();
        return createSimpleResponse(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleNotSupportedException(NotSupportedException e) {
        log.warn("Request for unsupported feature in VideoPlayerController");
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
