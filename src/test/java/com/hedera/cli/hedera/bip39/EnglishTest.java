package com.hedera.cli.hedera.bip39;

import static com.hedera.cli.hedera.bip39.English.words;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EnglishTest {

  @Test
  public void testEnglishWords() {
    String[] englishWords = words;
    int expected = 2048;
    int actual = englishWords.length;
    assertEquals(expected, actual);
  }
}