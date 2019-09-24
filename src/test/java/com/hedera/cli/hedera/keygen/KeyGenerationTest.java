package com.hedera.cli.hedera.keygen;

import com.hedera.cli.hedera.bip39.Mnemonic;
import com.hedera.cli.hedera.bip39.MnemonicException;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.ShortBufferException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class KeyGenerationTest {

    // Migration was first initiated 11 September 2019 via Hedera Wallet iOS/Android app
    @Test
    public void testHGCSeedAndEntropyKeyGenPriorToBipMigration() {

        // Generate new keys prior to bip migration
        KeyGeneration keyGeneration = new KeyGeneration();
        HGCSeed hgcSeed = new HGCSeed((CryptoUtils.getSecureRandomData(32)));
        List<String> mnemonic = keyGeneration.generateMnemonic(hgcSeed);
        String hgcMethod = "hgc";
        KeyPair keypair = keyGeneration.generateKeysAndWords(hgcSeed, hgcMethod, mnemonic);

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
        Assert.assertEquals(keypair.getPrivateKeySeedHex(), keypair.getSeedAndPublicKeyHex().substring(0, 64), keypair.getPrivateKeyHex());
        Assert.assertEquals(compareKeyPair.getPrivateKeySeedHex(), compareKeyPair.getSeedAndPublicKeyHex().substring(0, 64), compareKeyPair.getPrivateKeyHex());
    }

    @Test
    public void testHGCSeedAndEntropyKeyGenAfterBipMigration() throws NoSuchAlgorithmException, InvalidKeyException, ShortBufferException, MnemonicException.MnemonicChecksumException, MnemonicException.MnemonicLengthException, MnemonicException.MnemonicWordException {
        // Generate new keys post bip migration
        KeyGeneration keyGeneration = new KeyGeneration();
        // Create new HGCSeed
        HGCSeed hgcSeed = new HGCSeed((CryptoUtils.getSecureRandomData(32)));
        List<String> mnemonic = keyGeneration.generateMnemonic(hgcSeed);
        // Generate mnemonics using HGCSeed
//        List<String> mnemonic = Arrays.asList("arrest", "insect", "jump", "unfair", "reject", "tell", "denial", "tilt", "visual", "fortune", "car", "tail", "offer", "radio", "stomach", "relief", "push", "purpose", "track", "wild", "tennis", "client", "zone", "float");
        EDBip32KeyChain kc2 = new EDBip32KeyChain();
        KeyPair kp2 = kc2.keyPairFromWordList(0, mnemonic);

        String bipMethod = "bip";
        // Use Mnemonic to feed as "password" to derive bipSeed
        KeyPair keypair = keyGeneration.generateKeysAndWords(hgcSeed, bipMethod, mnemonic);
        Assert.assertEquals(kp2.getPrivateKeyHex(), keypair.getPrivateKeyHex());
        Assert.assertEquals(kp2.getPublicKeyHex(), keypair.getPublicKeyHex());

        int index = 0;
        // at this point, entropy from mnemonic would not be using the current way of handling.
        Mnemonic mnemonicClass = new Mnemonic();
        byte[] bipEntropyFromMnemonic = mnemonicClass.toEntropy(mnemonic);

        // this is the entropy from the second seeding with bip39 compatiple passphrase and salt
        List<String> compareMnemonic = keyGeneration.compareMnemonicFromBipEntropy(bipEntropyFromMnemonic);
        EDKeyPair compareKeyPair = keyGeneration.compareKeyGenWithBipMnemonicFromHGCSeed(compareMnemonic, hgcSeed, index);

        Assert.assertEquals(mnemonic, compareMnemonic);
        Assert.assertEquals(keypair.getPrivateKeyEncodedHex(), compareKeyPair.getPrivateKeyEncodedHex());
        Assert.assertEquals(keypair.getPrivateKeyEncodedHex(), compareKeyPair.getPrivateKeyEncodedHex());
        Assert.assertEquals(keypair.getPublicKeyEncodedHex(), compareKeyPair.getPublicKeyEncodedHex());
        Assert.assertEquals(keypair.getPrivateKeyHex(), compareKeyPair.getPrivateKeyHex());
        Assert.assertEquals(keypair.getPublicKeyHex(), compareKeyPair.getPublicKeyHex());
    }
}
