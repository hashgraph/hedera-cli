package com.hedera.cli.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.FileSystemUtils;

@ExtendWith(MockitoExtension.class)
public class TransactionManagerTest {

    @TempDir
    public Path tempDir;

    private DataDirectory dataDirectory;

    // class under test
    @InjectMocks
    private TransactionManager txManager;

    @BeforeEach
    public void setUp() {
        // manual invocation of DataDirectory
        dataDirectory = new DataDirectory();
        dataDirectory.setDataDir(tempDir);
        txManager = new TransactionManager();
        txManager.init();
        txManager.setDataDirectory(dataDirectory);

        String accountId = "0.0.1234";
        String randFileName = "mushy_daisy_4820";
        dataDirectory.writeFile("network.txt", "testnet");
        dataDirectory.mkHederaSubDir("testnet/accounts/");
        dataDirectory.writeFile("testnet/accounts/default.txt", randFileName + ":" + accountId);
    }

    @AfterEach
    public void tearDown() throws IOException {
        FileSystemUtils.deleteRecursively(tempDir);
    }

    @Test
    public void dateToMilliseconds() throws ParseException {
        // raw test data
        String[] dateInString = { "22-02-2019", "21:30:58" };

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = formatter.parse("22-02-2019 21:30:58");
        String expected = date.toInstant().toString();

        // execute the function that we want to test
        Instant instant = txManager.dateToMilliseconds(dateInString);
        String actual = instant.toString();

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void saveTransactionsToJson() {
        TransactionObj txObj = new TransactionObj();
        String txId = "sometransactionid";
        txObj.setTxID(txId);
        txObj.setTxFee(100000000L);
        try {
            txManager.saveTransactionsToJson(txId, txObj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String networkName = dataDirectory.readFile("network.txt");
        String pathToTransactionFolder = networkName + File.separator + "transactions" + File.separator;
        String filename = txId + ".json";
        String pathToTransactionFile = pathToTransactionFolder + filename;
        HashMap<String, String> transactionHashMap = dataDirectory.readJsonToHashmap(pathToTransactionFile);

        assertEquals("sometransactionid", transactionHashMap.get("txID"));
        assertEquals("100000000", transactionHashMap.get("txFee"));

        assertNotNull(txManager.getDataDirectory());
        assertNotNull(txManager.getObjectWriter());
    }

    @Test
    public void saveTransactionToJsonFails() throws Exception {

        // deliberately create a mock ow and set it to our TransactionManager class
        // overriding the real ow
        ObjectWriter ow = mock(ObjectWriter.class);
        when(ow.writeValueAsString(any(Object.class))).thenThrow(mock(JsonProcessingException.class));

        txManager.setObjectWriter(ow);

        assertThrows(JsonProcessingException.class, () -> {
            TransactionObj txObj = new TransactionObj();
            String txId = "sometransactionid";
            txObj.setTxID(txId);
            txObj.setTxFee(100000000L);
            txManager.saveTransactionsToJson(txId, txObj);
        });
    }
}
