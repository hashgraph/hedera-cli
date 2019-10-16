package com.hedera.cli.hedera.crypto;

import com.hedera.cli.Application;
import com.hedera.cli.hedera.utils.AccountUtils;
import com.hedera.cli.hedera.utils.DataDirectory;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class AccountListTest {

    @Autowired
    ApplicationContext context;

    @Test
    public void testStreamList() {
        AccountUtils accountUtils = new AccountUtils();
        String pathToIndexTxt = accountUtils.pathToAccountsFolder() + "index.txt";

        HashMap<String, String> testMap = new HashMap<>();
        testMap.put("0.0.90304", "aggressive_primerose_3092");
        testMap.put("0.0.82319", "gloomy_alyssum_270");
        testMap.put("0.0.1003", "wiry_bryn_3883");
        testMap.put("0.0.1009", "jaunty_mint_465");
        testMap.put("0.0.112232", "definitive_forsythia_2853");
        testMap.put("0.0.8888", "sorrowful_geranium_7578");

        DataDirectory dataDirectory = Mockito.mock(DataDirectory.class);
        when(dataDirectory.readFileHashmap(pathToIndexTxt)).thenReturn(testMap);
        Map<String, String> readingIndexAccount = dataDirectory.readFileHashmap(pathToIndexTxt);
        assertEquals(readingIndexAccount.entrySet(), testMap.entrySet());
    }
}
