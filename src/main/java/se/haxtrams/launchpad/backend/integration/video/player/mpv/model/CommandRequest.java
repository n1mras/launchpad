package se.haxtrams.launchpad.backend.integration.video.player.mpv.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CommandRequest(@JsonProperty("command") List<Object> cmdArgs) {}
