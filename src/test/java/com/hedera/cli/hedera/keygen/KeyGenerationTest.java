package com.hedera.cli.hedera.keygen;

import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;

public class KeyGenerationTest {

    @Test
    public void testHGCSeedAndEntropyKeyGen() {

        // Generate new keys
        KeyGeneration keyGeneration = new KeyGeneration();
        HGCSeed hgcSeed = new HGCSeed((CryptoUtils.getSecureRandomData(32)));
        List<String> mnemonic = keyGeneration.generateMnemonic(hgcSeed);
        KeyPair keypair = keyGeneration.generateKeysAndWords(hgcSeed);

        int index = 0;
        // Compare with entropy
        byte[] entropy = keyGeneration.entropyFromMnemonic(mnemonic);
        List<String> compareMnemonic = keyGeneration.compareMnemonicFromEntropy(entropy);
        EDKeyPair compareKeyPair = keyGeneration.compareKeyGenWithEntropy(entropy, index);

        // Compare
        Assert.assertEquals(mnemonic, compareMnemonic);
        Assert.assertEquals(Arrays.toString(hgcSeed.getEntropy()), Arrays.toString(entropy));
        Assert.assertEquals(keypair.getPrivateKeyEncodedHex(), compareKeyPair.getPrivateKeyEncodedHex());
        Assert.assertEquals(keypair.getPrivateKeyEncodedHex(), compareKeyPair.getPrivateKeyEncodedHex());
        Assert.assertEquals(keypair.getPublicKeyEncodedHex(), compareKeyPair.getPublicKeyEncodedHex());
        Assert.assertEquals(keypair.getPrivateKeyHex(), compareKeyPair.getPrivateKeyHex());
        Assert.assertEquals(keypair.getPublicKeyHex(), compareKeyPair.getPublicKeyHex());
        Assert.assertEquals(keypair.getSeedAndPublicKeyHex(), compareKeyPair.getSeedAndPublicKeyHex());
        Assert.assertEquals(keypair.getPrivateKeySeedHex(),keypair.getSeedAndPublicKeyHex().substring(0,64),keypair.getPrivateKeyHex());
        Assert.assertEquals(compareKeyPair.getPrivateKeySeedHex(),compareKeyPair.getSeedAndPublicKeyHex().substring(0,64),compareKeyPair.getPrivateKeyHex());

    }
}
