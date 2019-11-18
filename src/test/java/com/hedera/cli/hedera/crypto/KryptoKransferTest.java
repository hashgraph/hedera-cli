package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.shell.ShellHelper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class KryptoKransferTest {

    @InjectMocks
    private CryptoTransfer cryptoTransfer;

    @Mock
    private CryptoTransferOptions cryptoTransferOptions;

    @Mock
    private Hedera hedera;

    @Mock
    private ValidateAccounts validateAccounts;

    @Mock
    private ShellHelper shellHelper;

    @Test
    public void senderListReturnsIfEmptyOrNull() {
//        when(validateAccounts.getSenderListArgs()).thenReturn("");
//        List<String> senderList = validateAccounts.getSenderList();
//        List<String> emptySenderList = new ArrayList<>();
//        assertEquals(emptySenderList, senderList);
    }

    @Test
    public void senderListSize1() {
//        String accountId = "0.0.1001";
//        cryptoTransferValidateAccounts.setSenderListArgs(accountId);
//        when(cryptoTransferValidateAccounts.senderListArgs()).thenReturn(cryptoTransferValidateAccounts.getSenderListArgs());
//        assertEquals(1, cryptoTransferValidateAccounts.senderList().size());
//        assertEquals(accountId, cryptoTransferValidateAccounts.senderList().get(0));
    }
//
//    @Test
//    public void recipientListNotValid() {
//        cryptoTransferValidateAccounts.recipientList();
//    }

//    @Test
//    public void senderListSize1WithOperator() {
//        List<String> senderList = new ArrayList<>();
//        senderList.add("0.0.1001");
//        kryptoKransfer.setSenderList(senderList);
//        when(hedera.getOperatorId()).thenReturn(AccountId.fromString(accountId));
//        cryptoTransferValidation.senderListContainsOperator(senderList);
//    }
//
//    @Test
//    public void senderListSize1NoOperator() {
//        List<String> senderList = new ArrayList<>();
//        senderList.add("0.0.1002");
//        kryptoKransfer.setSenderList(senderList);
//        when(hedera.getOperatorId()).thenReturn(AccountId.fromString(accountId));
//        cryptoTransferValidation.senderListContainsOperator(senderList);
//    }
//
//    @Test
//    public void senderListSize2WithOperator() {
//        List<String> senderList = new ArrayList<>();
//        senderList.add("0.0.1001");
//        senderList.add("0.0.1002");
//        kryptoKransfer.setSenderList(senderList);
//        when(hedera.getOperatorId()).thenReturn(AccountId.fromString(accountId));
//        cryptoTransferValidation.senderListContainsOperator(senderList);
//    }
//
//    @Test
//    public void senderListSize2NoOperator() {
//        List<String> senderList = new ArrayList<>();
//        senderList.add("0.0.1002");
//        senderList.add("0.0.1003");
//        kryptoKransfer.setSenderList(senderList);
//        when(hedera.getOperatorId()).thenReturn(AccountId.fromString(accountId));
//        cryptoTransferValidation.senderListContainsOperator(senderList);
//    }

    @Test
    public void appendTransferList() {
//        List<String> senderList = new ArrayList<>();
//        senderList.add("0.0.1002");
//        List<String> recipientList = new ArrayList<>();
//        recipientList.add("0.0.1003");
//        recipientList.add("0.0.1004");
//        List<String> actualList = cryptoTransferValidateAccounts.createTransferList(senderList, recipientList);
//        List<String> expectedList = new ArrayList<>();
//        expectedList.add("0.0.1002");
//        expectedList.add("0.0.1003");
//        expectedList.add("0.0.1004");
//        assertEquals(expectedList, actualList);
    }
}
