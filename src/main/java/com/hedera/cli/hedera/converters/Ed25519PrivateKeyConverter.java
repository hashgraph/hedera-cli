package com.hedera.cli.hedera.converters;

import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class Ed25519PrivateKeyConverter implements Converter<String, Ed25519PrivateKey> {
    @Override
    public Ed25519PrivateKey convert(@NotNull String source) {
        try {
            return Ed25519PrivateKey.fromString(source);
        } catch (Exception e) {
            throw new IllegalArgumentException("Private key is not in the right ED25519 string format");
        }
    }
}
