package com.hedera.cli.hedera.converters;

import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestEd25519PrivateKeyConverter {
    Ed25519PrivateKeyConverter converter = new Ed25519PrivateKeyConverter();

    @Test
    public void validKeyConversion() {
        String hexEncodedkey = "302e020100300506032b6570042204205f66a51931e8c99089472e0d70516b6272b94dd772b967f8221e1077f966dbda";
        Ed25519PrivateKey privateKey = converter.convert(hexEncodedkey);
        assertEquals(hexEncodedkey, privateKey.toString());
    }

    @Test
    public void invalidKeyThrowsException() {
        String hexEncodedkey = "302e020100300506032b6570042204205f66a51931e8c99089472e0d70516b6272b94dd772b967f8221e1077f966dbd";
        assertThrows(IllegalArgumentException.class, () -> {
            converter.convert(hexEncodedkey);
        });
    }
}
