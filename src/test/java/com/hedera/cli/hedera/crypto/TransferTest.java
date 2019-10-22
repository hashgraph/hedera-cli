package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import picocli.CommandLine;
import picocli.CommandLine.Command;

public class TransferTest {

    @Test
    public void testTransferSubcommands() {
        @Command
        class Single {}
        @Command
        class Multiple {}
        @Command
        class Transfer {}

        CommandLine commandLine = new CommandLine(new Transfer());
        commandLine
                .addSubcommand("single", new Single())
                .addSubcommand("multiple", new Multiple());

        Map<String, CommandLine> commandMap = commandLine.getSubcommands();
        assertEquals(2, commandMap.size());
        assertTrue(commandMap.containsKey("single"));
        assertTrue(commandMap.containsKey("multiple"));
    }
}
