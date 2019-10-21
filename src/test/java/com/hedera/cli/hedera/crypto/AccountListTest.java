package com.hedera.cli.hedera.crypto;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import com.hedera.cli.hedera.utils.AccountUtils;
import com.hedera.cli.hedera.utils.DataDirectory;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountListTest {

    @InjectMocks
    private AccountUtils accountUtils;

    @Mock
    private DataDirectory dataDirectory;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testStreamList() {
        String pathToIndexTxt = accountUtils.pathToIndexTxt();

        HashMap<String, String> testMap = new HashMap<>();
        testMap.put("0.0.90304", "aggressive_primerose_3092");
        testMap.put("0.0.82319", "gloomy_alyssum_270");
        testMap.put("0.0.1003", "wiry_bryn_3883");
        testMap.put("0.0.1009", "jaunty_mint_465");
        testMap.put("0.0.112232", "definitive_forsythia_2853");
        testMap.put("0.0.8888", "sorrowful_geranium_7578");

        when(dataDirectory.readIndexToHashmap(pathToIndexTxt)).thenReturn(testMap);
        Map<String, String> readingIndexAccount = dataDirectory.readIndexToHashmap(pathToIndexTxt);
        assertTrue(readingIndexAccount.entrySet().equals(testMap.entrySet()));
    }
}
