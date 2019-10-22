package com.hedera.cli.hedera.keygen;

import com.hedera.cli.hedera.bip39.Mnemonic;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import javax.crypto.ShortBufferException;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class EDBipKeyChainTest {

    @Test
    public void testKeyAtIndex() {
        String words = "draft struggle fitness mimic mountain rare lonely grocery topple wreck satoshi kangaroo balcony odor tiger crush bamboo parent monkey afraid elite earn hundred learn";
        byte[] bipSeed = Mnemonic.generateBipSeed(words, "");
        assertEquals(
                "60691cded1328c5799e36d72aec3842b5230d376ce9b1177b3dc8c79d2d715b099c486fbf91a93ebadcaf473fafa79d5d694c013bcc561c130c447e3f84659f4",
                Hex.toHexString(bipSeed));

        byte[] edSeed = new byte[0];
        try {
            edSeed = Slip10Utils.deriveEd25519PrivateKey(bipSeed, 44, 3030, 0, 0, 0);
        } catch (NoSuchAlgorithmException | ShortBufferException | InvalidKeyException e) {
            e.printStackTrace();
        }
        KeyPair keyPair = new EDKeyPair(edSeed);
        assertEquals("00516a26d75230616da9b18b27fa4d1ce68ca6dbb6db5ee42dc63f35c977310f",
                Hex.toHexString(keyPair.getPublicKeyBytes()));
    }
}
