package com.hedera.cli.hedera.keygen;

public class EDKeyChain implements KeyChain {
    private Reference hgcSeed;

    public EDKeyChain(Reference seed){
        hgcSeed = seed;
    }

    @Override
    public KeyPair keyAtIndex(long index) {
        byte[] edSeed = CryptoUtils.deriveKey(hgcSeed.toBytes(), index, 32);
        EDKeyPair pair = new EDKeyPair(edSeed);
        return pair;
    }
}
