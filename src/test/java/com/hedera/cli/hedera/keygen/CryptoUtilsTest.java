package com.hedera.cli.hedera.keygen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hedera.cli.hedera.bip39.Mnemonic;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicChecksumException;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicLengthException;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicWordException;

import org.junit.jupiter.api.Test;

public class CryptoUtilsTest {

  @Test
  public void cryptoUtils() {
    CryptoUtils c = new CryptoUtils();
    assertNotNull(c);
  }

  @Test
  public void testGetSecureRandomData() {
    int testLength = 10;
    byte[] randomBytes = CryptoUtils.getSecureRandomData(testLength);

    int expected = testLength;
    int actual = randomBytes.length;
    assertEquals(expected, actual);
  }

  @Test
  public void testSha256Digest() throws NoSuchAlgorithmException {
    int expectedLength = 32;

    byte[] message;
    byte[] hash;

    try {
      message = "hello".getBytes();
      hash = CryptoUtils.shaDigest(message, "SHA-256");
      int testString1Length = hash.length;
      assertEquals(expectedLength, testString1Length);
    } catch (NoSuchAlgorithmException e) {
      throw new NoSuchAlgorithmException(e.getMessage());
    }

    try {
      message = "hedera hashgraph".getBytes();
      hash = CryptoUtils.shaDigest(message, "SHA-256");
      int testString2Length = hash.length;
      assertEquals(expectedLength, testString2Length);
    } catch (NoSuchAlgorithmException e) {
      throw new NoSuchAlgorithmException(e.getMessage());
    }

    try {
      message = "this is the fastest, fairest and most secure consensus distributed ledger".getBytes();
      hash = CryptoUtils.shaDigest(message, "SHA-256");
      int testString3Length = hash.length;
      assertEquals(expectedLength, testString3Length);
    } catch (NoSuchAlgorithmException e) {
      throw new NoSuchAlgorithmException(e.getMessage());
    }

  }

  @Test
  public void testSha384Digest() throws NoSuchAlgorithmException {
    int expectedLength = 48;

    byte[] message;
    byte[] hash;

    try {
      message = "hello".getBytes();
      hash = CryptoUtils.shaDigest(message, "SHA-384");
      int testString1Length = hash.length;
      assertEquals(expectedLength, testString1Length);
    } catch (NoSuchAlgorithmException e) {
      throw new NoSuchAlgorithmException(e.getMessage());
    }

    try {
      message = "hedera hashgraph".getBytes();
      hash = CryptoUtils.shaDigest(message, "SHA-384");
      int testString2Length = hash.length;
      assertEquals(expectedLength, testString2Length);
    } catch (NoSuchAlgorithmException e) {
      throw new NoSuchAlgorithmException(e.getMessage());
    }

    try {
      message = "this is the fastest, fairest and most secure consensus distributed ledger".getBytes();
      hash = CryptoUtils.shaDigest(message, "SHA-384");
      int testString3Length = hash.length;
      assertEquals(expectedLength, testString3Length);
    } catch (NoSuchAlgorithmException e) {
      throw new NoSuchAlgorithmException(e.getMessage());
    }
  }

  @Test
  public void testNotSupportedHashAlgo() {
    assertThrows(NoSuchAlgorithmException.class, () -> {
      byte[] message = "hello".getBytes();;
      CryptoUtils.shaDigest(message, "SHA-512");
    });

    assertThrows(NoSuchAlgorithmException.class, () -> {
      byte[] message = "hello".getBytes();;
      CryptoUtils.shaDigest(message, "NO SUCH ALGO");
    });
  }

  @Test
  public void testDeriveKey() {

    Mnemonic m = new Mnemonic();
    // test data (recovery phrase list)
    List<String> phraseList = Stream.of("opera", "critic", "capital", "genre", "soda", "glimpse", "isolate", "mistake",
        "hobby", "nest", "waste", "beef", "under", "august", "either", "face", "home", "seek", "bike", "swear", "diet",
        "body", "skirt", "charge").collect(Collectors.toList());

    try {
      byte[] entropy = m.toEntropy(phraseList);
      int index = 0;
      int length = 32;
      byte[] seed = CryptoUtils.deriveKey(entropy, index, length);
      assertNotNull(seed);
      assertEquals(32, seed.length);
    } catch (MnemonicLengthException | MnemonicWordException | MnemonicChecksumException e) {
      e.printStackTrace();
    }

  }

}