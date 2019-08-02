package com.hedera.cli.hedera.keygen;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface KeyPair {
  byte[] getPrivateKeyBytes();

  byte[] getPublicKeyBytes();

  byte[] signMessage(byte[] message);

  boolean verifySignature(byte[] message, byte[] signature);

  byte[] getSeedAndPublicKey();

  byte[] getPrivateKeyEncoded();

  byte[] getPublicKeyEncoded();

  String getPrivateKeyEncodedHex();

  String getPublicKeyEncodedHex();

  byte[] getPrivateKeySeed();

  String getPrivateKeySeedHex();

  String getPrivateKeyHex();

  String getPublicKeyHex();

  String getSeedAndPublicKeyHex();

  PrivateKey getPrivateKey();

  PublicKey getPublicKey();
}
