package com.hedera.cli.hedera.keygen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
  public void testGetSecureRandomData() {
    int testLength = 10;
    byte[] randomBytes = CryptoUtils.getSecureRandomData(testLength);

    int expected = testLength;
    int actual = randomBytes.length;
    assertEquals(expected, actual);
  }

  @Test
  public void testSha256Digest() {
    int expectedLength = 32;

    byte[] message = "hello".getBytes();
    byte[] hash = CryptoUtils.sha256Digest(message);
    int testString1Length = hash.length;
    assertEquals(expectedLength, testString1Length);

    message = "hedera hashgraph".getBytes();
    hash = CryptoUtils.sha256Digest(message);
    int testString2Length = hash.length;
    assertEquals(expectedLength, testString2Length);

    message = "this is the fastest, fairest and most secure consensus distributed ledger".getBytes();
    hash = CryptoUtils.sha256Digest(message);
    int testString3Length = hash.length;
    assertEquals(expectedLength, testString3Length);
  }

  @Test
  public void testSha384Digest() {
    int expectedLength = 48;

    byte[] message = "hello".getBytes();
    byte[] hash = CryptoUtils.sha384Digest(message);
    int testString1Length = hash.length;
    assertEquals(expectedLength, testString1Length);

    message = "hedera hashgraph".getBytes();
    hash = CryptoUtils.sha384Digest(message);
    int testString2Length = hash.length;
    assertEquals(expectedLength, testString2Length);

    message = "this is the fastest, fairest and most secure consensus distributed ledger".getBytes();
    hash = CryptoUtils.sha384Digest(message);
    int testString3Length = hash.length;
    assertEquals(expectedLength, testString3Length);
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