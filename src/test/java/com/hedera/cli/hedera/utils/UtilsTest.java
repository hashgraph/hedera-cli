package com.hedera.cli.hedera.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

import com.hedera.cli.models.TransactionObj;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.FileSystemUtils;

@ExtendWith(MockitoExtension.class)
public class UtilsTest {

    @TempDir
    public Path tempDir;

    private DataDirectory dataDirectory;

    // class under test
    private Utils utils;

    @BeforeEach
    public void setUp() {
        // manual invocation of DataDirectory
        dataDirectory = new DataDirectory();
        dataDirectory.setDataDir(tempDir);
        utils = new Utils();
        utils.setDataDirectory(dataDirectory);

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
        Instant instant = utils.dateToMilliseconds(dateInString);
        String actual = instant.toString();

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void saveTransactionsToJson() {
        TransactionObj txObj = new TransactionObj();
        String txID = "sometransactionid";
        txObj.setTxID(txID);
        txObj.setTxFee(100000000L);
        utils.saveTransactionsToJson(txID, txObj);

        String networkName = dataDirectory.readFile("network.txt");
        String pathToTransactionFolder = networkName + File.separator + "transactions" + File.separator;
        String filename = txID + ".json";
        String pathToTransactionFile = pathToTransactionFolder + filename;
        HashMap<String, String> transactionHashMap = dataDirectory.jsonToHashmap(pathToTransactionFile);

        assertEquals("sometransactionid", transactionHashMap.get("txID"));
        assertEquals("100000000", transactionHashMap.get("txFee"));
    }
}
