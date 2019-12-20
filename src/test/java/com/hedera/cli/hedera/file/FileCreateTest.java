package com.hedera.cli.hedera.file;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

public class FileCreateTest {

    @Test
    public void testFileCreateArgs() {

        @Command class FileCreate {

            @Option(names = {"-d", "--date"}, arity = "0..1")
            private String[] date;

            @Option(names = {"-c", "--contentsString"}, split = " ", arity = "0..*")
            private String[] fileContentsInString;

            @Option(names = {"-s", "--fileSizeByte"})
            private int fileSizeByte;

        }

        FileCreate fc = CommandLine.populateCommand(new FileCreate(), "-d=22-02-2019,21:30:58","-c=\"hello world\"");
        List<String> expectedDate = new ArrayList<>();
        expectedDate.add("22-02-2019,21:30:58");
        List<String> expectedContentString = new ArrayList<>();
        expectedContentString.add("\"hello world\"");
        assertEquals(expectedContentString, Arrays.asList(fc.fileContentsInString));
        assertEquals(0, fc.fileSizeByte);
        assertEquals(expectedDate, Arrays.asList(fc.date));

        CommandLine cmd = new CommandLine(new FileCreate());
//        ParseResult result = cmd.parseArgs("-t=100");
//        assertTrue(result.hasMatchedOption("t"));
//        assertNull(result.subcommand());
//        assertEquals(Integer.valueOf(100), result.matchedOptionValue('t', 100));
//        assertEquals(Collections.singletonList(100), result.matchedOption('t').typedValues());
//        assertEquals(Collections.singletonList("-t=100"), result.originalArgs());
    }

    @Test
    public void testStringArrayToString() {
        String[] testArray = { "Hi", "there" };
        String expectedString = "Hi there";
        FileCreate fileCreate = new FileCreate();
        String actualString = fileCreate.stringArrayToString(testArray);
        assertThat(actualString, containsString(expectedString));
    }

    @Test
    public void testNBytes() {
        int testNBytes = 10;
        String result = String.join("", Collections.nCopies(testNBytes, "A"));
        assertEquals(10, result.getBytes().length);
    }
}