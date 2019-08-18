package com.hedera.cli.hedera.crypto;


import org.junit.Test;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class CryptoTransferTests {

    @Test
    public void testCryptoTransferSingleArgs() {

        @Command class CryptoTransfer {

            @Option(names = {"-r", "--recipient"}, arity = "0..1")
            private String recipient;

            @Option(names = {"-a", "--recipientAmt"}, arity = "0..1")
            private String recipientAmt;

        }

        CryptoTransfer ct = CommandLine.populateCommand(new CryptoTransfer(), "-r=1234","-a=100");
        assertEquals("1234", ct.recipient);
        assertEquals("100", ct.recipientAmt);

        CommandLine cmd = new CommandLine(new CryptoTransfer());
        ParseResult result = cmd.parseArgs("-r=1111", "-a=1000");
        assertTrue(result.hasMatchedOption("r"));
        assertTrue(result.hasMatchedOption("a"));
        assertEquals("1111", result.matchedOptionValue("r", "1111"));
        assertEquals("1000", result.matchedOptionValue("a", "1000"));
        assertEquals(Arrays.asList("-r=1111", "-a=1000"), result.originalArgs());

    }
}
