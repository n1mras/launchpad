package se.haxtrams.launchpad.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/media")
public class MediaController {


    @GetMapping("/test")
    public Object test() {
        return "Hello World!";
    }
}
