package se.haxtrams.launchpad.backend.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    @SuppressWarnings("ConstantConditions")
    void deNullify() {
        final Boolean isTrue = true;
        final Boolean isNull = null;

        assertTrue(Utils.deNullify(isTrue, false));
        assertFalse(Utils.deNullify(isNull, false));
    }

    @Test
    void cleanupFileName() {
        final var dirtyName = "[Remove] There.once____was.a.maiden.[text123].from.Stonebury     hollow [O12sdfSf - 1234]";
        final var expected = "There once was a maiden from Stonebury hollow";
        final var result = Utils.cleanupFileName(dirtyName);

        assertEquals(expected, result);
    }
}