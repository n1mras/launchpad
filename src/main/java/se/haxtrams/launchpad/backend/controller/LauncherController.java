package se.haxtrams.launchpad.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.haxtrams.launchpad.backend.exceptions.domain.NotFoundException;
import se.haxtrams.launchpad.backend.service.SystemService;
import se.haxtrams.launchpad.backend.service.VideoService;

import static se.haxtrams.launchpad.backend.helper.ResponseHelper.createSimpleResponse;

@RestController
@RequestMapping("/launcher")
public class LauncherController {
    private final VideoService videoService;
    private final SystemService systemService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public LauncherController(VideoService videoService, SystemService systemService) {
        this.videoService = videoService;
        this.systemService = systemService;
    }

    @PostMapping("/video/{id}")
    public ResponseEntity<String> launchVideo(@PathVariable("id") Long id) {
        var video = videoService.findVideoById(id);
        systemService.openVideo(video);

        return createSimpleResponse(HttpStatus.OK);
    }

    @PostMapping("/video/{id}/directory")
    public ResponseEntity<String> launchVideoDirectory(@PathVariable("id") Long id) {
        var video = videoService.findVideoById(id);
        systemService.openFileLocation(video);

        return createSimpleResponse(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        log.warn("File not found request in Launcher controller", e);
        return createSimpleResponse(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleException(Exception e) {
        log.error("Unexpected exception in Launcher controller", e);
        return createSimpleResponse(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
