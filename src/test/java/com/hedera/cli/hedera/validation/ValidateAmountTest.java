package com.hedera.cli.hedera.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hedera.cli.hedera.crypto.CryptoTransferOptions;
import com.hedera.cli.shell.ShellHelper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
public class ValidateAmountTest {

    @InjectMocks
    private ValidateAmount validateAmount;

    @Mock
    private ShellHelper shellHelper;

    private CryptoTransferOptions cryptoTransferOptions;
    private CryptoTransferOptions.Exclusive exclusive;
    private CryptoTransferOptions.Dependent dependent;

    @Test
    public void assertAutowiredDependenciesNotNull() {
        validateAmount.setShellHelper(shellHelper);
        assertNotNull(validateAmount.getShellHelper());
        assertNotNull(validateAmount);
    }

    @Test
    public void checkTrue() {
        dependent = new CryptoTransferOptions.Dependent();
        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtTinyBars("400,1000,10030");
        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        assertNotNull(cryptoTransferOptions);
        validateAmount.setCryptoTransferOptions(cryptoTransferOptions);
        validateAmount.setHbarListArgs(exclusive.getTransferListAmtHBars());
        validateAmount.setTinybarListArgs(exclusive.getTransferListAmtTinyBars());
        assertTrue(validateAmount.check(cryptoTransferOptions));
    }

    @Test
    public void transactionAmountNotValidNoHbarNorTinybar() {
        dependent = new CryptoTransferOptions.Dependent();

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtHBars("");
        exclusive.setTransferListAmtTinyBars("");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        validateAmount.setCryptoTransferOptions(cryptoTransferOptions);
        validateAmount.setHbarListArgs(exclusive.getTransferListAmtHBars());
        validateAmount.setTinybarListArgs(exclusive.getTransferListAmtTinyBars());

        boolean validatedAmount = validateAmount.transactionAmountNotValid();
        assertFalse(validateAmount.check(cryptoTransferOptions));

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper, times(2)).printError(valueCapture.capture());

        System.out.println(valueCapture.getAllValues());
        String actual = valueCapture.getAllValues().get(0);
        String expected = "You have to provide transfer amounts either in hbars or tinybars";
        assertEquals(expected, actual);
        assertTrue(validatedAmount);

        String actual2 = valueCapture.getAllValues().get(1);
        assertEquals(expected, actual2);
    }

    @Test
    public void transactionAmountNotValidBothHbarAndTinybar() {
        dependent = new CryptoTransferOptions.Dependent();

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtTinyBars("400,1000,10030");
        exclusive.setTransferListAmtHBars("0.1,0.004,1.9");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        validateAmount.setCryptoTransferOptions(cryptoTransferOptions);
        validateAmount.setHbarListArgs(exclusive.getTransferListAmtHBars());
        validateAmount.setTinybarListArgs(exclusive.getTransferListAmtTinyBars());

        boolean validatedAmount = validateAmount.transactionAmountNotValid();

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).printError(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "Transfer amounts must either be in hbars or tinybars, not both";
        assertEquals(expected, actual);
        assertTrue(validatedAmount);

        assertEquals("0.1,0.004,1.9", validateAmount.getHbarListArgs());
        assertEquals("400,1000,10030", validateAmount.getTinybarListArgs());
    }

    @Test
    public void transactionAmountValidTinybar() {
        dependent = new CryptoTransferOptions.Dependent();

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtTinyBars("400,1000,10030");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        validateAmount.setCryptoTransferOptions(cryptoTransferOptions);
        validateAmount.setHbarListArgs(exclusive.getTransferListAmtHBars());
        validateAmount.setTinybarListArgs(exclusive.getTransferListAmtTinyBars());

        boolean validatedAmount = validateAmount.transactionAmountNotValid();
        assertFalse(validatedAmount);
        assertTrue(validateAmount.isTiny(cryptoTransferOptions));
        assertTrue(validateAmount.check(cryptoTransferOptions));
    }

    @Test
    public void transactionAmountValidHbar() {
        dependent = new CryptoTransferOptions.Dependent();

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtHBars("0.400,1.00,0.111003");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        validateAmount.setCryptoTransferOptions(cryptoTransferOptions);
        validateAmount.setHbarListArgs(exclusive.getTransferListAmtHBars());
        validateAmount.setTinybarListArgs(exclusive.getTransferListAmtTinyBars());

        boolean validatedAmount = validateAmount.transactionAmountNotValid();
        assertFalse(validatedAmount);
        assertFalse(validateAmount.isTiny(cryptoTransferOptions));
    }

    @Test
    public void amountListInTiny() {
        dependent = new CryptoTransferOptions.Dependent();

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtTinyBars("400,1000,10030");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        validateAmount.setCryptoTransferOptions(cryptoTransferOptions);
        validateAmount.setTinybarListArgs(exclusive.getTransferListAmtTinyBars());

        List<String> expectedAmountList = new ArrayList<>();
        expectedAmountList.add("400");
        expectedAmountList.add("1000");
        expectedAmountList.add("10030");

        assertEquals(expectedAmountList, validateAmount.getAmountList(cryptoTransferOptions));
    }

    @Test
    public void amountListInHbar() {
        dependent = new CryptoTransferOptions.Dependent();

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtHBars("200,500,8030");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        validateAmount.setCryptoTransferOptions(cryptoTransferOptions);
        validateAmount.setHbarListArgs(exclusive.getTransferListAmtHBars());

        List<String> expectedAmountList = new ArrayList<>();
        expectedAmountList.add("200");
        expectedAmountList.add("500");
        expectedAmountList.add("8030");

        assertEquals(expectedAmountList, validateAmount.getAmountList(cryptoTransferOptions));
    }

    @Test
    public void amountListEmptyOrNull() {
        dependent = new CryptoTransferOptions.Dependent();

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtHBars("");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        validateAmount.setCryptoTransferOptions(cryptoTransferOptions);
        validateAmount.setHbarListArgs(exclusive.getTransferListAmtHBars());

        List<String> actualList = validateAmount.getAmountList(cryptoTransferOptions);
        validateAmount.setAmountList(actualList);
        assertNull(actualList);
        assertFalse(validateAmount.check(cryptoTransferOptions));
    }

    @Test
    public void convertLongToHbar() {
        String amt = "2000000000000";
        assertEquals("20000.00000000", validateAmount.convertLongToHbar(amt));

        String amt2 = "4";
        assertEquals("0.00000004", validateAmount.convertLongToHbar(amt2));
    }

    @Test
    public void zeroSumTrue() {
        List<String> amountList = Arrays.asList(("-7,-10000,10000,7").split(","));
        long sum = validateAmount.sumOfTinybarsInLong(amountList);
        assertEquals(0, sum);
        boolean zeroSum = validateAmount.verifyZeroSum(sum);
        assertTrue(zeroSum);
    }

    @Test
    public void zeroSumFalseWithTinybarsZero() {
        List<String> amountList = Arrays.asList(("0,10,10000,7").split(","));
        long sum = validateAmount.sumOfTinybarsInLong(amountList);
        assertEquals(-1, sum);
        validateAmount.verifyZeroSum(sum);

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper, times(2)).printError(valueCapture.capture());

        String actual = valueCapture.getAllValues().get(0);
        String expected = "Tinybars must be more or less than 0";
        assertEquals(expected, actual);

        String actual2 = valueCapture.getAllValues().get(1);
        String expected2 = "Invalid transfer list. Your transfer list must sum up to 0";
        assertEquals(expected2, actual2);
    }

    @Test
    public void zeroSumFalseWithHbarsZero() {
        List<String> amountList = Arrays.asList(("0,10,10000,7").split(","));
        long sum = validateAmount.sumOfHbarsInLong(amountList);
        assertEquals(-1, sum);
        validateAmount.verifyZeroSum(sum);

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper, times(2)).printError(valueCapture.capture());

        String actual = valueCapture.getAllValues().get(0);
        String expected = "Hbars must be more or less than 0";
        assertEquals(expected, actual);

        String actual2 = valueCapture.getAllValues().get(1);
        String expected2 = "Invalid transfer list. Your transfer list must sum up to 0";
        assertEquals(expected2, actual2);
    }

    @Test
    public void zeroSumFalseWithTinybarsInDeciError() {
        List<String> amountList = Arrays.asList(("-0.7,-10000.6,10000,0.7").split(","));
        long sum = validateAmount.sumOfTinybarsInLong(amountList);
        assertEquals(-1, sum);
        validateAmount.verifyZeroSum(sum);

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper, times(2)).printError(valueCapture.capture());

        String actual = valueCapture.getAllValues().get(0);
        String expected = "Tinybars must not be a decimal";
        assertEquals(expected, actual);

        String actual2 = valueCapture.getAllValues().get(1);
        String expected2 = "Invalid transfer list. Your transfer list must sum up to 0";
        assertEquals(expected2, actual2);
    }

    @Test
    public void verifySumOfHbarsNotDeci() {
//        List<String> senderList = Arrays.asList("0.0.117813,0.0.117814".split(","));
//        List<String> recipientList = Arrays.asList("0.0.114152,0.0.11667".split(","));
        List<String> amountList = Arrays.asList(("-10,-10,10,10").split(","));
        assertEquals(0, validateAmount.sumOfHbarsInLong(amountList));
    }

    @Test
    public void verifySumOfHbarsInDeci() {
        List<String> amountList = Arrays.asList(("-2.1,-0.0005,1.0005,1.1").split(","));
        assertEquals(0, validateAmount.sumOfHbarsInLong(amountList));
    }

    @Test
    public void verifySumOfTinybarsNotDeci() {
        List<String> amountList = Arrays.asList(("-100,-100,100,100").split(","));
        assertEquals(0, validateAmount.sumOfTinybarsInLong(amountList));
    }

    @Test
    public void verifySumOfTinybarsInDeciNotZeroSum() {
        List<String> amountList = Arrays.asList(("-0.7,-10000.6,10000,0.7").split(","));
        assertEquals(-1, validateAmount.sumOfTinybarsInLong(amountList));
    }

    @Test
    public void convertHbarToLong() {
        long amt;
        long amt1;
        long amt2;
        long amt3;
        List<String> amountList = Arrays.asList(("-0.7,-10000.6,10000.6,0.7").split(","));

        amt = validateAmount.convertHbarToLong(amountList.get(0));
        assertEquals(-70000000L, amt);
        amt1 = validateAmount.convertHbarToLong(amountList.get(1));
        assertEquals(-1000060000000L, amt1);
        amt2 = validateAmount.convertHbarToLong(amountList.get(2));
        assertEquals(1000060000000L, amt2);
        amt3 = validateAmount.convertHbarToLong(amountList.get(3));
        assertEquals(70000000L, amt3);
    }

    @Test
    public void convertTinybarToLong() {
        long amt;
        long amt1;
        long amt2;
        List<String> amountList = Arrays.asList(("-10,-222200,10000000000000000").split(","));
        amt = validateAmount.convertTinybarToLong(amountList.get(0));
        assertEquals(-10L, amt);
        amt1 = validateAmount.convertTinybarToLong(amountList.get(1));
        assertEquals(-222200L, amt1);
        amt2 = validateAmount.convertTinybarToLong(amountList.get(2));
        assertEquals(10000000000000000L, amt2);
    }

    @Test
    public void listArgs() {
        String txListNotEmpty = "1000";
        String txListEmpty = "";
        String args = validateAmount.listArgs(txListNotEmpty, txListEmpty);
        assertEquals(txListNotEmpty, args);
    }
}
