package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.shell.ShellHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class KryptoKransferTest {

    @InjectMocks
    private KryptoKransfer kryptoKransfer;

    @Mock
    private Hedera hedera;

    @Mock
    private CryptoTransferValidation cryptoTransferValidation;

    @Mock
    private ShellHelper shellHelper;

    @Test
    public void transactionAmountNotValid() {
        kryptoKransfer.transactionAmountNotValid();
        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).printError(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "You have to provide transfer amounts either in hbars or tinybars";
        assertEquals(expected, actual);
    }

    @Test
    public void transactionAmountNotValidBoth() {
        kryptoKransfer.setTinybarListArgs("100");
        kryptoKransfer.setHbarListArgs("0.1");
        when(cryptoTransferValidation.tinybarListArgs()).thenReturn(kryptoKransfer.getTinybarListArgs());
        when(cryptoTransferValidation.hbarListArgs()).thenReturn(kryptoKransfer.getHbarListArgs());
        kryptoKransfer.transactionAmountNotValid();
        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).printError(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "Transfer amounts must either be in hbars or tinybars, not both";
        assertEquals(expected, actual);
    }

    @Test
    public void senderListReturnsIfEmptyOrNull() {
        when(cryptoTransferValidation.senderListArgs()).thenReturn("");
        List<String> senderList = kryptoKransfer.senderList();
        assertNull(senderList);
    }

    @Test
    public void senderListSize1() {
        String accountId = "0.0.1001";
        kryptoKransfer.setSenderListArgs(accountId);
        when(cryptoTransferValidation.senderListArgs()).thenReturn(kryptoKransfer.getSenderListArgs());
        assertEquals(1, kryptoKransfer.senderList().size());
        assertEquals(accountId, kryptoKransfer.senderList().get(0));
    }

    @Test
    public void recipientListNotValid() {
        kryptoKransfer.recipientList();
    }

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
        List<String> senderList = new ArrayList<>();
        senderList.add("0.0.1002");
        List<String> recipientList = new ArrayList<>();
        recipientList.add("0.0.1003");
        recipientList.add("0.0.1004");
        List<String> actualList = kryptoKransfer.appendTransferList(senderList, recipientList);
        List<String> expectedList = new ArrayList<>();
        expectedList.add("0.0.1002");
        expectedList.add("0.0.1003");
        expectedList.add("0.0.1004");
        assertEquals(expectedList, actualList);
    }
}
