package com.hedera.cli.hedera.file;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class FileTest {

    @Test
    public void testFileSubcommands() {
        @Command
        class Create {
        }
        @Command
        class Delete {
        }
        @Command
        class Update {
        }
        @Command
        class File {
        }

        CommandLine commandLine = new CommandLine(new File());
        commandLine.addSubcommand("create", new Create()).addSubcommand("delete", new Delete()).addSubcommand("update",
                new Update());

        Map<String, CommandLine> commandMap = commandLine.getSubcommands();
        assertEquals(3, commandMap.size());
        assertTrue(commandMap.containsKey("create"));
        assertTrue(commandMap.containsKey("delete"));
        assertTrue(commandMap.containsKey("update"));
    }
}
