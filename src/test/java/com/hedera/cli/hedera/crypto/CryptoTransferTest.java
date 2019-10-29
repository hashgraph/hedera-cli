package com.hedera.cli.hedera.crypto;

import java.util.Arrays;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.utils.AccountUtils;
import com.hedera.cli.hedera.utils.Composite;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class CryptoTransferTest {

    @InjectMocks
    CryptoTransfer cryptoTransfer;

    @Mock
    ShellHelper shellHelper;

    @Mock
    Composite composite;

    @Mock
    AccountUtils accountUtils;

    @Mock
    Hedera hedera;

    @Mock
    InputReader inputReader;

    @Mock
    Client client;

    @Test
    public void testCryptoTransferSingleArgs() {

        @Command
        class CryptoTransfer {

            @Option(names = { "-a", "--accountId" }, arity = "0..1")
            private String recipient;

            @Option(names = { "-r", "--recipientAmt" }, arity = "0..1")
            private String recipientAmt;

        }

        CryptoTransfer ct = CommandLine.populateCommand(new CryptoTransfer(), "-a=0.0.1234", "-r=100");
        assertEquals("0.0.1234", ct.recipient);
        assertEquals("100", ct.recipientAmt);

        CommandLine cmd = new CommandLine(new CryptoTransfer());
        ParseResult result = cmd.parseArgs("-a=0.0.1111", "-r=1000");
        assertTrue(result.hasMatchedOption("a"));
        assertTrue(result.hasMatchedOption("r"));
        assertEquals("0.0.1111", result.matchedOptionValue("a", "0.0.1111"));
        assertEquals("1000", result.matchedOptionValue("r", "1000"));
        assertEquals(Arrays.asList("-a=0.0.1111", "-r=1000"), result.originalArgs());

    }

    @Test
    public void assertDependenciesNotNull() {
        shellHelper = cryptoTransfer.getShellHelper();
        assertNotNull(shellHelper);

        hedera = cryptoTransfer.getHedera();
        assertNotNull(hedera);

        accountUtils = cryptoTransfer.getAccountUtils();
        assertNotNull(accountUtils);

        composite = cryptoTransfer.getComposite();
        assertNotNull(composite);

        inputReader = cryptoTransfer.getInputReader();
        assertNotNull(inputReader);
    }

    @Test
    public void gettersAndSetters() {
        String memosSringExpected = "Hello there!";
        cryptoTransfer.setMemoString(memosSringExpected);
        String memoStringActual = cryptoTransfer.getMemoString();
        assertEquals(memosSringExpected, memoStringActual);

        String mPreviewExpected = "yes";
        cryptoTransfer.setMPreview(mPreviewExpected);
        String mPreviewActual = cryptoTransfer.getMPreview();
        assertEquals(mPreviewExpected, mPreviewActual);

        String isInfoExpected = "yes";
        cryptoTransfer.setIsInfoCorrect(isInfoExpected);
        String isInfoActual = cryptoTransfer.getIsInfoCorrect();
        assertEquals(isInfoExpected, isInfoActual);
    }
}
