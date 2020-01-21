package com.hedera.cli.hedera.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hedera.cli.hedera.crypto.CryptoTransferOptions;
import com.hedera.cli.shell.ShellHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ValidateTransferListTest {

    @InjectMocks
    private ValidateTransferList validateTransferList;

    @Mock
    private ShellHelper shellHelper;

    @Mock
    private ValidateAmount validateAmount;

    @Mock
    private ValidateAccounts validateAccounts;

    private CryptoTransferOptions cryptoTransferOptions;
    private CryptoTransferOptions.Exclusive exclusive;
    private CryptoTransferOptions.Dependent dependent;
    private String senderListArgs;
    private List<String> senderList;
    private String recipient1Amt;
    private String recipient2Amt;
    private String recipientListArgs;
    private List<String> recipientList;
    private List<String> expectedAmountList;

    @BeforeEach
    public void setUp() {
        String sender = "0.0.1001";
        senderList = new ArrayList<>();
        senderList.add(sender);

        String recipient1 = "0.0.1002";
        String recipient2 = "0.0.1003";
        recipientList = new ArrayList<>();
        recipientList.add(recipient1);
        recipientList.add(recipient2);

        String senderAmt = "-1400";
        recipient1Amt = "1000";
        recipient2Amt = "400";

        senderListArgs = sender;
        recipientListArgs = recipient1 + "," + recipient2;

        expectedAmountList = new ArrayList<>();
        expectedAmountList.add(senderAmt);
        expectedAmountList.add(recipient1Amt);
        expectedAmountList.add(recipient2Amt);
    }

    @Test
    public void assertAutowiredDependenciesNotNull() {
        validateTransferList.setShellHelper(shellHelper);
        assertNotNull(validateTransferList.getShellHelper());
        validateTransferList.setValidateAccounts(validateAccounts);
        assertNotNull(validateTransferList.getValidateAccounts());
        validateTransferList.setValidateAmount(validateAmount);
        assertNotNull(validateTransferList.getValidateAmount());
        assertNotNull(validateTransferList);
        validateTransferList.setRecipientList(recipientList);
        assertEquals(recipientList, validateTransferList.getRecipientList());
        validateTransferList.setSenderList(senderList);
        assertEquals(senderList, validateTransferList.getSenderList());
        validateTransferList.setAmountList(expectedAmountList);
        assertEquals(expectedAmountList, validateTransferList.getAmountList());
    }

    @Test
    public void sumOfAmountInTiny() {

        dependent = new CryptoTransferOptions.Dependent();
        exclusive = new CryptoTransferOptions.Exclusive();
        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        validateTransferList.setCryptoTransferOptions(cryptoTransferOptions);
        assertEquals(cryptoTransferOptions, validateTransferList.getCryptoTransferOptions());
        validateTransferList.setTiny(true);
        List<String> amountList = new ArrayList<>();
        amountList.add("50");
        amountList.add("50");
        validateTransferList.setAmountList(amountList);
        when(validateAmount.sumOfTinybarsInLong(amountList)).thenReturn(100L);
        assertEquals(100L, validateTransferList.sumOfAmountList(amountList));
    }

    @Test
    public void sumOfAmountInHbar() {

        dependent = new CryptoTransferOptions.Dependent();
        exclusive = new CryptoTransferOptions.Exclusive();
        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        validateTransferList.setCryptoTransferOptions(cryptoTransferOptions);

        validateTransferList.setTiny(false);
        List<String> amountList = new ArrayList<>();
        amountList.add("0.50");
        amountList.add("0.40");
        validateTransferList.setAmountList(amountList);
        when(validateAmount.sumOfHbarsInLong(amountList)).thenReturn(90000000L);
        assertEquals(90000000L, validateTransferList.sumOfAmountList(amountList));
    }

    @Test
    public void updateAmountListTinybar() {
        dependent = new CryptoTransferOptions.Dependent();

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtTinyBars("500000,400000");
        exclusive.setTransferListAmtHBars("");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        validateTransferList.setCryptoTransferOptions(cryptoTransferOptions);

        List<String> amountList = new ArrayList<>();
        amountList.add("500000");
        amountList.add("400000");
        validateTransferList.setAmountList(amountList);
        validateTransferList.setTiny(true);
        assertTrue(validateTransferList.isTiny());
        long sumOfRecipientAmount = 900000L;
        validateTransferList.updateAmountList(amountList, sumOfRecipientAmount);
        amountList.add(0, "-900000");
        assertEquals(amountList, validateTransferList.getFinalAmountList(cryptoTransferOptions));
    }

    @Test
    public void convertAmountListToTinybar() {
        List<String> amountList = new ArrayList<>();
        amountList.add("0.006");
        amountList.add("0.003");
        validateTransferList.setAmountList(amountList);
        validateTransferList.setTiny(false);
        long sumOfReceipientsAmount = 900000L;
        validateTransferList.updateAmountList(amountList, sumOfReceipientsAmount);
        verify(validateAmount, times(2)).convertHbarToLong(any());
    }

    @Test
    public void verifyAmountListCase3Senders() {
        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(false);
        dependent.setSenderList("0.0.1001,0.0.1002,0.0.1005");
        dependent.setRecipientList("0.0.1003,0.0.1004");

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtTinyBars("500000,400000");
        exclusive.setTransferListAmtHBars("");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        validateTransferList.setCryptoTransferOptions(cryptoTransferOptions);
        List<String> amountList = Arrays.asList(exclusive.getTransferListAmtTinyBars().split(","));
        validateTransferList.setAmountList(amountList);
        validateTransferList.setTiny(false);

        validateTransferList.setSenderList(Arrays.asList(dependent.getSenderList().split(",")));
        validateTransferList.verifyAmountList(cryptoTransferOptions);
        verify(validateAmount, times(1)).getAmountList(cryptoTransferOptions);
        verify(validateAccounts, times(1)).getSenderList(cryptoTransferOptions);
        verify(validateAccounts, times(1)).getRecipientList(cryptoTransferOptions);
        verify(validateAmount, times(1)).isTiny(cryptoTransferOptions);

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).printWarning(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "More than 2 senders not supported";
        assertEquals(expected, actual);
    }

    @Test
    public void verifyAmountListCase1SenderIsOperator() {
        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(false);
        dependent.setSenderList(senderListArgs);
        dependent.setRecipientList(recipientListArgs);

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtTinyBars(recipient1Amt + "," + recipient2Amt);
        exclusive.setTransferListAmtHBars("");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        when(validateAmount.isTiny(cryptoTransferOptions)).thenReturn(true);
        when(validateAmount.getAmountList(cryptoTransferOptions)).thenReturn(expectedAmountList);
        when(validateAccounts.getSenderList(cryptoTransferOptions)).thenReturn(senderList);
        when(validateAccounts.getRecipientList(cryptoTransferOptions)).thenReturn(recipientList);
        when(validateAmount.verifyZeroSum(0)).thenReturn(true);
        boolean validated = validateTransferList.verifyAmountList(cryptoTransferOptions);
        assertTrue(validated);
    }

    @Test
    public void verifyAmountListCase1SenderIsOperatorHbar() {
        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(false);
        dependent.setSenderList(senderListArgs);
        dependent.setRecipientList(recipientListArgs);

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtTinyBars("");
        exclusive.setTransferListAmtHBars(recipient1Amt + "," + recipient2Amt);

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        when(validateAmount.isTiny(cryptoTransferOptions)).thenReturn(false);
        when(validateAmount.getAmountList(cryptoTransferOptions)).thenReturn(expectedAmountList);
        when(validateAccounts.getSenderList(cryptoTransferOptions)).thenReturn(senderList);
        when(validateAccounts.getRecipientList(cryptoTransferOptions)).thenReturn(recipientList);
        when(validateAmount.verifyZeroSum(0)).thenReturn(true);
        boolean validated = validateTransferList.verifyAmountList(cryptoTransferOptions);
        assertTrue(validated);
    }

    @Test
    public void verifyAmountListCase1SenderIsOperatorAmountTransferSizeNotEqual() {
        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(false);
        dependent.setSenderList(senderListArgs);
        dependent.setRecipientList(recipientListArgs);

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtTinyBars(recipient1Amt + "," + recipient2Amt);
        exclusive.setTransferListAmtHBars("");

        List<String> amountList = new ArrayList<>();
        amountList.add(recipient1Amt);
        amountList.add(recipient2Amt);
        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        when(validateAmount.isTiny(cryptoTransferOptions)).thenReturn(true);
        when(validateAccounts.senderListHasOperator(cryptoTransferOptions)).thenReturn(true);
        when(validateAmount.getAmountList(cryptoTransferOptions)).thenReturn(amountList);
        when(validateAccounts.getSenderList(cryptoTransferOptions)).thenReturn(senderList);
        when(validateAccounts.getRecipientList(cryptoTransferOptions)).thenReturn(recipientList);
        when(validateAmount.verifyZeroSum(0)).thenReturn(true);
        boolean validated = validateTransferList.verifyAmountList(cryptoTransferOptions);
        assertTrue(validated);
    }

    @Test
    public void verifyAmountListCase1SenderDoesNotHaveOperator() {
        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(false);
        dependent.setSenderList(senderListArgs);
        dependent.setRecipientList(recipientListArgs);

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtTinyBars(recipient1Amt + "," + recipient2Amt);
        exclusive.setTransferListAmtHBars("");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        when(validateAmount.isTiny(cryptoTransferOptions)).thenReturn(true);
        when(validateAmount.getAmountList(cryptoTransferOptions)).thenReturn(expectedAmountList);
        when(validateAccounts.getSenderList(cryptoTransferOptions)).thenReturn(senderList);
        when(validateAccounts.getRecipientList(cryptoTransferOptions)).thenReturn(recipientList);
        when(validateAmount.verifyZeroSum(0)).thenReturn(true);
        boolean validated = validateTransferList.verifyAmountList(cryptoTransferOptions);
        assertTrue(validated);
    }

    @Test
    public void verifyAmountListCase1SenderDoesNotHaveOperatorHbar() {
        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(false);
        dependent.setSenderList(senderListArgs);
        dependent.setRecipientList(recipientListArgs);

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtHBars(recipient1Amt + "," + recipient2Amt);
        exclusive.setTransferListAmtTinyBars("");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        when(validateAmount.isTiny(cryptoTransferOptions)).thenReturn(false);
        // when(validateAccounts.senderListHasOperator(cryptoTransferOptions)).thenReturn(false);
        when(validateAmount.getAmountList(cryptoTransferOptions)).thenReturn(expectedAmountList);
        when(validateAccounts.getSenderList(cryptoTransferOptions)).thenReturn(senderList);
        when(validateAccounts.getRecipientList(cryptoTransferOptions)).thenReturn(recipientList);
        when(validateAmount.verifyZeroSum(0)).thenReturn(true);
        boolean validated = validateTransferList.verifyAmountList(cryptoTransferOptions);
        assertTrue(validated);
    }

    @Test
    public void verifyAmountListCase1InvalidList() {
        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(false);
        dependent.setSenderList(senderListArgs);
        dependent.setRecipientList(recipientListArgs);

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtTinyBars(recipient1Amt + "," + recipient2Amt);
        exclusive.setTransferListAmtHBars("");

        List<String> wrongAmtList = new ArrayList<>();
        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        when(validateAmount.isTiny(cryptoTransferOptions)).thenReturn(true);
        when(validateAccounts.senderListHasOperator(cryptoTransferOptions)).thenReturn(false);
        when(validateAmount.getAmountList(cryptoTransferOptions)).thenReturn(wrongAmtList);
        when(validateAccounts.getSenderList(cryptoTransferOptions)).thenReturn(senderList);
        when(validateAccounts.getRecipientList(cryptoTransferOptions)).thenReturn(recipientList);
        validateTransferList.verifyAmountList(cryptoTransferOptions);
        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).printError(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "Invalid transfer list. Your transfer list must sum up to 0";
        assertEquals(expected, actual);
    }
}
