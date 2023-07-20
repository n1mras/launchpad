package se.haxtrams.launchpad.backend.integration.video.player.mpv.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MpvCommand {
    CYCLE("cycle"),
    SEEK("seek"),
    CLIENT_NAME("client_name"),
    GET_PROPERTY("get_property"),
    SET_PROPERTY("set_property"),
    OBSERVE_PROPERTY("observe_property"),
    OBSERVE_PROPERTY_STRING("observe_property_string"),
    UNOBSERVE_PROPERTY("unobserve_property"),
    REQUEST_LOG_MESSAGES("request_log_messages"),
    ENABLE_EVENT("enable_event"),
    DISABLE_EVENT("disable_event"),
    GET_VERSION("get_version");
    private String value;

    MpvCommand(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
