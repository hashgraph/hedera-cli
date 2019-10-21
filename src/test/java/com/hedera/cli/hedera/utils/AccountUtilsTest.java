package com.hedera.cli.hedera.utils;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hedera.cli.services.CurrentAccountService;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {CurrentAccountService.class})
public class AccountUtilsTest {
    
    @InjectMocks
    private AccountUtils accountUtils;

    @Mock
    private DataDirectory dataDirectory;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void defaultAccountString() {
        String testDataInDefaultFile = "adjective_botanic_number:0.0.1001";
        when(dataDirectory.readFile(any())).thenReturn(testDataInDefaultFile);
        String[] defaultAccountArray = accountUtils.defaultAccountString();
        assertEquals(testDataInDefaultFile.split(":")[0], defaultAccountArray[0]);
        assertEquals(testDataInDefaultFile.split(":")[1], defaultAccountArray[1]);
    }

    @Test
    public void retrieveDefaultAccountID() {
        // to be completed
    }

    // @Test
    // public void testRetrieveDefaultAccountID() {
    //     AccountUtils accountUtils = Mockito.mock(AccountUtils.class);
    //     when(accountUtils
    //             .retrieveDefaultAccountID())
    //             .thenReturn(AccountId.fromString("0.0.1234"));
    //     AccountId accountId = accountUtils.retrieveDefaultAccountID();
    //     assertEquals(AccountId.fromString("0.0.1234"), accountId);
    // }

    // @Test
    // public void testRetrieveDefaultAccountPublicKeyInHexString() {
    //     AccountUtils accountUtils = Mockito.mock(AccountUtils.class);
    //     when(accountUtils
    //             .retrieveDefaultAccountPublicKeyInHexString())
    //             .thenReturn("publicKeyInStringDerOrASN1Format");
    //     String publicKey = accountUtils.retrieveDefaultAccountPublicKeyInHexString();
    //     assertEquals("publicKeyInStringDerOrASN1Format", publicKey);
    // }

    // @Test
    // public void testRetrieveDefaultAccountKeyInHexString() {
    //     AccountUtils accountUtils = Mockito.mock(AccountUtils.class);
    //     when(accountUtils.retrieveDefaultAccountKeyInHexString())
    //             .thenReturn("privateKeyInStringDerOrASN1Format");
    //     String privateKey = accountUtils.retrieveDefaultAccountKeyInHexString();
    //     assertEquals("privateKeyInStringDerOrASN1Format", privateKey);
    // }

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