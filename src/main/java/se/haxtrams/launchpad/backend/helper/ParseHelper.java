package se.haxtrams.launchpad.backend.helper;

import java.util.Objects;

public class ParseHelper {
    public static <T> T deNullify(final T input, final T defaultValue) {
        return Objects.nonNull(input) ? input : defaultValue;
    }
}
