package com.hedera.cli.hedera.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

import com.hedera.cli.models.TransactionObj;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.util.FileSystemUtils;

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

        // prepare test data
        TimeZone tz = Calendar.getInstance().getTimeZone();
        StringBuilder sb = new StringBuilder();
        sb.append("2019-02-22T21:30:58");
        sb.append(getCurrentTimeZoneOffset());
        sb.append("[");
        sb.append(tz.getID());
        sb.append("]");
        String expected = sb.toString();

        // execute the function that we want to test
        ZonedDateTime zonedDateTime = utils.dateToMilliseconds(dateInString);
        String actual = zonedDateTime.toString();

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

    private String getCurrentTimeZoneOffset() {
        // get timezone offset
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());
        String offset = String.format("%02d:%02d", Math.abs(offsetInMillis / 3600000),
                Math.abs((offsetInMillis / 60000) % 60));
        return (offsetInMillis >= 0 ? "+" : "-") + offset;
    }
}
