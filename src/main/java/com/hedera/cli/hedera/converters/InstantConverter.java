package com.hedera.cli.hedera.converters;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class InstantConverter implements Converter<String, Instant> {
    public static final String NOW = "now";

    @Override
    public Instant convert(@NotNull String source) {
        if (source.equalsIgnoreCase(NOW)) {
            return Instant.now();
        }
        try {
            long epochSecond = Long.parseLong(source);
            return Instant.ofEpochSecond(epochSecond);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Could not parse epoch seconds to Instant");
        }
    }
}
