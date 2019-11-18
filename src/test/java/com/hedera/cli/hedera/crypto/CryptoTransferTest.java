package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.TransactionManager;
import com.hedera.cli.shell.ShellHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CryptoTransferTest {

    @InjectMocks
    private CryptoTransfer cryptoTransfer;

    @Mock
    private ShellHelper shellHelper;

    @Mock
    private AccountManager accountManager;

    @Mock
    private ValidateAmount validateAmount;

    @Mock
    private ValidateAccounts validateAccounts;

    @Mock
    private Hedera hedera;

    @Mock
    private InputReader inputReader;

    @Mock
    private TransactionManager transactionManager;

    private CryptoTransferOptions cryptoTransferOptions;
    private CryptoTransferOptions.Exclusive exclusive;
    private CryptoTransferOptions.Dependent dependent;

    @BeforeEach
    public void setUp() {
        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(false);
        dependent.setRecipientList("0.0.114152,0.0.11667");
        dependent.setSenderList("0.0.116681,0.0.117813");

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtHBars("0.4,0.01,1.33");
        exclusive.setTransferListAmtTinyBars("400,1000,10030");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        cryptoTransfer.setO(cryptoTransferOptions);
    }

    @Test
    public void assertGettersExists() {
        assertEquals(shellHelper, cryptoTransfer.getShellHelper());
        assertEquals(accountManager, cryptoTransfer.getAccountManager());
        assertEquals(hedera, cryptoTransfer.getHedera());
        assertEquals(inputReader, cryptoTransfer.getInputReader());
        assertEquals(transactionManager, cryptoTransfer.getTransactionManager());
        assertEquals(cryptoTransferOptions, cryptoTransfer.getO());
        assertEquals(dependent, cryptoTransfer.getO().getDependent());
        assertEquals(exclusive, cryptoTransfer.getO().getExclusive());
    }

////    @Test
////    public void transferListToPromptPreviewMap() {
////        List<String> senderList = Arrays.asList("0.0.116681".split(","));
////        List<String> recipientList = Arrays.asList("0.0.117813".split(","));
////        List<String> transferList = Arrays.asList(("0.0.116681,0.0.117813").split(","));
////        List<String> amountList = Arrays.asList(("-100,100").split(","));
////
////        Map<Integer, PreviewTransferList> expectedMap = new HashMap<>();
////        PreviewTransferList previewTransferList = new PreviewTransferList(AccountId.fromString("0.0.116681"), "-100");
////        PreviewTransferList previewTransferList1 = new PreviewTransferList(AccountId.fromString("0.0.117813"), "100");
////        expectedMap.put(0, previewTransferList);
////        expectedMap.put(1, previewTransferList1);
////
////        Map<Integer, PreviewTransferList> actualMap = cryptoTransfer.transferListToPromptPreviewMap(senderList, recipientList, transferList, amountList);
////        assertEquals(expectedMap.get(0).getAccountId(), actualMap.get(0).getAccountId());
////    }


////    @Test
////    public void runFailstbarsMismatchedTransferListAmountList() {
////        // test data: replace specified hb and specified tb with blank string
////        exclusive.setTransferListAmtHBars("");
////        cryptoTransferOptions.setExclusive(exclusive);
////        cryptoTransfer.setCryptoTransferOptions(cryptoTransferOptions);
////        List<CryptoTransferOptions> cList = Arrays.asList(cryptoTransferOptions);
////        cryptoTransfer.setCryptoTransferOptionsList(cList);
////
////        // execute
////        cryptoTransfer.run();
////
////        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
////        verify(shellHelper).printError(valueCapture.capture());
////        String actual = valueCapture.getValue();
////        String expected = "Lists aren't the same size";
////        assertEquals(expected, actual);
////    }
//
////    @Test
////    public void runFailshbarsMismatchedTransferListAmountList() {
////        // test data: replace specified hb and specified tb with blank string
////        exclusive.setTransferListAmtTinyBars("");
////        cryptoTransferOptions.setExclusive(exclusive);
////        cryptoTransfer.setCryptoTransferOptions(cryptoTransferOptions);
////        List<CryptoTransferOptions> cList = Arrays.asList(cryptoTransferOptions);
////        cryptoTransfer.setCryptoTransferOptionsList(cList);
////
////        // execute
////        cryptoTransfer.run();
////
////        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
////        verify(shellHelper).printError(valueCapture.capture());
////        String actual = valueCapture.getValue();
////        String expected = "Lists aren't the same size";
////        assertEquals(expected, actual);
////    }
////
////    @Test
////    public void runFailsMatchedTransferListAmountListButFailsTransferListVerification() {
////        dependent.setSenderList("0.116681");
////        cryptoTransferOptions.setDependent(dependent);
////        exclusive.setTransferListAmtTinyBars("");
////        cryptoTransferOptions.setExclusive(exclusive);
////        cryptoTransfer.setCryptoTransferOptions(cryptoTransferOptions);
////        List<CryptoTransferOptions> cList = Arrays.asList(cryptoTransferOptions);
////        cryptoTransfer.setCryptoTransferOptionsList(cList);
////
////        // execute
////        cryptoTransfer.run();
////
////        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
////        verify(shellHelper).printError(valueCapture.capture());
////        String actual = valueCapture.getValue();
////        System.out.println("actual: " + actual);
////        String expected = "Please check that accountId is in the right format";
////        assertEquals(expected, actual);
////    }
////
////
////    @Test
////    public void runValidButFailsYesNo() {
////        dependent.setSenderList("0.0.116681");
////        cryptoTransferOptions.setDependent(dependent);
////        exclusive.setTransferListAmtTinyBars("");
////        exclusive.setTransferListAmtHBars("-1000,400,600");
////        cryptoTransferOptions.setExclusive(exclusive);
////        cryptoTransfer.setCryptoTransferOptions(cryptoTransferOptions);
////        List<CryptoTransferOptions> cList = Arrays.asList(cryptoTransferOptions);
////        cryptoTransfer.setCryptoTransferOptionsList(cList);
////
////        when(accountManager.isAccountId(anyString())).thenReturn(true);
////
////        // execute
////        cryptoTransfer.run();
////
////        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
////        verify(shellHelper).printError(valueCapture.capture());
////        String actual = valueCapture.getValue();
////        String expected = "Input must be either yes or no";
////        assertEquals(expected, actual);
////    }
//
//    @Test
//    public void assertAutowiredDependenciesNotNull() {
//        assertNotNull(shellHelper);
//        assertNotNull(accountManager);
//        assertNotNull(hedera);
//        assertNotNull(inputReader);
//        assertNotNull(transactionManager);
//        assertNotNull(cryptoTransferOptions);
//    }
//
//    @Test
//    public void tinyBarsLoop() {
//        cryptoTransferOptions = new CryptoTransferOptions();
//        cryptoTransfer.setHbarAmtArgs("0.4,0.01,1.33");
//        cryptoTransfer.setTinybarAmtArgs("400,1000,10030");
//        cryptoTransfer.setTransferListArgs("0.0.116681,0.0.117813,0.0.114152,0.0.11667");
//        cryptoTransfer.setSkipPreview(false);
//
//        assertEquals(dependent.isSkipPreview(), cryptoTransfer.isSkipPreview());
//        assertEquals(exclusive.transferListAmtTinyBars, cryptoTransfer.getTinybarAmtArgs());
//        assertEquals(exclusive.transferListAmtHBars, cryptoTransfer.getHbarAmtArgs());
//        assertEquals(dependent.senderList + "," + dependent.recipientList, cryptoTransfer.getTransferListArgs());
//        assertNull(null, cryptoTransfer.getHbarAmtArgs());
//    }
////
////    @Test
////    public void verifyEqualListReturnsTrue() {
////        List<String> senderList = Arrays.asList("0.0.116681,0.0.117813".split(","));
////        List<String> recipientList = Arrays.asList("0.0.114152,0.0.11667".split(","));
////        List<String> transferList = Arrays.asList("0.0.116681,0.0.117813,0.0.114152,0.0.11667".split(","));
////        List<String> amountList = Arrays.asList(("-100,-100,100,100").split(","));
////        assertTrue(cryptoTransfer.verifyEqualList(senderList, recipientList, transferList, amountList));
////    }
//
////    @Test
////    public void verifyEqualListReturnsFalse() {
////        List<String> senderList = Arrays.asList("0.0.116681".split(","));
////        List<String> recipientList = Arrays.asList("0.0.114152,0.0.11667".split(","));
////        List<String> transferList = Arrays.asList(("0.0.116681,0.0.114152,0.0.11667").split(","));
////        List<String> amountList = Arrays.asList(("-100,-100,100,100").split(","));
////        assertFalse(cryptoTransfer.verifyEqualList(senderList, recipientList, transferList, amountList));
////
////        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
////        doNothing().when(shellHelper).printError(valueCapture.capture());
////        cryptoTransfer.verifyEqualList(senderList, recipientList, transferList, amountList);
////        String actual = valueCapture.getValue();
////        String expected = "Lists aren't the same size";
////        assertEquals(expected, actual);
////    }
//
////    @Test
////    public void verifyTransferListReturnsFalse() {
////        List<String> senderList = Arrays.asList("0.117813".split(","));
////        List<String> recipientList = Arrays.asList("114152,0.0.11667".split(","));
////        List<String> transferList = Arrays.asList(("0.117813,114152,0.0.11667").split(","));
////        List<String> amountList = Arrays.asList("-100,100,100".split(","));
////        when(accountManager.isAccountId("0.117813")).thenReturn(false);
////        assertTrue(cryptoTransfer.verifyEqualList(senderList, recipientList, transferList, amountList));
////
////        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
////        doNothing().when(shellHelper).printError(valueCapture.capture());
////        cryptoTransfer.verifyTransferList(transferList);
////        String actual = valueCapture.getValue();
////        String expected = "Please check that accountId is in the right format";
////        assertEquals(expected, actual);
////    }
//
//    @Test
//    public void verifyTransferListReturnsTrue() {
//        List<String> transferList = Arrays.asList(("0.0.116681,0.0.117813,0.0.114152,0.0.11667").split(","));
//        when(accountManager.isAccountId("0.0.116681")).thenReturn(true);
//        when(accountManager.isAccountId("0.0.117813")).thenReturn(true);
//        when(accountManager.isAccountId("0.0.114152")).thenReturn(true);
//        when(accountManager.isAccountId("0.0.11667")).thenReturn(true);
//        assertTrue(cryptoTransfer.verifyTransferList(transferList));
//    }
}
