package se.haxtrams.launchpad.backend.controller;

import org.jeasy.random.EasyRandom;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.haxtrams.launchpad.backend.model.domain.settings.Settings;

@RestController
@RequestMapping("/media")
public class MediaController {


    @GetMapping("/test")
    public Object test() {
        return new EasyRandom().nextObject(Settings.class);
    }
}
