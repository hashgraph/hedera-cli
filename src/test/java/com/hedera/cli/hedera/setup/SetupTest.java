package com.hedera.cli.hedera.setup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.shell.ShellHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SetupTest {

    private final PrintStream stdout = System.out;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @InjectMocks
    private Setup setup;

    @Mock
    private InputReader inputReader;

    @Mock
    private Hedera hedera;

    @Mock
    private ShellHelper shellHelper;

    @Mock
    private AccountManager accountManager;

    @BeforeEach
    public void setUp() throws UnsupportedEncodingException {
        System.setOut(new PrintStream(output, true, "UTF-8"));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(stdout);
    }

    @Test
    public void help() {
        setup.help();
        String actual = captureLine().trim();
        String expected = "Usage: setup";
        assertEquals(expected, actual);
    }

    @Test
    public void assertGetters() {
        assertEquals(shellHelper, setup.getShellHelper());
    }

    @Test
    public void runFailsWithInvalidAccountId() {
        System.setOut(stdout);
        String prompt1 = "account ID in the format of 0.0.xxxx that will be used as default operator";
        when(accountManager.verifyAccountId(eq(""))).thenReturn(null);
        when(hedera.getAccountManager()).thenReturn(accountManager);
        when(inputReader.prompt(eq(prompt1))).thenReturn(""); // user provides an invalid AccountId String

        // execute function under test
        setup.run();

        verify(inputReader, times(1)).prompt(anyString());
    }

    private String captureLine() {
        return new String(output.toByteArray());
    }
}