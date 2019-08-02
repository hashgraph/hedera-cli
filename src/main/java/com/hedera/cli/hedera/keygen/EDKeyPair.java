package com.hedera.cli.hedera.keygen;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import org.bouncycastle.util.encoders.Hex;

public class EDKeyPair implements KeyPair {

  private EdDSAPrivateKey privateKey;
  private EdDSAPublicKey publicKey;
  // private byte[] seed;

  private EDKeyPair() {

  }

  public EDKeyPair(byte[] seed) {
    // this.seed = seed;
    EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
    EdDSAPrivateKeySpec privateKeySpec = new EdDSAPrivateKeySpec(seed, spec);
    this.privateKey = new EdDSAPrivateKey(privateKeySpec);
    EdDSAPublicKeySpec pubKeySpec = new EdDSAPublicKeySpec(privateKeySpec.getA(), spec);
    this.publicKey = new EdDSAPublicKey(pubKeySpec);
  }

  public static EDKeyPair buildFromPrivateKey(final byte[] privateKeyBytes) {
    final EDKeyPair edKeyPair = new EDKeyPair();
    try {
      // try encoded first
      final PKCS8EncodedKeySpec encodedPrivKey = new PKCS8EncodedKeySpec(privateKeyBytes);
      edKeyPair.privateKey = new EdDSAPrivateKey(encodedPrivKey);
    } catch (InvalidKeySpecException e) {
      // key is invalid (likely not encoded)
      // try non encoded
      final EdDSAPrivateKeySpec privKey = new EdDSAPrivateKeySpec(privateKeyBytes,
          EdDSANamedCurveTable.ED_25519_CURVE_SPEC);
      edKeyPair.privateKey = new EdDSAPrivateKey(privKey);
    }

    EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);

    edKeyPair.publicKey = new EdDSAPublicKey(new EdDSAPublicKeySpec(edKeyPair.privateKey.getAbyte(), spec));
    return edKeyPair;
  }

  @Override
  public byte[] getSeedAndPublicKey() {
    byte[] seed = this.privateKey.getSeed();
    byte[] publicKey = this.getPublicKeyBytes();

    byte[] key = new byte[seed.length + publicKey.length];
    System.arraycopy(seed, 0, key, 0, seed.length);
    System.arraycopy(publicKey, 0, key, seed.length, publicKey.length);
    return key;
  }

  @Override
  public String getSeedAndPublicKeyHex() {
    return Hex.toHexString(this.getSeedAndPublicKey());
  }

  @Override
  public PrivateKey getPrivateKey() {
    return this.privateKey;
  }

  @Override
  public PublicKey getPublicKey() {
    return this.publicKey;
  }

  @Override
  public byte[] getPrivateKeyBytes() {
    return this.privateKey.geta();
  }

  @Override
  public String getPrivateKeyHex() {
    return Hex.toHexString(this.getPrivateKeyBytes());
  }

  @Override
  public byte[] getPrivateKeySeed() {
    return this.privateKey.getSeed();
  }

  @Override
  public String getPrivateKeySeedHex() {
    return Hex.toHexString(this.getPrivateKeySeed());
  }

  @Override
  public byte[] getPrivateKeyEncoded() {
    return this.privateKey.getEncoded();
  }

  @Override
  public String getPrivateKeyEncodedHex() {
    return Hex.toHexString(this.getPrivateKeyEncoded());
  }

  @Override
  public byte[] getPublicKeyBytes() {
    return this.publicKey.getAbyte();
  }

  @Override
  public String getPublicKeyHex() {
    return Hex.toHexString(this.getPublicKeyBytes());
  }

  @Override
  public byte[] getPublicKeyEncoded() {
    return this.publicKey.getEncoded();
  }

  @Override
  public String getPublicKeyEncodedHex() {
    return Hex.toHexString(this.getPublicKeyEncoded());
  }

  @Override
  public byte[] signMessage(byte[] message) {
    EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
    try {
      Signature sgr = new EdDSAEngine(MessageDigest.getInstance(spec.getHashAlgorithm()));
      sgr.initSign(privateKey);
      sgr.update(message);
      return sgr.sign();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return new byte[0];
  }

  @Override
  public boolean verifySignature(byte[] message, byte[] signature) {
    EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
    try {
      Signature sgr = new EdDSAEngine(MessageDigest.getInstance(spec.getHashAlgorithm()));
      sgr.initVerify(publicKey);
      sgr.update(message);
      return sgr.verify(signature);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
}
