package se.haxtrams.launchpad.backend.controller.api.v1;

import static se.haxtrams.launchpad.backend.util.ResponseUtil.createSimpleResponse;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.haxtrams.launchpad.backend.converter.ApiConverter;
import se.haxtrams.launchpad.backend.exceptions.NotFoundException;
import se.haxtrams.launchpad.backend.model.api.response.PageResponse;
import se.haxtrams.launchpad.backend.model.api.response.video.VideoFileResponse;
import se.haxtrams.launchpad.backend.service.SyncService;
import se.haxtrams.launchpad.backend.service.SystemService;
import se.haxtrams.launchpad.backend.service.VideoLibraryService;

@RestController
@RequestMapping("/api/v1/library/video")
public class VideoLibraryController {
    private final VideoLibraryService videoService;
    private final SyncService syncService;
    private final SystemService systemService;
    private final ApiConverter apiConverter;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public VideoLibraryController(
            VideoLibraryService videoLibraryService,
            SyncService syncService,
            SystemService systemService,
            ApiConverter apiConverter) {
        this.videoService = videoLibraryService;
        this.syncService = syncService;
        this.systemService = systemService;
        this.apiConverter = apiConverter;
    }

    @GetMapping
    public PageResponse<VideoFileResponse> getVideoFiles(
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size,
            @RequestParam(name = "filter", required = false) Optional<String> filterName) {
        final var filter = filterName.orElse("");
        final var pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        final var result = videoService.findVideosWithName(filter, pageRequest).map(apiConverter::toVideoFileResponse);

        return apiConverter.toPageResponse(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshVideoLibrary() {
        CompletableFuture.runAsync(syncService::sync);
        return createSimpleResponse(HttpStatus.ACCEPTED);
    }

    @PostMapping("/{id}/location")
    public ResponseEntity<VideoFileResponse> launchVideoLocation(@PathVariable("id") Long id) {
        var video = videoService.findVideoById(id);
        systemService.openFileLocation(video);

        return ResponseEntity.ok(apiConverter.toVideoFileResponse(video));
    }

    @ExceptionHandler
    private ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        log.warn("File not found request in Launcher controller", e);
        return createSimpleResponse(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception e) {
        log.error("unexpected exception in media controller", e);
        return createSimpleResponse(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
