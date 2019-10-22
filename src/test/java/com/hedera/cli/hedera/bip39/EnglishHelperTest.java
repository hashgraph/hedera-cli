package com.hedera.cli.hedera.bip39;

import static com.hedera.cli.hedera.bip39.EnglishHelper.words;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class EnglishHelperTest {

  @Test
  public void testEnglishWords() {
    String[] englishWords = words;
    int expected = 2048;
    int actual = englishWords.length;
    assertEquals(expected, actual);
  }
}