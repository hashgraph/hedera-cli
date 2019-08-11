package com.hedera.cli.hedera.keygen;

import com.hedera.cli.hedera.bip39.Mnemonic;
import com.hedera.cli.hedera.bip39.MnemonicException;
import org.springframework.lang.NonNull;

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
        } else  {
            Reference reference = new Reference(String.join(" ", mnemonic));
            this.entropy = reference.toBytes();
        }
    }

    @NonNull
    public List<String> toWordsList(){
        try {
            return new Mnemonic().toMnemonic(entropy);
        } catch (MnemonicException.MnemonicLengthException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] getEntropy() {
        return entropy;
    }
}
