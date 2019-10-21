package com.hedera.cli.hedera.botany;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class AdjectivesWordListHelperTest {

    @Test
    public void splitmystring() {
        String string = "zanyzealouszestyzigzag";
        String[] parts = string.split("(?=z)"); // split after the letter
//        String[] parts = string.split("(?<=c)"); // split before the letter

        // insert quotes
        List<String> wordList = new ArrayList<String>(Arrays.asList(parts));
        String res = String.join(",", wordList).replaceAll("([^,]+)", "\"$1\"");
        assertEquals(res, "\"zany\",\"zealous\",\"zesty\",\"zig\",\"zag\"");
    }
}