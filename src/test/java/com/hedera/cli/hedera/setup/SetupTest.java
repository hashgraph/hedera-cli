package com.hedera.cli.hedera.setup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.crypto.AccountRecovery;
import com.hedera.cli.hedera.keygen.EDBip32KeyChain;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    private ShellHelper shellHelper;

    @Mock
    private InputReader inputReader;

    @Mock
    private AccountRecovery accountRecovery;

    @Mock
    private Hedera hedera;

    // test data
    private List<String> phraseList = Arrays.asList("hello", "fine", "demise", "ladder", "glow", "hard", "magnet",
            "fan", "donkey", "carry", "chuckle", "assault", "leopard", "fee", "kingdom", "cheap", "odor", "okay",
            "crazy", "raven", "goose", "focus", "shrimp", "carbon");
    private String accountId = "0.0.1234";
    private KeyPair keyPair;

    @BeforeEach
    public void setUp() throws UnsupportedEncodingException {
        System.setOut(new PrintStream(output, true, "UTF-8"));
        // generate keyPair from phraseList (test data) for tests
        EDBip32KeyChain keyChain = new EDBip32KeyChain();
        int index = 0;
        keyPair = keyChain.keyPairFromWordList(index, phraseList);
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
    public void run() {
        System.setOut(stdout);
        String prompt1 = "account ID in the format of 0.0.xxxx that will be used as default operator";
        String prompt2 = "24 words phrase";
        String prompt3 = "Have you migrated your account on Hedera wallet? If migrated, enter `bip`, else enter `hgc`";
        String secret = "secret";
        boolean echo = false;
        String phraseInput = String.join(" ", phraseList).trim();

        AccountManager accountManager = mock(AccountManager.class);
        when(accountManager.verifyAccountId(eq(accountId))).thenReturn(accountId);
        when(hedera.getAccountManager()).thenReturn(accountManager);
        when(inputReader.prompt(eq(prompt1))).thenReturn(accountId);
        System.out.println(phraseInput);
        System.out.println(phraseInput.length());
        when(inputReader.prompt(eq(prompt2), eq(secret), eq(echo))).thenReturn(phraseInput);
        when(inputReader.prompt(eq(prompt3))).thenReturn("bip");
        when(accountManager.verifyPhraseList(eq(phraseList))).thenReturn(phraseList);
        when(accountManager.verifyMethod(eq("bip"))).thenReturn("bip");

        lenient().when(accountRecovery.recoverEDKeypairPostBipMigration(eq(phraseList))).thenReturn(keyPair);
        lenient().when(accountRecovery.recoverEd25519AccountKeypair(eq(phraseList), eq(accountId))).thenReturn(keyPair);
        when(accountRecovery.verifyAndSaveAccount(eq(accountId), eq(keyPair))).thenReturn(true);

        // execute function under test
        setup.run();

        // assertions
        verify(accountRecovery, times(1)).printKeyPair(keyPair, accountId);
        verify(accountManager, times(1)).setDefaultAccountId(AccountId.fromString(accountId), keyPair);
    }

    @Test
    public void runFails() {
        System.setOut(stdout);
        String prompt1 = "account ID in the format of 0.0.xxxx that will be used as default operator";
        String prompt2 = "24 words phrase";
        String secret = "secret";
        boolean echo = false;
        String prompt3 = "Have you migrated your account on Hedera wallet? If migrated, enter `bip`, else enter `hgc`";
        String phraseInput = String.join(" ", phraseList).trim();

        AccountManager accountManager = mock(AccountManager.class);
        when(accountManager.verifyAccountId(eq(accountId))).thenReturn(accountId);
        when(hedera.getAccountManager()).thenReturn(accountManager);
        when(inputReader.prompt(eq(prompt1))).thenReturn(accountId);
        when(inputReader.prompt(eq(prompt2), eq(secret), eq(echo))).thenReturn(phraseInput);
        when(inputReader.prompt(eq(prompt3))).thenReturn("bip");
        when(accountManager.verifyPhraseList(eq(phraseList))).thenReturn(phraseList);
        when(accountManager.verifyMethod(eq("bip"))).thenReturn("bip");

        lenient().when(accountRecovery.recoverEDKeypairPostBipMigration(eq(phraseList))).thenReturn(keyPair);
        lenient().when(accountRecovery.recoverEd25519AccountKeypair(eq(phraseList), eq(accountId))).thenReturn(keyPair);

        // when checking against Hedera, we find out that the account id and
        // re-generated keyPair do not match!
        // mock this failure by returningh false
        when(accountRecovery.verifyAndSaveAccount(eq(accountId), eq(keyPair))).thenReturn(false);

        // execute function under test
        setup.run();

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).printError(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "Error in verifying that accountId and recovery words match";

        assertEquals(expected, actual);
    }

    @Test
    public void runFailsWithInvalidAccountId() {
        System.setOut(stdout);
        String prompt1 = "account ID in the format of 0.0.xxxx that will be used as default operator";
        AccountManager accountManager = mock(AccountManager.class);
        when(accountManager.verifyAccountId(eq(""))).thenReturn(null);
        when(hedera.getAccountManager()).thenReturn(accountManager);
        when(inputReader.prompt(eq(prompt1))).thenReturn(""); // user provides an invalid AccountId String

        // execute function under test
        setup.run();

        verify(inputReader, times(1)).prompt(anyString());
    }

    @Test
    public void runFailsWithInvalidPhraseList() {
        System.setOut(stdout);
        String prompt1 = "account ID in the format of 0.0.xxxx that will be used as default operator";
        String prompt2 = "24 words phrase";
        String secret = "secret";
        boolean echo = false;

        AccountManager accountManager = mock(AccountManager.class);
        when(accountManager.verifyAccountId(eq(accountId))).thenReturn(accountId);
        when(hedera.getAccountManager()).thenReturn(accountManager);
        when(inputReader.prompt(eq(prompt1))).thenReturn(accountId);
        when(inputReader.prompt(eq(prompt2), eq(secret), eq(echo))).thenReturn(""); // invalid mnemonic phrase
        List<String> phraseList = Arrays.asList("".split(" "));
        when(accountManager.verifyPhraseList(eq(phraseList))).thenReturn(phraseList);

        // execute function under test
        setup.run();

        assertNotEquals(24, phraseList.size());
    }

    @Test
    public void runSucceedsWithHgc() {
        System.setOut(stdout);
        String prompt1 = "account ID in the format of 0.0.xxxx that will be used as default operator";
        String prompt2 = "24 words phrase";
        String secret = "secret";
        boolean echo = false;
        String prompt3 = "Have you migrated your account on Hedera wallet? If migrated, enter `bip`, else enter `hgc`";
        String phraseInput = String.join(" ", phraseList).trim();

        AccountManager accountManager = mock(AccountManager.class);
        when(accountManager.verifyAccountId(eq(accountId))).thenReturn(accountId);
        when(hedera.getAccountManager()).thenReturn(accountManager);
        when(inputReader.prompt(eq(prompt1))).thenReturn(accountId);
        when(inputReader.prompt(eq(prompt2), eq(secret), eq(echo))).thenReturn(phraseInput);
        when(inputReader.prompt(eq(prompt3))).thenReturn("hgc");
        when(accountManager.verifyPhraseList(eq(phraseList))).thenReturn(phraseList);
        when(accountManager.verifyMethod(eq("hgc"))).thenReturn("hgc");

        // execute function under test
        setup.run();

        verify(accountRecovery).recoverEd25519AccountKeypair(eq(phraseList), eq(accountId));
    }

    private String captureLine() {
        return new String(output.toByteArray());
    }

}