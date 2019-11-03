package com.hedera.cli.hedera.keygen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

public class WordListHelperTest {

  @Test
  public void testWordList() {
    WordListHelper w = new WordListHelper();
    assertNotNull(w);

    List<String> wordList = WordListHelper.words;
    int expected = 4096;
    int actual = wordList.size();
    assertEquals(expected, actual);
  }

}