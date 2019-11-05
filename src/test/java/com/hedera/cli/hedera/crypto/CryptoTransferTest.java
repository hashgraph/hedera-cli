package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.*;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.PreviewTransferList;
import com.hedera.cli.models.TransactionManager;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;
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
        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransfer.setCryptoTransferOptions(cryptoTransferOptions);
        dependent = new CryptoTransferOptions.Dependent();
        exclusive = new CryptoTransferOptions.Exclusive();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        dependent.setSkipPreview(false);
        dependent.setRecipientList("0.0.114152,0.0.11667");
        dependent.setSenderList("0.0.116681,0.0.117813");
        exclusive.setTransferListAmtHBars("0.4,0.01,1.33");
        exclusive.setTransferListAmtTinyBars("400,1000,10030");
    }

    @Test
    public void assertGettersExists() {
        assertEquals(shellHelper, cryptoTransfer.getShellHelper());
        assertEquals(accountManager, cryptoTransfer.getAccountManager());
        assertEquals(hedera, cryptoTransfer.getHedera());
        assertEquals(inputReader, cryptoTransfer.getInputReader());
        assertEquals(transactionManager, cryptoTransfer.getTransactionManager());
        assertEquals(cryptoTransferOptions, cryptoTransfer.getCryptoTransferOptions());
        assertEquals(dependent, cryptoTransfer.getCryptoTransferOptions().getDependent());
        assertEquals(exclusive, cryptoTransfer.getCryptoTransferOptions().getExclusive());
    }

    @Test
    public void assertAutowiredDependenciesNotNull() {
        assertNotNull(shellHelper);
        assertNotNull(accountManager);
        assertNotNull(hedera);
        assertNotNull(inputReader);
        assertNotNull(transactionManager);
        assertNotNull(cryptoTransferOptions);
    }

    @Test
    public void tinyBarsLoop() {

        cryptoTransferOptions = new CryptoTransferOptions();

//        List<CryptoTransferOptions> cryptoTransferOptionsList = Arrays.asList(
//                new CryptoTransferOptions(),
//                new CryptoTransferOptions()
//        );

        cryptoTransfer.setHbarAmtArgs("0.4,0.01,1.33");
        cryptoTransfer.setTinybarAmtArgs("400,1000,10030");
        cryptoTransfer.setTransferListArgs("0.0.116681,0.0.117813,0.0.114152,0.0.11667");
        cryptoTransfer.setSkipPreview(false);

        assertEquals(dependent.isSkipPreview(), cryptoTransfer.isSkipPreview());
        assertEquals(exclusive.transferListAmtTinyBars, cryptoTransfer.getTinybarAmtArgs());
        assertEquals(exclusive.transferListAmtHBars, cryptoTransfer.getHbarAmtArgs());
        assertEquals(dependent.senderList + "," + dependent.recipientList, cryptoTransfer.getTransferListArgs());
        assertNull(null, cryptoTransfer.getHbarAmtArgs());
//
//        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
//        doNothing().when(shellHelper).printInfo(valueCapture.capture());
//        cryptoTransfer.run();
//        String actual = valueCapture.getValue();
//        String expected = "here in tiny loop";
//        assertEquals(expected, actual);
    }

//    @Test
//    public void hBarsLoop() {
//        cryptoTransferOptions.dependent.senderList = "0.0.116681,0.0.117813";
//        cryptoTransferOptions.dependent.recipientList = "0.0.114152,0.0.11667";
//        cryptoTransferOptions.exclusive.recipientAmtTinyBars = null;
//        cryptoTransferOptions.exclusive.recipientAmtHBars = "20,-20,20,20";
//        cryptoTransferOptions.dependent.mPreview = "no";
//
//        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
//        doNothing().when(shellHelper).printInfo(valueCapture.capture());
//        cryptoTransfer.run();
//        String actual = valueCapture.getValue();
//        String expected = "here in hbar loop";
//        assertEquals(expected, actual);
//    }

//    @Test
//    public void noArgsLoopReturnsError() {
//        cryptoTransferOptions.dependent.senderList = "0.0.116681,0.0.117813";
//        cryptoTransferOptions.dependent.recipientList = "0.0.114152,0.0.11667";
//        cryptoTransferOptions.exclusive.recipientAmtTinyBars = null;
//        cryptoTransferOptions.exclusive.recipientAmtHBars = null;
//        cryptoTransferOptions.dependent.mPreview = "no";
//
//        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
//        doNothing().when(shellHelper).printError(valueCapture.capture());
//        cryptoTransfer.run();
//        String actual = valueCapture.getValue();
//        String expected = "You have to provide a transaction amount in hbars or tinybars";
//        assertEquals(expected, actual);
//    }

//    @Test
//    public void bothArgsLoopReturnsError() {
//        cryptoTransferOptions.dependent.senderList = "0.0.116681,0.0.117813";
//        cryptoTransferOptions.dependent.recipientList = "0.0.114152,0.0.11667";
//        cryptoTransferOptions.exclusive.recipientAmtTinyBars = "-100,-100,100,100";
//        cryptoTransferOptions.exclusive.recipientAmtHBars = "20,-20,20,20";
//        cryptoTransferOptions.dependent.mPreview = "no";
//
//        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
//        doNothing().when(shellHelper).printError(valueCapture.capture());
//        cryptoTransfer.run();
//        String actual = valueCapture.getValue();
//        String expected = "You have to provide a transaction amount in hbars or tinybars";
//        assertEquals(expected, actual);
//}

    @Test
    public void verifyEqualListReturnsTrue() {
        List<String> transferList = Arrays.asList(("0.0.116681,0.0.117813,0.0.114152,0.0.11667").split(","));
        List<String> amountList = Arrays.asList(("-100,-100,100,100").split(","));
        assertTrue(cryptoTransfer.verifyEqualList(transferList, amountList));
    }

    @Test
    public void verifyEqualListReturnsFalse() {
        List<String> transferList = Arrays.asList(("0.0.117813,0.0.114152,0.0.11667").split(","));
        List<String> amountList = Arrays.asList(("-100,-100,100,100").split(","));
        assertFalse(cryptoTransfer.verifyEqualList(transferList, amountList));

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        doNothing().when(shellHelper).printError(valueCapture.capture());
        cryptoTransfer.verifyEqualList(transferList, amountList);
        String actual = valueCapture.getValue();
        String expected = "Lists aren't the same size";
        assertEquals(expected, actual);
    }

    @Test
    public void verifyTransferListReturnsFalseAccounts() {
        List<String> transferList = Arrays.asList(("0.117813,114152,0.0.11667").split(","));
        List<String> amountList = Arrays.asList(("-100,100,100").split(","));
        when(accountManager.isAccountId("0.117813")).thenReturn(false);
        assertTrue(cryptoTransfer.verifyEqualList(transferList, amountList));

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        doNothing().when(shellHelper).printError(valueCapture.capture());
        cryptoTransfer.verifyTransferList(transferList);
        String actual = valueCapture.getValue();
        String expected = "Please check that accountId is in the right format";
        assertEquals(expected, actual);
    }

    @Test
    public void verifyTransferListReturnsTrue() {
        List<String> transferList = Arrays.asList(("0.0.116681,0.0.117813,0.0.114152,0.0.11667").split(","));
        when(accountManager.isAccountId("0.0.116681")).thenReturn(true);
        when(accountManager.isAccountId("0.0.117813")).thenReturn(true);
        when(accountManager.isAccountId("0.0.114152")).thenReturn(true);
        when(accountManager.isAccountId("0.0.11667")).thenReturn(true);
        assertTrue(cryptoTransfer.verifyTransferList(transferList));
    }

    @Test
    public void verifyTransferListReturnsFalse() {
        List<String> transferList = Arrays.asList(("0.0.116681,0.0.114152,0.0.11667").split(","));
        List<String> amountList = Arrays.asList(("-100,-100,100,100").split(","));
        assertFalse(cryptoTransfer.verifyEqualList(transferList, amountList));

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        doNothing().when(shellHelper).printError(valueCapture.capture());
        cryptoTransfer.verifyEqualList(transferList, amountList);
        String actual = valueCapture.getValue();
        String expected = "Lists aren't the same size";
        assertEquals(expected, actual);
    }

    @Test
    public void verifyTransferInHbarsNotDeci() {
        List<String> amountList = Arrays.asList(("-10,-10,10,10").split(","));
        assertTrue(cryptoTransfer.sumOfHbarsInLong(amountList));
    }

    @Test
    public void verifyTransferInHbarsResolveDecimals() {
        List<String> amountList = Arrays.asList(("-2.1,-0.0005,1.0005,1.1").split(","));
        assertTrue(cryptoTransfer.sumOfHbarsInLong(amountList));
    }

    @Test
    public void verifyTransferInTinybarsNotDeci() {
        List<String> amountList = Arrays.asList(("-100,-100,100,100").split(","));
        assertTrue(cryptoTransfer.sumOfTinybarsInLong(amountList));
    }

    @Test
    public void verifyTransferInTinybarsResolveDecimals() {
        List<String> amountList = Arrays.asList(("-0.7,-10000.6,10000,0.7").split(","));
        assertFalse(cryptoTransfer.sumOfTinybarsInLong(amountList));
    }

    @Test
    public void convertHbarToLong() {
        long amt;
        long amt1;
        long amt2;
        long amt3;
        List<String> amountList = Arrays.asList(("-0.7,-10000.6,10000.6,0.7").split(","));

        amt = cryptoTransfer.convertHbarToLong(amountList.get(0));
        assertEquals(-70000000L, amt);
        amt1 = cryptoTransfer.convertHbarToLong(amountList.get(1));
        assertEquals(-1000060000000L, amt1);
        amt2 = cryptoTransfer.convertHbarToLong(amountList.get(2));
        assertEquals(1000060000000L, amt2);
        amt3 = cryptoTransfer.convertHbarToLong(amountList.get(3));
        assertEquals(70000000L, amt3);
    }

    @Test
    public void convertTinybarToLong() {
        long amt;
        long amt1;
        long amt2;
        List<String> amountList = Arrays.asList(("-10,-222200,10000000000000000").split(","));
        amt = cryptoTransfer.convertTinybarToLong(amountList.get(0));
        assertEquals(-10L, amt);
        amt1 = cryptoTransfer.convertTinybarToLong(amountList.get(1));
        assertEquals(-222200L, amt1);
        amt2 = cryptoTransfer.convertTinybarToLong(amountList.get(2));
        assertEquals(10000000000000000L, amt2);
    }

    @Test
    public void transferListToPromptPreviewMap() {
        List<String> transferList = Arrays.asList(("0.0.116681,0.0.117813").split(","));
        List<String> amountList = Arrays.asList(("-100,100").split(","));

        Map<Integer, PreviewTransferList> expectedMap = new HashMap<>();
        PreviewTransferList previewTransferList = new PreviewTransferList(AccountId.fromString("0.0.116681"), "-100");
        PreviewTransferList previewTransferList1 = new PreviewTransferList(AccountId.fromString("0.0.117813"), "100");
        expectedMap.put(0, previewTransferList);
        expectedMap.put(1, previewTransferList1);

        Map<Integer, PreviewTransferList> actualMap = cryptoTransfer.transferListToPromptPreviewMap(transferList, amountList);
        assertEquals(expectedMap.get(0).getAccountId(), actualMap.get(0).getAccountId());
    }
}
