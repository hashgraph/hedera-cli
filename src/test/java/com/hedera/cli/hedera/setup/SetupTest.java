package com.hedera.cli.hedera.setup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.hedera.cli.hedera.keygen.*;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.models.HederaAccount;
import org.hjson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SetupTest {

    @InjectMocks
    private Setup setup;

    @Mock
    private HGCSeed hgcSeed;

    @Mock
    private DataDirectory dataDirectory;

    @Mock
    private RandomNameGenerator randomNameGenerator;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveToJson() throws JsonProcessingException {
        List<String> mnemonic = Arrays.asList("hello, fine, demise, ladder, glow, hard, magnet, fan, donkey, carry, chuckle, assault, leopard, fee, kingdom, cheap, odor, okay, crazy, raven, goose, focus, shrimp, carbon");
        hgcSeed = new HGCSeed((CryptoUtils.getSecureRandomData(32)));
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
        String pathToFile = "testnet/accounts/"+ randFileName +".json";
        String pathToIndex = "testnet/accounts/index.txt";
        String pathToDefault = "testnet/accounts/default.txt";
        ObjectMapper mapper = new ObjectMapper();
        Object jsonObject = mapper.readValue(accountValue.toString(), HederaAccount.class);
        String accountValueString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

        when(dataDirectory.readFile("network.txt")).thenReturn("testnet");
        when(dataDirectory.readFile(pathToDefault, randFileName +":"+ accountId)).thenReturn(randFileName +":"+ accountId);
        doNothing().when(dataDirectory).writeFile(pathToFile, accountValueString);
        when(dataDirectory.readWriteToIndex(pathToIndex, mHashMap)).thenReturn(mHashMap);
        when(randomNameGenerator.getRandomName()).thenReturn(randFileName);

        assertEquals("testnet", dataDirectory.readFile("network.txt"));
        assertEquals(randFileName +":"+ accountId, dataDirectory.readFile(pathToDefault, randFileName +":"+ accountId));
        assertEquals(mHashMap, dataDirectory.readWriteToIndex(pathToIndex, mHashMap));
        assertEquals(randFileName, randomNameGenerator.getRandomName());
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
        List<String> mnemonic = Arrays.asList("hello, fine, demise, ladder, glow, hard, magnet, fan, donkey, carry, chuckle, assault, leopard, fee, kingdom, cheap, odor, okay, crazy, raven, goose, focus, shrimp, carbon");
        hgcSeed = new HGCSeed((CryptoUtils.getSecureRandomData(32)));
        String accountId = "0.0.1234";

        EDBip32KeyChain keyChain = new EDBip32KeyChain();
        int index = 0;
        KeyPair keyPair = keyChain.keyPairFromWordList(index, mnemonic);
        JsonObject objectExpected = new JsonObject();
        objectExpected.add("accountId", accountId);
        objectExpected.add("privateKey", keyPair.getPrivateKeyHex());
        objectExpected.add("publicKey", keyPair.getPublicKeyHex());

        KeyGeneration keyGeneration = Mockito.mock(KeyGeneration.class);
        when(keyGeneration.generateMnemonic(hgcSeed))
                .thenReturn(mnemonic);
        when(keyGeneration.generateKeysAndWords(hgcSeed, mnemonic))
                .thenReturn(keyPair);
        KeyPair keypairTest = keyGeneration.generateKeysAndWords(hgcSeed, mnemonic);
        JsonObject objectActual = setup.addAccountToJson(accountId, keypairTest);
        assertEquals(objectExpected, objectActual);
    }
}
