package de.vsc.coi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class VersionCheckerTest {

    //@formatter:off
    @ParameterizedTest
    @CsvSource({"1.2, 1.2, false",
             "1.2.1, 1.2, true",
             "1.2, 1.2.1, false",
             "1.2, 1.2, false",
             "1.3.1, 1.1.1, true",
             "1.2.9, 1.3, false",
             "1.19, 1.18, true",
             "1.18, 1.19, false",
             "1.19.1-SNAPSHOT, 1.19, true",
             "1.19, 1.19.1-SNAPSHOT, false"})
    //@formatter:on
    void needsUpdate(final String latest, final String current, final boolean result) {
        assertEquals(VersionChecker.needsUpdate(latest, current), result);
    }
}