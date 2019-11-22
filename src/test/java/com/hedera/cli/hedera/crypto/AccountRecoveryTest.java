package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.keygen.EDBip32KeyChain;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.DataDirectory;
import com.hedera.cli.models.RecoveredAccountModel;
import com.hedera.cli.shell.ShellHelper;

import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.HederaException;
import com.hedera.hashgraph.sdk.Transaction;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.AccountInfo;
import com.hedera.hashgraph.sdk.account.AccountInfoQuery;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hederahashgraph.api.proto.java.CryptoGetInfoResponse;
import com.hederahashgraph.api.proto.java.Response;
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
    private DataDirectory dataDirectory;
    @Mock
    private AccountManager accountManager;

    private List<String> phraseList = Arrays.asList("hello", "fine", "demise", "ladder", "glow", "hard", "magnet", "fan",
            "donkey", "carry", "chuckle", "assault", "leopard", "fee", "kingdom", "cheap", "odor", "okay", "crazy", "raven",
            "goose", "focus", "shrimp", "carbon");
    private String accountId = "0.0.1234";
    private KeyPair keyPair;

    @BeforeEach
    public void setUp() throws UnsupportedEncodingException {
        System.setOut(new PrintStream(output, true, "UTF-8"));
        EDBip32KeyChain keyChain = new EDBip32KeyChain();
        int index = 0;
        keyPair = keyChain.keyPairFromWordList(index, phraseList);
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

    @Test
    public void promptWords() {
        accountRecovery.setWords(true);
        when(inputReader.prompt("Recover account using 24 words or keys? Enter words/keys")).thenReturn("words");
        boolean wordsActual = accountRecovery.promptPreview(inputReader);
        assertEquals(accountRecovery.isWords(), wordsActual);
    }

    @Test
    public void promptKeys() {
        accountRecovery.setWords(false);
        when(inputReader.prompt("Recover account using 24 words or keys? Enter words/keys")).thenReturn("keys");
        boolean wordsActual = accountRecovery.promptPreview(inputReader);
        assertEquals(accountRecovery.isWords(), wordsActual);
    }

    @Test
    public void printKeyPairInRecoveredAccountModelFormat() throws JsonProcessingException {
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
    public void isBip() {
        String bip = "bip";
        String hgc = "hgc";
        assertTrue(accountRecovery.isBip(bip));
        assertFalse(accountRecovery.isBip(hgc));
    }

    @Test
    public void isWords() {
        accountRecovery.setInputReader(inputReader);
        when(inputReader.prompt("Recover account using 24 words or keys? Enter words/keys")).thenReturn("words");
        boolean isWords = accountRecovery.promptPreview(inputReader);
        accountRecovery.setWords(isWords);
        assertEquals(inputReader, accountRecovery.getInputReader());
        assertTrue(isWords);
    }

    @Test
    public void verifyAccountExistsLocallyFalse() {
        AccountInfo accountInfo = mock(AccountInfo.class);
        when(accountInfo.getAccountId()).thenReturn(AccountId.fromString(accountId));

        String pathToIndexTxt = accountManager.pathToIndexTxt();
        HashMap<String, String> testMap = new HashMap<>();
        testMap.put("0.0.90304", "aggressive_primerose_3092");
        testMap.put("0.0.82319", "gloomy_alyssum_270");
        testMap.put("0.0.1003", "wiry_bryn_3883");
        testMap.put("0.0.1009", "jaunty_mint_465");
        testMap.put("0.0.112232", "definitive_forsythia_2853");
        testMap.put("0.0.8888", "sorrowful_geranium_7578");
        when(dataDirectory.readIndexToHashmap(pathToIndexTxt)).thenReturn(testMap);

        boolean accountExist = accountRecovery.verifyAccountExistsLocally(accountInfo, accountId);

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).printSuccess(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "Account recovered and verified with Hedera";
        assertEquals(expected, actual);
        assertTrue(accountExist);
    }

    @Test
    public void phraseListFromRecoveryWordsPrompt() {
        accountRecovery.setInputReader(inputReader);
        String prompt2 = "24 words phrase";
        String secret = "secret";
        boolean echo = false;
        String phraseInput = String.join(" ", phraseList).trim();
        when(inputReader.prompt(eq(prompt2), eq(secret), eq(echo))).thenReturn(phraseInput);
        when(accountManager.verifyPhraseList(phraseList)).thenReturn(phraseList);
        List<String> actualPhraseList = accountRecovery.phraseListFromRecoveryWordsPrompt(inputReader, accountManager);
        assertEquals(phraseList, actualPhraseList);
    }

    @Test
    public void ed25519PrivateKeyFromKeysPrompt() {
        accountRecovery.setInputReader(inputReader);
        String prompt2 = "Enter the private key of account " + accountId;
        String secret = "secret";
        boolean echo = false;

        when(inputReader.prompt(eq(prompt2), eq(secret), eq(echo))).thenReturn(keyPair.getPrivateKeyEncodedHex());
        accountRecovery.setEd25519PrivateKey(Ed25519PrivateKey.fromString(keyPair.getPrivateKeyEncodedHex()));
        accountRecovery.ed25519PrivateKeyFromKeysPrompt(inputReader, accountId, shellHelper);
        assertEquals(keyPair.getPrivateKeyEncodedHex(), accountRecovery.getEd25519PrivateKey().toString());
    }
}