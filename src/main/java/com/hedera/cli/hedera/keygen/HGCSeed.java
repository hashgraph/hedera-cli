package com.hedera.cli.hedera.keygen;

import com.hedera.cli.hedera.bip39.Mnemonic;
import com.hedera.cli.hedera.bip39.MnemonicException;
import org.springframework.lang.NonNull;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public class HGCSeed {

    public static int bip39WordListSize = 24;
    private byte[] entropy; // 32 Bytes

    public HGCSeed(byte[] entropy) {
        this.entropy = entropy;
    }

    public HGCSeed(List<String> mnemonic) throws Exception {
        if (mnemonic.size() == HGCSeed.bip39WordListSize) {
            this.entropy = new Mnemonic().toEntropy(mnemonic);
        } else {
            throw new Exception("Invalid word list");
        }
    }

    @NonNull
    public List<String> toWordsList(){
        try {
            return new Mnemonic().toMnemonic(entropy);
        } catch (MnemonicException.MnemonicLengthException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] getEntropy() {
        return entropy;
    }
}
