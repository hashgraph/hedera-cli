package com.hedera.cli.hedera.setup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Path;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.hedera.cli.hedera.keygen.CryptoUtils;
import com.hedera.cli.hedera.keygen.EDBip32KeyChain;
import com.hedera.cli.hedera.keygen.HGCSeed;
import com.hedera.cli.hedera.keygen.KeyGeneration;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.hedera.utils.DataDirectory;

import com.hedera.cli.models.RecoveredAccountModel;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountInfoQuery;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.hjson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.FileSystemUtils;

@ExtendWith(MockitoExtension.class)
public class SetupTest {

    @InjectMocks
    private Setup setup;

    @TempDir
    public Path tempDir;

    @Mock
    private KeyGeneration keyGeneration;

    @Mock
    private ShellHelper shellHelper;

    @Mock
    private RandomNameGenerator randomNameGenerator;

    // not a mock
    private DataDirectory dataDirectory;
    private HGCSeed seed;
    private String accountId;
    private List<String> mnemonic;
    private KeyPair keyPair;
    private String phrase;
    private AccountInfoQuery q;

    @BeforeEach
    public void init() {
        mnemonic = Arrays.asList(
                "hello, fine, demise, ladder, glow, hard, magnet, fan, donkey, carry, chuckle, assault, leopard, fee, kingdom, cheap, odor, okay, crazy, raven, goose, focus, shrimp, carbon");
        seed = new HGCSeed((CryptoUtils.getSecureRandomData(32)));
        accountId = "0.0.1234";
        EDBip32KeyChain keyChain = new EDBip32KeyChain();
        int index = 0;
        keyPair = keyChain.keyPairFromWordList(index, mnemonic);
        phrase = "once busy dash argue stuff quarter property west tackle swamp enough brisk split code borrow ski soccer tip churn kitten congress admit april defy";
    }

    public void prepareTestData() {
        String randFileName = "mushy_daisy_4820";
        // we manually invoke new DataDirectory as a real object
        dataDirectory = new DataDirectory();
        // then, we use the tempDir as its actual data directory
        dataDirectory.setDataDir(tempDir);
        setup.setDataDirectory(dataDirectory);
        dataDirectory.writeFile("network.txt", "testnet");
        dataDirectory.mkHederaSubDir("testnet/accounts/");
        dataDirectory.writeFile("testnet/accounts/default.txt", randFileName + ":" + accountId);
    }

    public void cleanUpTestData() {
        File tempDirFolder = new File(tempDir.toString());
        boolean deleted = FileSystemUtils.deleteRecursively(tempDirFolder);
        assertTrue(deleted);
    }

    @Test
    void testSaveToJson() {
        prepareTestData();

        JsonObject accountValue = new JsonObject();
        accountValue.add("accountId", accountId);
        accountValue.add("privateKey", keyPair.getPrivateKeyHex());
        accountValue.add("publicKey", keyPair.getPublicKeyHex());

        String randFileName = "mushy_daisy_4820";
        HashMap<String, String> mHashMap = new HashMap<>();
        mHashMap.put(accountId, randFileName);
        when(randomNameGenerator.getRandomName()).thenReturn(randFileName);

        setup.saveToJson(accountId, accountValue, shellHelper);

        // read the mushy_daisy_4820.json file back from our temporary test directory
        String pathToFile = "testnet/accounts/" + randFileName + ".json";
        HashMap<String, String> jsonMap = dataDirectory.jsonToHashmap(pathToFile);

        assertEquals(accountValue.get("accountId").asString(), jsonMap.get("accountId"));
        assertEquals(accountValue.get("privateKey").asString(), jsonMap.get("privateKey"));
        assertEquals(accountValue.get("publicKey").asString(), jsonMap.get("publicKey"));

        setup.saveToJson("0.0.1235", null, shellHelper);
        cleanUpTestData();
    }

    @Test
    void accountToJsonInRightFormat() {
        JsonObject objectExpected = new JsonObject();
        objectExpected.add("accountId", accountId);
        objectExpected.add("privateKey", keyPair.getPrivateKeyHex());
        objectExpected.add("publicKey", keyPair.getPublicKeyHex());

        when(keyGeneration.generateKeysAndWords(seed, mnemonic)).thenReturn(keyPair);
        KeyPair keypairTest = keyGeneration.generateKeysAndWords(seed, mnemonic);
        JsonObject objectActual = setup.addAccountToJson(accountId, keypairTest);
        assertEquals(objectExpected, objectActual);
    }

    @Test
    void printKeyPairInRecoveredAccountModelFormat() {
        RecoveredAccountModel recoveredAccountModel;
        recoveredAccountModel = new RecoveredAccountModel();
        recoveredAccountModel.setAccountId(accountId);
        recoveredAccountModel.setPrivateKey(keyPair.getPrivateKeyHex());
        recoveredAccountModel.setPublicKey(keyPair.getPublicKeyHex());
        recoveredAccountModel.setPrivateKeyEncoded(keyPair.getPrivateKeyEncodedHex());
        recoveredAccountModel.setPublicKeyEncoded(keyPair.getPublicKeyEncodedHex());
        recoveredAccountModel.setPrivateKeyBrowserCompatible(keyPair.getSeedAndPublicKeyHex());
        setup.printKeyPair(keyPair, accountId, shellHelper);
        assertEquals(accountId, recoveredAccountModel.getAccountId());
        assertEquals(keyPair.getPrivateKeyHex(), recoveredAccountModel.getPrivateKey());
        assertEquals(keyPair.getPublicKeyHex(), recoveredAccountModel.getPublicKey());
        assertEquals(keyPair.getPrivateKeyEncodedHex(), recoveredAccountModel.getPrivateKeyEncoded());
        assertEquals(keyPair.getPublicKeyEncodedHex(), recoveredAccountModel.getPublicKeyEncoded());
        assertEquals(keyPair.getSeedAndPublicKeyHex(), recoveredAccountModel.getPrivateKeyBrowserCompatible());
    }

    @Test
    void createJsonObjWithPrivateKey() {
        JsonObject objectExpected = new JsonObject();
        objectExpected.add("accountId", accountId);
        objectExpected.add("privateKey", keyPair.getPrivateKeyEncodedHex());
        objectExpected.add("publicKey", keyPair.getPublicKeyEncodedHex());

        JsonObject objectActual = setup.addAccountToJsonWithPrivateKey(accountId, Ed25519PrivateKey.fromString(keyPair.getPrivateKeyHex()));
        assertEquals(objectExpected, objectActual);
    }

//    @Test
//    public void handleSetupWithBipRecoveryWords() {
//        prepareTestData();
//
//        String accountId = "0.0.5432";
//        Client client = new Client(AccountId.fromString("0.0.3"), "35.188.20.11:50211");
//        when(inputReader.prompt("Have you migrated your account on Hedera wallet? If migrated, enter `bip`, else enter `hgc`")).thenReturn("bip");
//        when(inputReader.prompt("account ID in the format of 0.0.xxxx that will be used as default operator")).thenReturn(accountId);
//        when(inputReader.prompt("24 words phrase", "secret", false)).thenReturn(phrase);
//        when(accountUtils.isAccountId(accountId)).thenReturn(true);
//        when(accountRecovery.recoverEDKeypairPostBipMigration(Arrays.asList(phrase.split(" ")))).thenReturn(keyPair);
//
//        q = new AccountInfoQuery(client)
//                .setAccountId(AccountId.fromString(accountId))
//                .setPayment(
//                        new CryptoTransferTransaction(null)
//                                .setTransactionId(new TransactionId(new AccountId(2), Instant.now()))
//                                .setNodeAccountId(new AccountId(3))
//                                .addSender(new AccountId(2), 10000)
//                                .addRecipient(new AccountId(3), 10000)
//                                .setTransactionFee(100_000)
//                                .sign(Ed25519PrivateKey.fromString(keyPair.getPrivateKeyHex())))
//                .setAccountId(AccountId.fromString(accountId));
//
//        assertThrows(HederaException.class, () -> {
//            when(q.execute()).thenCallRealMethod();
//        });
//
//        setup.handle(inputReader, shellHelper);
//        cleanUpTestData();
//    }
//
//    @Test
//    public void handleSetupWithHgcRecoveryWords() {
//        prepareTestData();
//
//        String accountId = "0.0.9876";
//        Client client = new Client(AccountId.fromString("0.0.3"), "35.188.20.11:50211");
//        when(inputReader.prompt("Have you migrated your account on Hedera wallet? If migrated, enter `bip`, else enter `hgc`")).thenReturn("hgc");
//        when(inputReader.prompt("account ID in the format of 0.0.xxxx that will be used as default operator")).thenReturn(accountId);
//        when(inputReader.prompt("24 words phrase", "secret", false)).thenReturn(phrase);
//        when(accountUtils.isAccountId(accountId)).thenReturn(true);
//        when(accountRecovery.recoverEd25519AccountKeypair(Arrays.asList(phrase.split(" ")), accountId, shellHelper)).thenReturn(keyPair);
//
//        q = new AccountInfoQuery(client)
//                .setAccountId(AccountId.fromString(accountId))
//                .setPayment(
//                        new CryptoTransferTransaction(null)
//                                .setTransactionId(new TransactionId(new AccountId(2), Instant.now()))
//                                .setNodeAccountId(new AccountId(3))
//                                .addSender(new AccountId(2), 10000)
//                                .addRecipient(new AccountId(3), 10000)
//                                .setTransactionFee(100_000)
//                                .sign(Ed25519PrivateKey.fromString(keyPair.getPrivateKeyHex())))
//                .setAccountId(AccountId.fromString(accountId));
//
//        assertThrows(HederaException.class, () -> {
//            when(q.execute()).thenCallRealMethod();
//        });
//        setup.handle(inputReader, shellHelper);
//        cleanUpTestData();
//    }
}
