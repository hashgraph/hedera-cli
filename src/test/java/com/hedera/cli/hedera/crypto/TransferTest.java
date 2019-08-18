package com.hedera.cli.hedera.crypto;

import org.junit.Test;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertTrue("single", commandMap.containsKey("single"));
        assertTrue("multiple", commandMap.containsKey("multiple"));
    }
}
