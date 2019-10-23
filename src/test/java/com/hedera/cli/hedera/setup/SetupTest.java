package com.hedera.cli.hedera.setup;

import com.hedera.cli.hedera.botany.AdjectivesWordListHelper;
import com.hedera.cli.hedera.botany.BotanyWordListHelper;

import com.hedera.cli.hedera.keygen.*;
import com.hedera.cli.hedera.utils.DataDirectory;
import org.hjson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Arrays;
import java.util.List;

public class SetupTest {

    @InjectMocks
    private Setup setup;

    @Mock
    private HGCSeed hgcSeed;

    @Mock
    private DataDirectory dataDirectory;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetRandomName() {
        setup = new Setup();
        Setup spy = spy(setup);

        //optionally, you can stub out some methods:
        when(spy.getRandomName()).thenReturn("thorny_bluebell_8443");
        //using the spy calls *real* methods
        String helloSpy = spy.getRandomName();
        assertEquals("thorny_bluebell_8443", helloSpy);
        //optionally, you can verify
        verify(spy).getRandomName();

        // Or
        String randomNameActual = setup.getRandomName();
        List<String> botanyWordList = BotanyWordListHelper.words;
        List<String> adjectivesWordList = AdjectivesWordListHelper.words;
        int high = 10000;

        String adjectives = randomNameActual.split("_")[0];
        String botany = randomNameActual.split("_")[1];
        int number = Integer.valueOf(randomNameActual.split("_")[2]);
        assertTrue(botanyWordList.contains(botany));
        assertTrue(adjectivesWordList.contains(adjectives));
        assertTrue(high >= number);
    }


    @Test
    public void testSaveToJson() throws IOException {

        String pathToAccountFile = "";
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

//        doAnswer(invocation -> "adjective_botanic_number:0.0.1234").when(dataDirectory)
//                .readFile("testnet/accounts/default.txt");
//        doAnswer(invocation -> new HashMap<String, String>() {
//            {
//                put("privateKey", "somesecretprivatekey");
//            }
//        }).when(dataDirectory).jsonToHashmap("testnet/accounts/adjective_botanic_number.json");
//
//        Path mockPath = mock(Path.class);
//        FileSystem mockFileSystem = mock(FileSystem.class);
//        FileSystemProvider mockFileSystemProvider = mock(FileSystemProvider.class);
//        OutputStream mockOutputStream = mock(OutputStream.class);
//        when(mockPath.getFileSystem()).thenReturn(mockFileSystem);
//        when(mockFileSystem.provider()).thenReturn(mockFileSystemProvider);
//        when(mockFileSystemProvider.newOutputStream(any(Path.class), anyVararg())).thenReturn(mockOutputStream);
//        when(mockFileSystem.getPath(anyString(), anyVararg())).thenReturn(mockPath);
//
//// using Spring helper, but could use Java reflection
//        ReflectionTestUtils.setField(serviceToTest, "fileSystem", mockFileSystem);

//        DataDirectory dataDirectory = Mockito.mock(DataDirectory.class);
        doAnswer(invocation -> "testnet").when(dataDirectory).readFile("network.txt");
//        when(dataDirectory.readFile("network.txt")).thenReturn("testnet");
        System.out.println(dataDirectory);
        setup = new Setup();
        Setup spy = spy(setup);
        setup.saveToJson(pathToAccountFile, accountValue);
        verify(spy).saveToJson(pathToAccountFile, accountValue);
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
