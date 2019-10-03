package com.hedera.cli.hedera.utils;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.services.CurrentAccountService;
import com.hedera.hashgraph.sdk.account.AccountId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {CurrentAccountService.class})
public class AccountUtilsTest {

    @Autowired
    ApplicationContext context;
//    @Test
//    public void testCurrentOrDefaultAccountString() {
//        String defaultPath = "default.txt";
//        AccountUtils accountUtils = Mockito.mock(AccountUtils.class);
//        when(accountUtils
//                .currentOrDefaultAccountString(defaultPath))
//                .thenReturn("adjective_botanic_number:0.0.zzzz".split(":"));
//        String defaultAccountId = accountUtils.currentOrDefaultAccountString("default.txt")[1];
//        assertEquals("0.0.zzzz", defaultAccountId);
//    }

    @Test
    public void testRetrieveAccountString() {
        AccountUtils accountUtils = Mockito.mock(AccountUtils.class);
        when(accountUtils
                .defaultAccountString())
                .thenReturn("adjective_botanic_number:0.0.xxxx".split(":"));
        String accountId = accountUtils.defaultAccountString()[1];
        assertEquals("0.0.xxxx", accountId);
    }

    @Test
    public void testRetrieveDefaultAccountID() {
        AccountUtils accountUtils = Mockito.mock(AccountUtils.class);
        when(accountUtils
                .retrieveDefaultAccountID())
                .thenReturn(AccountId.fromString("0.0.1234"));
        AccountId accountId = accountUtils.retrieveDefaultAccountID();
        assertEquals(AccountId.fromString("0.0.1234"), accountId);
    }

    @Test
    public void testRetrieveDefaultAccountPublicKeyInHexString() {
        AccountUtils accountUtils = Mockito.mock(AccountUtils.class);
        when(accountUtils
                .retrieveDefaultAccountPublicKeyInHexString())
                .thenReturn("publicKeyInStringDerOrASN1Format");
        String publicKey = accountUtils.retrieveDefaultAccountPublicKeyInHexString();
        assertEquals("publicKeyInStringDerOrASN1Format", publicKey);
    }

    @Test
    public void testRetrieveDefaultAccountKeyInHexString() {
        AccountUtils accountUtils = Mockito.mock(AccountUtils.class);
        when(accountUtils.retrieveDefaultAccountKeyInHexString())
                .thenReturn("privateKeyInStringDerOrASN1Format");
        String privateKey = accountUtils.retrieveDefaultAccountKeyInHexString();
        assertEquals("privateKeyInStringDerOrASN1Format", privateKey);
    }

//    @Test
//    public void testRetrieveIndexAccountKeyInHexString() {
//
//        HashMap<String, String> mHashmap = new HashMap<>();
//        mHashmap.put("0.0.9998", "filename_001");
//        mHashmap.put("0.0.7777", "filename_007");
//
//        Hedera hedera = new Hedera(context);
//        hedera.retrieveIndexAccountKeyInHexString();
//
//    }
}