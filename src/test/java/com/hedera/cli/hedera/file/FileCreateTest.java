package com.hedera.cli.hedera.file;

import org.junit.Test;
import picocli.CommandLine;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class FileCreateTest {

    @Test
    public void testFileCreate() {
        FileCreate fileCreate = new FileCreate();
        CommandLine cmd = new CommandLine(fileCreate);

//        try {
//            cmd.parseArgs("-a=1", "-b=2");
//        } catch (MutuallyExclusiveArgsException ex) {
//            assert "Error: -a=<a>, -b=<b> are mutually exclusive (specify only one)"
//                    .equals(ex.getMessage());
//        }

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
