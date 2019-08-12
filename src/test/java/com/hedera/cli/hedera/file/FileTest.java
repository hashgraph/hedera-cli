package com.hedera.cli.hedera.file;

import org.junit.Test;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Map;

import static org.junit.Assert.*;

public class FileTest {

    @Test
    public void testFileSubcommands() {
        @Command class Create {}
        @Command class Delete {}
        @Command class Update {}
        @Command class File {}

        CommandLine commandLine = new CommandLine(new File());
        commandLine
                .addSubcommand("create", new Create())
                .addSubcommand("delete", new Delete())
                .addSubcommand("update", new Update());

        Map<String, CommandLine> commandMap = commandLine.getSubcommands();
        assertEquals(3, commandMap.size());
        assertTrue("create", commandMap.containsKey("create"));
        assertTrue("delete", commandMap.containsKey("delete"));
        assertTrue("update", commandMap.containsKey("update"));
    }
}
