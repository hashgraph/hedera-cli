package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;

public class CryptoTransferTest {

    @Test
    public void testCryptoTransferSingleArgs() {

        @Command
        class CryptoTransfer {

            @Option(names = { "-a", "--accountId" }, arity = "0..1")
            private String recipient;

            @Option(names = { "-r", "--recipientAmt" }, arity = "0..1")
            private String recipientAmt;

        }

        CryptoTransfer ct = CommandLine.populateCommand(new CryptoTransfer(), "-a=1234", "-r=100");
        assertEquals("1234", ct.recipient);
        assertEquals("100", ct.recipientAmt);

        CommandLine cmd = new CommandLine(new CryptoTransfer());
        ParseResult result = cmd.parseArgs("-a=1111", "-r=1000");
        assertTrue(result.hasMatchedOption("a"));
        assertTrue(result.hasMatchedOption("r"));
        assertEquals("1111", result.matchedOptionValue("a", "1111"));
        assertEquals("1000", result.matchedOptionValue("r", "1000"));
        assertEquals(Arrays.asList("-a=1111", "-r=1000"), result.originalArgs());

    }
}
