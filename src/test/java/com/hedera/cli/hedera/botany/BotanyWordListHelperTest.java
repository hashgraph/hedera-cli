package com.hedera.cli.hedera.botany;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class BotanyWordListHelperTest {
  @Test
  public void shouldBeUniqueList() {
    BotanyWordListHelper b = new BotanyWordListHelper();
    assertNotNull(b);

    List<String> wordList = BotanyWordListHelper.words;
    int wordListSize = wordList.size();

    Set<String> wordListSet = wordList.stream().collect(Collectors.toSet());
    int wordListSetSize = wordListSet.size();

    assertEquals(wordListSize, wordListSetSize);
  }
}