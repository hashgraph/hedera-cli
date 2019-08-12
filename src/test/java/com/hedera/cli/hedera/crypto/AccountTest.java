package com.hedera.cli.hedera.crypto;

import org.junit.Test;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AccountTest {

    @Test
    public void testFileSubcommands() {
        @Command class Create {}
        @Command class Info {}
        @Command class Update {}
        @Command class Account {}

        CommandLine commandLine = new CommandLine(new Account());
        commandLine
                .addSubcommand("create", new Create())
                .addSubcommand("info", new Info())
                .addSubcommand("update", new Update());

        Map<String, CommandLine> commandMap = commandLine.getSubcommands();
        assertEquals(3, commandMap.size());
        assertTrue("create", commandMap.containsKey("create"));
        assertTrue("info", commandMap.containsKey("info"));
        assertTrue("update", commandMap.containsKey("update"));
    }

}
