package com.hedera.cli.hedera.keygen;

public class EDKeyChain implements KeyChain {
    private HGCSeed hgcSeed;

    public EDKeyChain(HGCSeed seed){
        hgcSeed = seed;
    }

    @Override
    public KeyPair keyAtIndex(long index) {
        byte[] edSeed = CryptoUtils.deriveKey(hgcSeed.getEntropy(), index, 32);
        EDKeyPair pair = new EDKeyPair(edSeed);
        return pair;
    }
}
