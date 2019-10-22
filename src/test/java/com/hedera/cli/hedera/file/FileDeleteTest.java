package com.hedera.cli.hedera.file;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

public class FileDeleteTest {

    @Test
    public void testFileDeleteArgs() {

        @Command
        class FileDelete {

            @Option(names = {"-f", "--fileID"})
            private String fileIDInString;
        }

        FileDelete fd = CommandLine.populateCommand(new FileDelete(), "-f=0.0.1234");
        List<String> expectedId = new ArrayList<>();
        expectedId.add("0.0.1234");
        assertEquals(expectedId, Collections.singletonList(fd.fileIDInString));

        CommandLine cmd = new CommandLine(new FileDelete());
        CommandLine.ParseResult result = cmd.parseArgs("-f=0.0.1111");
        assertTrue(result.hasMatchedOption("f"));
        assertNull(result.subcommand());
        assertEquals("0.0.1111", result.matchedOptionValue('f', "0.0.1111"));
        assertEquals(Collections.singletonList("0.0.1111"), result.matchedOption('f').typedValues());
        System.out.println(result.originalArgs());
        assertEquals(Collections.singletonList("-f=0.0.1111"), result.originalArgs());
    }
}
