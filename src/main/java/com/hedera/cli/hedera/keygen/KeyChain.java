package com.hedera.cli.hedera.keygen;

public interface KeyChain {
    KeyPair keyAtIndex(long index);
}