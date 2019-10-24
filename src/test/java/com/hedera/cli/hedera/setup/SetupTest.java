package com.hedera.cli.hedera.setup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hedera.cli.hedera.keygen.CryptoUtils;
import com.hedera.cli.hedera.keygen.EDBip32KeyChain;
import com.hedera.cli.hedera.keygen.HGCSeed;
import com.hedera.cli.hedera.keygen.KeyGeneration;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.hedera.utils.DataDirectory;

import org.hjson.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.FileSystemUtils;

import io.github.netmikey.logunit.api.LogCapturer;

@ExtendWith(MockitoExtension.class)
public class SetupTest {

    @RegisterExtension
    private LogCapturer logs = LogCapturer.create().captureForType(Setup.class);

    @InjectMocks
    private Setup setup;

    @TempDir
    public Path tempDir;

    // not a mock
    private DataDirectory dataDirectory;

    @Mock
    private RandomNameGenerator randomNameGenerator;

    @Mock
    private KeyGeneration keyGeneration;

    public void prepareTestData() {
        // test data
        String accountId = "0.0.1234";
        String randFileName = "mushy_daisy_4820";

        // we manually invoke new DataDirectory as a real object
        dataDirectory = new DataDirectory();
        // then, we use the tempDir as its actual data directory
        dataDirectory.setDataDir(tempDir);

        when(randomNameGenerator.getRandomName()).thenReturn(randFileName);
        setup.setDataDirectory(dataDirectory);

        dataDirectory.writeFile("network.txt", "testnet");
        dataDirectory.mkHederaSubDir("testnet/accounts/");
        dataDirectory.writeFile("testnet/accounts/default.txt", randFileName + ":" + accountId);
    }

    public void cleanup() {
        File tempDirFolder = new File(tempDir.toString());
        boolean deleted = FileSystemUtils.deleteRecursively(tempDirFolder);
        System.out.println("Deleted: " + deleted);
    }

    @Test
    public void testSaveToJson() throws JsonProcessingException {
        prepareTestData();

        List<String> mnemonic = Arrays.asList(
                "hello, fine, demise, ladder, glow, hard, magnet, fan, donkey, carry, chuckle, assault, leopard, fee, kingdom, cheap, odor, okay, crazy, raven, goose, focus, shrimp, carbon");
        String accountId = "0.0.1234";

        EDBip32KeyChain keyChain = new EDBip32KeyChain();
        int index = 0;
        KeyPair keyPair = keyChain.keyPairFromWordList(index, mnemonic);
        JsonObject accountValue = new JsonObject();
        accountValue.add("accountId", accountId);
        accountValue.add("privateKey", keyPair.getPrivateKeyHex());
        accountValue.add("publicKey", keyPair.getPublicKeyHex());

        String randFileName = "mushy_daisy_4820";
        HashMap<String, String> mHashMap = new HashMap<>();
        mHashMap.put(accountId, randFileName);

        setup.saveToJson(accountId, accountValue);

        // read the mushy_daisy_4820.json file back from our temporary test directory
        String pathToFile = "testnet/accounts/" + randFileName + ".json";
        HashMap<String, String> jsonMap = dataDirectory.jsonToHashmap(pathToFile);

        assertEquals(accountValue.get("accountId").asString(), jsonMap.get("accountId").toString());
        assertEquals(accountValue.get("privateKey").asString(), jsonMap.get("privateKey").toString());
        assertEquals(accountValue.get("publicKey").asString(), jsonMap.get("publicKey").toString());

 
        setup.saveToJson("0.0.1235", null);
        logs.assertContains("did not save json");

        cleanup();
    }

    @Test
    public void phraseListSizeTrue() {
        String wordList = "once busy dash argue stuff quarter property west tackle swamp enough brisk split code borrow ski soccer tip churn kitten congress admit april defy";
        List<String> phraseList = Arrays.asList(wordList.split(" "));
        assertTrue(setup.phraseListSize(phraseList));
    }

    @Test
    public void phraseListSizeFalse() {
        String wordList = "dash argue stuff quarter property west tackle swamp enough brisk split code borrow ski soccer tip churn kitten congress admit april defy";
        List<String> phraseList = Arrays.asList(wordList.split(" "));
        assertFalse(setup.phraseListSize(phraseList));
    }

    @Test
    public void accountToJsonInRightFormat() {
        List<String> mnemonic = Arrays.asList(
                "hello, fine, demise, ladder, glow, hard, magnet, fan, donkey, carry, chuckle, assault, leopard, fee, kingdom, cheap, odor, okay, crazy, raven, goose, focus, shrimp, carbon");
        HGCSeed hgcSeed = new HGCSeed((CryptoUtils.getSecureRandomData(32)));
        String accountId = "0.0.1234";

        EDBip32KeyChain keyChain = new EDBip32KeyChain();
        int index = 0;
        KeyPair keyPair = keyChain.keyPairFromWordList(index, mnemonic);
        JsonObject objectExpected = new JsonObject();
        objectExpected.add("accountId", accountId);
        objectExpected.add("privateKey", keyPair.getPrivateKeyHex());
        objectExpected.add("publicKey", keyPair.getPublicKeyHex());

        when(keyGeneration.generateKeysAndWords(hgcSeed, mnemonic)).thenReturn(keyPair);
        KeyPair keypairTest = keyGeneration.generateKeysAndWords(hgcSeed, mnemonic);
        JsonObject objectActual = setup.addAccountToJson(accountId, keypairTest);
        assertEquals(objectExpected, objectActual);
    }
}
