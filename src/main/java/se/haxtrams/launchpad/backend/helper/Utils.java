package se.haxtrams.launchpad.backend.helper;

import java.util.Objects;

public class Utils {
    /**
     * @return input or default value if null.
     */
    public static <T> T deNullify(final T input, final T defaultValue) {
        return Objects.nonNull(input) ? input : defaultValue;
    }

    /**
     * Removes junk text from filenames
     * @return clean string
     */
    public static String cleanupFileName(final String input) {
        return input.replaceAll("(s*\\[[^]]*]|\\.|_|-\\w+)", " ")
            .trim()
            .replaceAll(" +", " ");//cleanup double whitespace
    }
}
