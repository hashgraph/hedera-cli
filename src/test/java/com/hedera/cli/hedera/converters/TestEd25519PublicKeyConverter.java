package com.hedera.cli.hedera.converters;

import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestEd25519PublicKeyConverter {
    Ed25519PublicKeyConverter converter = new Ed25519PublicKeyConverter();

    @Test
    public void validKeyConversion() {
        String hexEncodedkey = "302a300506032b65700321000aa8e21064c61eab86e2a9c164565b4e7a9a4146106e0a6cd03a8c395a110e92";
        Ed25519PublicKey publicKey = converter.convert(hexEncodedkey);
        assertEquals(hexEncodedkey, publicKey.toString());
    }

    @Test
    public void invalidKeyThrowsException() {
        String hexEncodedkey = "302a300506032b65700321000aa8e21064c61eab86e2a9c164565b4e7a9a4146106e0a6cd03a8c395a110e9";
        assertThrows(IllegalArgumentException.class, () -> {
            converter.convert(hexEncodedkey);
        });
    }
}
