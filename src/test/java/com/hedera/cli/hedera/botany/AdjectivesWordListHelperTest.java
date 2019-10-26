package com.hedera.cli.hedera.botany;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class AdjectivesWordListHelperTest {

    @Test
    public void splitmystring() {
        String string = "zanyzealouszestyzigzag";
        String[] parts = string.split("(?=z)"); // split after the letter
        // String[] parts = string.split("(?<=c)"); // split before the letter

        // insert quotes
        List<String> wordList = new ArrayList<String>(Arrays.asList(parts));
        String res = String.join(",", wordList).replaceAll("([^,]+)", "\"$1\"");
        assertEquals(res, "\"zany\",\"zealous\",\"zesty\",\"zig\",\"zag\"");
    }

    @Test
    public void shouldBeUniqueList() {
        AdjectivesWordListHelper a = new AdjectivesWordListHelper();
        assertNotNull(a);

        List<String> wordList = AdjectivesWordListHelper.words;
        int wordListSize = wordList.size();

        Set<String> wordListSet = wordList.stream().collect(Collectors.toSet());
        int wordListSetSize = wordListSet.size();

        assertEquals(wordListSetSize, wordListSize);
    }
}