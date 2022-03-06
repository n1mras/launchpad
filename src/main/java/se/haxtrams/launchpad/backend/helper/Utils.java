package se.haxtrams.launchpad.backend.helper;

import java.util.Objects;

public class Utils {
    public static <T> T deNullify(final T input, final T defaultValue) {
        return Objects.nonNull(input) ? input : defaultValue;
    }

    public static String cleanupFileName(final String input) {
        //replace . with space and remove brackets
        return input.replaceAll("(s*\\[[^]]*]|\\.|_)", " ")
            .trim()
            .replaceAll(" +", " ");//cleanup double whitespace
    }
}
