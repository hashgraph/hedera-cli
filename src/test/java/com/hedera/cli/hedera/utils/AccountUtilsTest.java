package com.hedera.cli.hedera.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;

import java.util.HashMap;

import com.hedera.cli.services.CurrentAccountService;
import com.hedera.hashgraph.sdk.account.AccountId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = { CurrentAccountService.class })
public class AccountUtilsTest {

    @InjectMocks
    private AccountUtils accountUtils;

    @Mock
    private DataDirectory dataDirectory;

    @Test
    public void checkPaths() {
        doAnswer(invocation -> "testnet").when(dataDirectory).readFile("network.txt");

        assertEquals("testnet/accounts/", accountUtils.pathToAccountsFolder());
        assertEquals("testnet/accounts/index.txt", accountUtils.pathToIndexTxt());
        assertEquals("testnet/accounts/default.txt", accountUtils.pathToDefaultTxt());
    }

    @Test
    public void defaultAccountString() {
        doAnswer(invocation -> "testnet").when(dataDirectory).readFile("network.txt");
        doAnswer(invocation -> "adjective_botanic_number:0.0.1234").when(dataDirectory)
                .readFile("testnet/accounts/default.txt");

        String[] defaultAccountArray = accountUtils.defaultAccountString();
        assertEquals("adjective_botanic_number:0.0.1234".split(":")[0], defaultAccountArray[0]);
        assertEquals("adjective_botanic_number:0.0.1234".split(":")[1], defaultAccountArray[1]);
    }

    @Test
    public void retrieveDefaultAccountID() {
        doAnswer(invocation -> "testnet").when(dataDirectory).readFile("network.txt");
        doAnswer(invocation -> "adjective_botanic_number:0.0.1234").when(dataDirectory)
                .readFile("testnet/accounts/default.txt");

        AccountId accountId = accountUtils.retrieveDefaultAccountID();
        assertEquals("0.0.1234", accountId.toString());
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
        }).when(dataDirectory).jsonToHashmap("testnet/accounts/adjective_botanic_number.json");

        String publicKey = accountUtils.retrieveDefaultAccountPublicKeyInHexString();
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
        }).when(dataDirectory).jsonToHashmap("testnet/accounts/adjective_botanic_number.json");

        String privateKey = accountUtils.retrieveDefaultAccountKeyInHexString();
        assertEquals("somesecretprivatekey", privateKey);
    }

}