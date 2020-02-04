package com.hedera.cli.hedera.convertors;

import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class Ed25519PublicKeyConverter implements Converter<String, Ed25519PublicKey> {
    @Override
    public Ed25519PublicKey convert(@NotNull String source) {
        try {
            return Ed25519PublicKey.fromString(source);
        } catch (Exception e) {
            throw new IllegalArgumentException("Public key is not in the right ED25519 string format");
        }
    }
}
