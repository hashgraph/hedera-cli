package com.hedera.cli.hedera.keygen;

import com.hedera.cli.hedera.bip39.Mnemonic;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.ShortBufferException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class EDBip32KeyChain implements KeyChain {
    private HGCSeed hgcSeed;

    public EDBip32KeyChain() {

    }

    public EDBip32KeyChain(HGCSeed seed) {
        hgcSeed = seed;
    }

    @Override
    public KeyPair keyAtIndex(long index) {
        List<String> wordList = hgcSeed.toWordsList();
        KeyPair kp = keyPairFromWordList(index, wordList);
        return kp;
    }

    public byte[] bipSeed(String words) {
        byte[] bipSeed = Mnemonic.generateBipSeed(words, "");
        return bipSeed;
    }

    public KeyPair keyPairFromWordList(long index, List<String> wordList) {
        String words = StringUtils.join(wordList, " ");
        byte[] bipSeed = bipSeed(words);
        int i=(int)index;
        int[] array = {44, 3030, 0, 0, i};
        byte[] ckd = new byte[0];
        try {
            ckd = Slip10Utils.deriveEd25519PrivateKey(bipSeed, array);
        } catch (NoSuchAlgorithmException | ShortBufferException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return new EDKeyPair(ckd);
    }
}
