package com.hedera.cli.hedera.keygen;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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

  }
  
}