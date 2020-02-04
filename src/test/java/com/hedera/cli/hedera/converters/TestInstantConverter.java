package com.hedera.cli.hedera.converters;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestInstantConverter {
    InstantConverter converter = new InstantConverter();

    @Test
    public void validInstantConversion() {
        long epochSecond = 1234512345;
        Instant instant = converter.convert("" + epochSecond);
        assertEquals(epochSecond, instant.getEpochSecond());
    }

    @Test
    public void validNowConversion() {
        Instant expected = Instant.now();
        Instant actual = converter.convert(InstantConverter.NOW);
        assertEquals(expected.getEpochSecond(), actual.getEpochSecond());
    }

    @Test
    public void invalidInstantThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            converter.convert("xyz");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            converter.convert("01-01-2000T10:00:00");
        });
    }
}
