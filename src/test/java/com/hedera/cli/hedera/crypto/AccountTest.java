package com.hedera.cli.hedera.crypto;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class AccountTest {

    @Test
    public void testFileSubcommands() {
        @Command
        class Create {
        }
        @Command
        class Info {
        }
        @Command
        class Update {
        }
        @Command
        class Account {
        }

        CommandLine commandLine = new CommandLine(new Account());
        commandLine.addSubcommand("create", new Create()).addSubcommand("info", new Info()).addSubcommand("update",
                new Update());

        Map<String, CommandLine> commandMap = commandLine.getSubcommands();
        assertEquals(3, commandMap.size());
        assertTrue(commandMap.containsKey("create"));
        assertTrue(commandMap.containsKey("info"));
        assertTrue(commandMap.containsKey("update"));
    }

}
