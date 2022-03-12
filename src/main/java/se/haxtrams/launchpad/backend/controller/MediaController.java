package se.haxtrams.launchpad.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.haxtrams.launchpad.backend.converter.ApiConverter;
import se.haxtrams.launchpad.backend.model.api.response.PageResponse;
import se.haxtrams.launchpad.backend.model.api.response.VideoFileResponse;
import se.haxtrams.launchpad.backend.service.VideoService;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static se.haxtrams.launchpad.backend.helper.ResponseHelper.createSimpleResponse;

@RestController
@RequestMapping("/api/media")
public class MediaController {
    private final VideoService videoService;
    private final ApiConverter apiConverter;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public MediaController(VideoService videoService, ApiConverter apiConverter) {
        this.videoService = videoService;
        this.apiConverter = apiConverter;
    }

    @GetMapping("/video/file")
    public PageResponse<VideoFileResponse> getVideoFiles(@RequestParam(name = "page") int page,
                                                         @RequestParam(name = "size") int size,
                                                         @RequestParam(name = "filter", required = false) Optional<String> filterName
    ) {
        final var filter = filterName.orElse("");
        final var pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        final var result = videoService.findVideosWithName(filter, pageRequest).map(apiConverter::toVideoFileResponse);

        return apiConverter.toPageResponse(result);
    }

    @PostMapping("/video")
    public ResponseEntity<String> refreshVideoMedia() {
        CompletableFuture.runAsync(videoService::loadFiles);
        return createSimpleResponse(HttpStatus.ACCEPTED);
    }


    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception e) {
        log.error("unexpected exception in media controller", e);
        return createSimpleResponse(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
