package com.hedera.cli.hedera.setup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
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

    @Mock
    private AccountRecovery accountRecovery;

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

//    @Test
//    public void runSucceedsBipWords() {
//        System.setOut(stdout);
//        String prompt1 = "account ID in the format of 0.0.xxxx that will be used as default operator";
//        String method = "bip";
//
//        when(accountManager.verifyAccountId(eq(accountId))).thenReturn(accountId);
//        when(hedera.getAccountManager()).thenReturn(accountManager);
//        when(inputReader.prompt(eq(prompt1))).thenReturn(accountId);
//        when(accountRecovery.promptPreview(inputReader)).thenReturn(true);
//        when(accountRecovery.phraseListFromRecoveryWordsPrompt(inputReader, accountManager)).thenReturn(phraseList);
//        when(accountRecovery.methodFromMethodPrompt(inputReader, accountManager)).thenReturn(method);
//        when(accountRecovery.isBip(method)).thenReturn(true);
//        doNothing().when(accountRecovery).recoverWithBipMethod(phraseList, Ed25519PrivateKey.fromString(keyPair.getPrivateKeyEncodedHex()), accountId, true);
//        setup.setEd25519PrivateKey(Ed25519PrivateKey.fromString(keyPair.getPrivateKeyEncodedHex()));
//        setup.setAccountRecovery(accountRecovery);
//        setup.setInputReader(inputReader);
//        setup.setPhraseList(phraseList);
//        setup.setShellHelper(shellHelper);
//        setup.run();
//
////        lenient().doNothing().when(accountRecovery).recoverWithBipMethod(eq(phraseList), eq(Ed25519PrivateKey.fromString(keyPair.getPrivateKeyEncodedHex())), eq(accountId), false);
////        lenient().when(accountRecovery.verifyAccountExistsInHedera(accountId, keyPair.getPrivateKeyEncodedHex())).thenReturn(true);
////        lenient().when(accountRecovery.recoverEd25519AccountKeypair(eq(phraseList))).thenReturn(keyPair);
//
//        assertEquals(accountRecovery, setup.getAccountRecovery());
//        assertEquals(inputReader, setup.getInputReader());
//        assertEquals(phraseList, setup.getPhraseList());
//        assertEquals(shellHelper, setup.getShellHelper());
//        assertEquals(keyPair.getPrivateKeyEncodedHex(), setup.getEd25519PrivateKey().toString());
//    }
//
//    @Test
//    public void runSucceeds() {
//        System.setOut(stdout);
//        String prompt1 = "account ID in the format of 0.0.xxxx that will be used as default operator";
//        String method = "bip";
//
//        when(accountManager.verifyAccountId(eq(accountId))).thenReturn(accountId);
//        when(hedera.getAccountManager()).thenReturn(accountManager);
//        when(inputReader.prompt(eq(prompt1))).thenReturn(accountId);
//        when(accountRecovery.promptPreview(inputReader)).thenReturn(false);
//        when(accountRecovery.phraseListFromRecoveryWordsPrompt(inputReader, accountManager)).thenReturn(phraseList);
//        when(accountRecovery.methodFromMethodPrompt(inputReader, accountManager)).thenReturn(method);
//        when(accountRecovery.isBip(method)).thenReturn(false);
//
//        lenient().when(accountRecovery.recoverEDKeypairPostBipMigration(eq(phraseList))).thenReturn(keyPair);
//        lenient().when(accountRecovery.verifyAccountExistsInHedera(accountId, keyPair.getPrivateKeyEncodedHex())).thenReturn(true);
//        lenient().when(accountRecovery.recoverEd25519AccountKeypair(eq(phraseList))).thenReturn(keyPair);
//        verify(accountRecovery, times(1)).recoverWithHgcMethod(phraseList, Ed25519PrivateKey.fromString(keyPair.getPrivateKeyEncodedHex()), accountId, false);
//    }
}