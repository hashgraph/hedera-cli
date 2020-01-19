package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.keygen.EDBip32KeyChain;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.DataDirectory;
import com.hedera.cli.models.RecoveredAccountModel;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountRecoveryTest {

    private final PrintStream stdout = System.out;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @InjectMocks
    private AccountRecovery accountRecovery;

    @Mock
    private ShellHelper shellHelper;

    @Mock
    private InputReader inputReader;

    @Mock
    private InputPrompts inputPrompts;

    @Mock
    private DataDirectory dataDirectory;

    @Mock
    private AccountManager accountManager;

    private List<String> phraseList = Arrays.asList("hello", "fine", "demise", "ladder", "glow", "hard", "magnet",
            "fan", "donkey", "carry", "chuckle", "assault", "leopard", "fee", "kingdom", "cheap", "odor", "okay",
            "crazy", "raven", "goose", "focus", "shrimp", "carbon");
    private String accountId = "0.0.1234";
    private KeyPair keyPair;
    private Ed25519PrivateKey ed25519PrivateKey;

    @BeforeEach
    public void setUp() throws UnsupportedEncodingException {
        System.setOut(new PrintStream(output, true, "UTF-8"));
        EDBip32KeyChain keyChain = new EDBip32KeyChain();
        int index = 0;
        keyPair = keyChain.keyPairFromWordList(index, phraseList);
        ed25519PrivateKey = Ed25519PrivateKey.fromString(keyPair.getPrivateKeyEncodedHex());
    }

    @AfterEach
    public void tearDown() {
        System.setOut(stdout);
    }

    @Test
    public void run() {
        assertNotNull(accountManager);

        accountRecovery.run();

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).printInfo(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "Start the recovery process";
        assertEquals(expected, actual);
    }

    // FIX THIS
    // we cannot mock final class AccountInfo because it is serializable, despite using mockito-inline
    // @Test
    // public void verifyAccountExistsLocallyFalse() {
    // AccountInfo accountInfo = mock(AccountInfo.class);
    // when(accountInfo.accountId).thenReturn(AccountId.fromString(accountId));

    // String pathToIndexTxt = accountManager.pathToIndexTxt();
    // HashMap<String, String> testMap = new HashMap<>();
    // testMap.put("0.0.90304", "aggressive_primerose_3092");
    // testMap.put("0.0.82319", "gloomy_alyssum_270");
    // testMap.put("0.0.1003", "wiry_bryn_3883");
    // testMap.put("0.0.1009", "jaunty_mint_465");
    // testMap.put("0.0.112232", "definitive_forsythia_2853");
    // testMap.put("0.0.8888", "sorrowful_geranium_7578");
    // when(dataDirectory.readIndexToHashmap(pathToIndexTxt)).thenReturn(testMap);

    // boolean accountExist =
    // accountRecovery.verifyAccountExistsLocally(accountInfo, accountId);

    // ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    // verify(shellHelper).printSuccess(valueCapture.capture());
    // String actual = valueCapture.getValue();
    // String expected = "Account recovered and verified with Hedera";
    // assertEquals(expected, actual);
    // assertTrue(accountExist);
    // }

    @Test
    public void runWithPrompt() {
        String bip = "bip";
        accountRecovery.setInputReader(inputReader);
        when(inputPrompts.keysOrPassphrasePrompt(inputReader)).thenReturn(true);
        boolean isWords = inputPrompts.keysOrPassphrasePrompt(inputReader);

        when(inputPrompts.passphrasePrompt(inputReader, accountManager)).thenReturn(phraseList);
        List<String> actualPhraseList = inputPrompts.passphrasePrompt(inputReader, accountManager);

        when(inputPrompts.ed25519PrivKeysPrompt(inputReader, accountId, shellHelper)).thenReturn(ed25519PrivateKey);
        Ed25519PrivateKey ed25519PrivateKey1 = inputPrompts.ed25519PrivKeysPrompt(inputReader, accountId, shellHelper);

        when(accountManager.isBip(bip)).thenReturn(true);
        accountRecovery.run();
        assertEquals(phraseList, actualPhraseList);
        assertEquals(ed25519PrivateKey, ed25519PrivateKey1);
        assertEquals(inputReader, accountRecovery.getInputReader());
        assertTrue(isWords);

        assertEquals(keyPair.getPrivateKeyHex(),
                accountRecovery.recoverKeypairWithPassphrase(phraseList, bip, accountId).getPrivateKeyHex());
    }

    @Test
    public void recoverWithMethodIsKeys() {
        AccountRecovery accountRecovery1 = Mockito.spy(accountRecovery);
        accountRecovery1.verifyWithPrivKey(ed25519PrivateKey, accountId);
        verify(accountRecovery1).verifyWithPrivKey(ed25519PrivateKey, accountId);
    }

    @Test
    public void verifyAndSaveWithPrivKeyFalse() {
        boolean notVerified = accountRecovery.verifyAndSaveWithPrivKey(ed25519PrivateKey, accountId);

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper, times(2)).printError(valueCapture.capture());
        List<String> actual = valueCapture.getAllValues();
        String expected = "Error in recovering account";
        assertEquals(expected, actual.get(1));
        assertFalse(notVerified);
    }

    @Test
    public void printKeyPair() throws JsonProcessingException {
        RecoveredAccountModel recoveredAccountModel;
        recoveredAccountModel = new RecoveredAccountModel();
        recoveredAccountModel.setAccountId(accountId);
        recoveredAccountModel.setPrivateKey(keyPair.getPrivateKeyHex());
        recoveredAccountModel.setPublicKey(keyPair.getPublicKeyHex());
        recoveredAccountModel.setPrivateKeyEncoded(keyPair.getPrivateKeyEncodedHex());
        recoveredAccountModel.setPublicKeyEncoded(keyPair.getPublicKeyEncodedHex());
        recoveredAccountModel.setPrivateKeyBrowserCompatible(keyPair.getSeedAndPublicKeyHex());
        accountRecovery.printKeyPair(keyPair, accountId);
        assertEquals(accountId, recoveredAccountModel.getAccountId());
        assertEquals(keyPair.getPrivateKeyHex(), recoveredAccountModel.getPrivateKey());
        assertEquals(keyPair.getPublicKeyHex(), recoveredAccountModel.getPublicKey());
        assertEquals(keyPair.getPrivateKeyEncodedHex(), recoveredAccountModel.getPrivateKeyEncoded());
        assertEquals(keyPair.getPublicKeyEncodedHex(), recoveredAccountModel.getPublicKeyEncoded());
        assertEquals(keyPair.getSeedAndPublicKeyHex(), recoveredAccountModel.getPrivateKeyBrowserCompatible());

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String result = ow.writeValueAsString(recoveredAccountModel);
        verify(shellHelper, times(1)).printSuccess(result);
    }

    @Test
    public void printKeyPairWithPrivKey() throws JsonProcessingException {
        RecoveredAccountModel recoveredAccountModel;
        recoveredAccountModel = new RecoveredAccountModel();
        recoveredAccountModel.setAccountId(accountId);
        recoveredAccountModel.setPrivateKey(ed25519PrivateKey.toString().substring(32));
        recoveredAccountModel.setPublicKey(ed25519PrivateKey.publicKey.toString().substring(24));
        recoveredAccountModel.setPrivateKeyEncoded(ed25519PrivateKey.toString());
        recoveredAccountModel.setPublicKeyEncoded(ed25519PrivateKey.publicKey.toString());
        accountRecovery.printKeyPairWithPrivKey(ed25519PrivateKey, accountId);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String result = ow.writeValueAsString(recoveredAccountModel);
        verify(shellHelper, times(1)).printSuccess(result);
    }
}