package com.hedera.cli.hedera.file;

import org.junit.Test;
import picocli.CommandLine;
import picocli.CommandLine.MutuallyExclusiveArgsException;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class FileCreateTest {

    @Test
    public void testFileCreate() {

        class FileCreate {
            @Option(names = { "-d", "--date" }, arity = "0..2")
            private String[] date;

            @Option(names = { "-t", "--maxTransactionFee" })
            private int maxTransactionFee;

            @Option(names = {"-c", "--contentsString"}, split = " ", arity = "0..*")
            private String[] fileContentsInString;

            @Option(names = {"-s", "--fileSizeByte"})
            private int fileSizeByte;
        }
         FileCreate fileCreate = new FileCreate();
         CommandLine cmd = new CommandLine(fileCreate);
         ParseResult result = cmd.parseArgs("-t","100");
         assertTrue(result.hasMatchedOption("t"));
         FileCreate fc = cmd.getCommand();
         assertEquals(100,fc.maxTransactionFee);
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
