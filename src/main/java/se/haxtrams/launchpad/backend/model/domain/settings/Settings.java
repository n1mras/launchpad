package se.haxtrams.launchpad.backend.model.domain.settings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Settings {
    private final VideoSettings videoSettings;

    @JsonCreator
    public Settings(@JsonProperty("videoSettings") VideoSettings videoSettings) {
        this.videoSettings = videoSettings;
    }

    public VideoSettings getVideoSettings() {
        return videoSettings;
    }
}
