package com.hedera.cli.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.keygen.CryptoUtils;
import com.hedera.cli.hedera.keygen.HGCSeed;
import com.hedera.cli.hedera.keygen.KeyGeneration;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.services.CurrentAccountService;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.hjson.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = { CurrentAccountService.class })
public class AccountManagerTest {

    @InjectMocks
    private AccountManager accountManager;

    @Mock
    private DataDirectory dataDirectory;

    @Mock
    private ShellHelper shellHelper;

    @Mock
    private InputReader inputReader;

    @Mock
    private RandomNameGenerator randomNameGenerator;

    @Test
    public void getters() {
        assertNotNull(accountManager.getDataDirectory());
        assertNotNull(accountManager.getShellHelper());
        assertNotNull(accountManager.getRandomNameGenerator());
    }

    @Test
    public void checkPaths() {
        doAnswer(invocation -> "testnet").when(dataDirectory).readFile("network.txt");

        assertEquals("testnet/accounts/", accountManager.pathToAccountsFolder());
        assertEquals("testnet/accounts/index.txt", accountManager.pathToIndexTxt());
        assertEquals("testnet/accounts/default.txt", accountManager.pathToDefaultTxt());
    }

    @Test
    public void defaultAccountString() {
        doAnswer(invocation -> "testnet").when(dataDirectory).readFile("network.txt");
        doAnswer(invocation -> "adjective_botanic_number:0.0.1234").when(dataDirectory)
                .readFile("testnet/accounts/default.txt");

        String[] defaultAccountArray = accountManager.defaultAccountString();
        assertEquals("adjective_botanic_number:0.0.1234".split(":")[0], defaultAccountArray[0]);
        assertEquals("adjective_botanic_number:0.0.1234".split(":")[1], defaultAccountArray[1]);
    }

    @Test
    public void retrieveDefaultAccountID() {
        doAnswer(invocation -> "testnet").when(dataDirectory).readFile("network.txt");
        doAnswer(invocation -> "adjective_botanic_number:0.0.1234").when(dataDirectory)
                .readFile("testnet/accounts/default.txt");

        AccountId accountId = accountManager.getDefaultAccountId();
        assertEquals("0.0.1234", accountId.toString());
    }

    @Test
    public void createAccountJsonWithPrivateKey() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        KeyPair keypair = prepareKeyPair();
        String privateKeyString = keypair.getPrivateKeyEncodedHex();
        Ed25519PrivateKey privateKey = Ed25519PrivateKey.fromString(privateKeyString);

        Method method = accountManager.getClass().getDeclaredMethod("createAccountJsonWithPrivateKey", String.class, Ed25519PrivateKey.class);
        method.setAccessible(true);
        JsonObject account = (JsonObject) method.invoke(accountManager, "0.0.1001", privateKey);
        method.setAccessible(false);

        assertEquals("0.0.1001", account.get("accountId").asString());
        assertEquals(privateKeyString, account.get("privateKey").asString());
    }

    @Test
    public void createAccountJsonWithKeyPair() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {

        KeyPair keypair = prepareKeyPair();
        String privateKeyString = keypair.getPrivateKeyHex();

        Method method = accountManager.getClass().getDeclaredMethod("createAccountJsonWithKeyPair", String.class,
                KeyPair.class);
        method.setAccessible(true);
        JsonObject account = (JsonObject) method.invoke(accountManager, "0.0.1001", keypair);
        method.setAccessible(false);

        assertEquals("0.0.1001", account.get("accountId").asString());
        assertEquals(privateKeyString, account.get("privateKey").asString());
    }

    @Test
    public void writeAccountId() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        doAnswer(invocation -> "adjective_botanic_number").when(randomNameGenerator).getRandomName();

        KeyPair keypair = prepareKeyPair();

        Method method = accountManager.getClass().getDeclaredMethod("createAccountJsonWithKeyPair", String.class,
                KeyPair.class);
        method.setAccessible(true);
        JsonObject account = (JsonObject) method.invoke(accountManager, "0.0.1001", keypair);
        method.setAccessible(false);

        method = accountManager.getClass().getDeclaredMethod("writeAccountId", AccountId.class,
                JsonObject.class);
        method.setAccessible(true);

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);

        // succeeds
        method.invoke(accountManager, AccountId.fromString("0.0.1001"), account);
        verify(shellHelper).printInfo(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "0.0.1001 saved";
        assertEquals(expected, actual);

        // fails
        method.invoke(accountManager, AccountId.fromString("0.0.1001"), null);
        verify(shellHelper).printError(valueCapture.capture());
        actual = valueCapture.getValue();
        expected = "Failed to save 0.0.1001";
        assertEquals(expected, actual);

        method.setAccessible(false);

        assertNotNull(accountManager);
    }

    @Test
    public void setDefaultAccountIdWithKeyPair() {
        KeyPair keypair = prepareKeyPair();

        accountManager.setDefaultAccountId(AccountId.fromString("0.0.1001"), keypair);
        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).printInfo(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "0.0.1001 saved";
        assertEquals(expected, actual);
    }

    @Test
    public void setDefaultAccountIdWithPrivateKey() {
        assertNotNull(1);
        KeyPair keypair = prepareKeyPair();
        String privateKeyString = keypair.getPrivateKeyEncodedHex();
        Ed25519PrivateKey privateKey = Ed25519PrivateKey.fromString(privateKeyString);

        accountManager.setDefaultAccountId(AccountId.fromString("0.0.1001"), privateKey);
        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).printInfo(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "0.0.1001 saved";
        assertEquals(expected, actual);
    }

    private KeyPair prepareKeyPair() {
        KeyGeneration keyGeneration = new KeyGeneration("bip");
        HGCSeed hgcSeed = new HGCSeed((CryptoUtils.getSecureRandomData(32)));
        List<String> mnemonic = keyGeneration.generateMnemonic(hgcSeed);
        KeyPair keypair = keyGeneration.generateKeysAndWords(hgcSeed, mnemonic);
        return keypair;
    }

    @SuppressWarnings("serial")
    @Test
    public void retrieveDefaultAccountPublicKeyInHexString() {
        doAnswer(invocation -> "testnet").when(dataDirectory).readFile("network.txt");
        doAnswer(invocation -> "adjective_botanic_number:0.0.1234").when(dataDirectory)
                .readFile("testnet/accounts/default.txt");
        doAnswer(invocation -> new HashMap<String, String>() {
            {
                put("publicKey", "somepublickeyhex");
            }
        }).when(dataDirectory).readJsonToHashmap("testnet/accounts/adjective_botanic_number.json");

        String publicKey = accountManager.getDefaultAccountPublicKeyInHexString();
        assertEquals("somepublickeyhex", publicKey);
    }

    @SuppressWarnings("serial")
    @Test
    public void testRetrieveDefaultAccountKeyInHexString() {
        doAnswer(invocation -> "testnet").when(dataDirectory).readFile("network.txt");
        doAnswer(invocation -> "adjective_botanic_number:0.0.1234").when(dataDirectory)
                .readFile("testnet/accounts/default.txt");
        doAnswer(invocation -> new HashMap<String, String>() {
            {
                put("privateKey", "somesecretprivatekey");
            }
        }).when(dataDirectory).readJsonToHashmap("testnet/accounts/adjective_botanic_number.json");

        String privateKey = accountManager.getDefaultAccountKeyInHexString();
        assertEquals("somesecretprivatekey", privateKey);
    }

    @Test
    public void testIsAccountId() {
        String str = "0.0.1001";
        assertTrue(accountManager.isAccountId(str));

        String str1 = " ";
        assertFalse(accountManager.isAccountId(str1));

        String str2 = "0.0.10a01";
        assertFalse(accountManager.isAccountId(str2));

        String str3 = "0.0.0";
        assertTrue(accountManager.isAccountId(str3));

        String str4 = "-";
        assertFalse(accountManager.isAccountId(str4));

        String str5 = "0.0";
        assertFalse(accountManager.isAccountId(str5));

        String str6 = "0.0.";
        assertFalse(accountManager.isAccountId(str6));

        String str7 = "0.0.a.";
        assertFalse(accountManager.isAccountId(str7));

        String str8 = "a.a.a.";
        assertFalse(accountManager.isAccountId(str8));

        String str9 = ".0.0.a.";
        assertFalse(accountManager.isAccountId(str9));

        String str11 = "0.a.1001";
        assertFalse(accountManager.isAccountId(str11));

        String str12 = "";
        assertFalse(accountManager.isAccountId(str12));

        String str13 = null;
        assertFalse(accountManager.isAccountId(str13));
    }

    @Test
    public void verifyPhaseListSizeTrue() {
        String phrase = "once busy dash argue stuff quarter property west tackle swamp enough brisk split code borrow ski soccer tip churn kitten congress admit april defy";
        List<String> phraseList = Arrays.asList(phrase.split(" "));
        assertEquals(phraseList, accountManager.verifyPhraseList(phraseList));
    }

    @Test
    public void verifyPhaseListSizeFalse() {
        String not24WordPhrase = "dash argue stuff quarter property west tackle swamp enough brisk split code borrow ski soccer tip churn kitten congress admit april defy";
        List<String> phraseList = Arrays.asList(not24WordPhrase.split(" "));
        assertNull(accountManager.verifyPhraseList(phraseList));
    }

    @Test
    public void verifyAccountIdTrue() {
        String accountId = "0.0.1234";
        assertEquals(accountId, accountManager.verifyAccountId(accountId));
    }

    @Test
    public void verifyAccountIdFalseReturnsNull() {
        String accountId = "0.0";
        assertNull(accountManager.verifyAccountId(accountId));
    }

    @Test
    public void verifyMethodBipTrue() {
        String method = "bip";
        assertEquals(method, accountManager.verifyMethod(method));
    }

    @Test
    public void verifyMethodHgcTrue() {
        String method = "hgc";
        assertEquals(method, accountManager.verifyMethod(method));
    }

    @Test
    public void verifyMethodFalse() {
        String method = "hellooo";
        assertNull(accountManager.verifyMethod(method));
    }

    @Test
    public void promptMemoStringReturnsEmpty() {
        when(inputReader.prompt("Memo field")).thenReturn(null);
        String memo = accountManager.promptMemoString(inputReader);
        assertEquals("", memo);
    }

    @Test
    public void promptMemoStringReturnsMemo() {
        when(inputReader.prompt("Memo field")).thenReturn("memo to send");
        String memo = accountManager.promptMemoString(inputReader);
        assertEquals("memo to send", memo);
    }

    @Test
    public void isBip() {
        assertTrue(accountManager.isBip("bip"));
        assertFalse(accountManager.isBip("hgc"));
    }
}