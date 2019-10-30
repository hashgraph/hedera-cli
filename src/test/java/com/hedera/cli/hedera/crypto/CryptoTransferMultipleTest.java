package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import com.hedera.cli.hedera.utils.AccountManager;
import com.hedera.cli.shell.ShellHelper;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CryptoTransferMultipleTest {

    @InjectMocks
    private Crypto crypto;

    @Mock
    private ShellHelper shellHelper;

    @Mock
    AccountManager accountManager;

    @Mock
    private CryptoTransferOptions cryptoTransferOptions;

    @Mock
    private List<CryptoTransferOptions> cryptoTransferOptionsList;


    @BeforeEach
    public void init() {
        shellHelper = crypto.getShellHelper();
        assertNotNull(shellHelper);
    }

//    @Test
//    public void testIsNumeric() {
//
//        String str = "111111111";
//        assertTrue(cryptoTransferMultiple.isNumeric(str));
//
//        String str1 = " ";
//        assertFalse(cryptoTransferMultiple.isNumeric(str1));
//
//        String str2 = null;
//        assertFalse(cryptoTransferMultiple.isNumeric(str2));
//
//        String str3 = "0.1";
//        assertFalse(cryptoTransferMultiple.isNumeric(str3));
//
//        String str4 = "-9";
//        assertFalse(cryptoTransferMultiple.isNumeric(str4));
//    }

//    @Test
//    public void tinyBarsLoop() {
//        cryptoTransferOptions.dependent.senderList = "0.0.116681,0.0.117813";
//        cryptoTransferOptions.dependent.recipientList = "0.0.114152,0.0.11667";
//        cryptoTransferOptions.exclusive.recipientAmtTinyBars = "-100,-100,100,100";
//        cryptoTransferOptions.exclusive.recipientAmtHBars = null;
//        cryptoTransferOptions.dependent.mPreview = "no";
//
//        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
//        doNothing().when(shellHelper).printInfo(valueCapture.capture());
//        crypto.run();
//        String actual = valueCapture.getValue();
//        String expected = "here in tiny loop";
//        assertEquals(expected, actual);
//    }
//
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
//        crypto.run();
//        String actual = valueCapture.getValue();
//        String expected = "here in hbar loop";
//        assertEquals(expected, actual);
//    }
//
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
//        crypto.run();
//        String actual = valueCapture.getValue();
//        String expected = "You have to provide a transaction amount in hbars or tinybars";
//        assertEquals(expected, actual);
//    }
//
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
//        crypto.run();
//        String actual = valueCapture.getValue();
//        String expected = "You have to provide a transaction amount in hbars or tinybars";
//        assertEquals(expected, actual);
//}

    @Test
    public void verifyEqualListReturnsTrue() {
        List<String> transferList = Arrays.asList(("0.0.116681,0.0.117813,0.0.114152,0.0.11667").split(","));
        List<String> amountList = Arrays.asList(("-100,-100,100,100").split(","));
        assertTrue(crypto.verifyEqualList(transferList, amountList));
    }

    @Test
    public void verifyEqualListReturnsFalse() {
        List<String> transferList = Arrays.asList(("0.0.117813,0.0.114152,0.0.11667").split(","));
        List<String> amountList = Arrays.asList(("-100,-100,100,100").split(","));
        assertFalse(crypto.verifyEqualList(transferList, amountList));

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        doNothing().when(shellHelper).printError(valueCapture.capture());
        crypto.verifyEqualList(transferList, amountList);
        String actual = valueCapture.getValue();
        String expected = "Lists aren't the same size";
        assertEquals(expected, actual);
    }

//    @Test
//    public void verifyTransferListReturnsTrue() {
//        List<String> transferList = Arrays.asList(("0.0.116681,0.0.117813,0.0.114152,0.0.11667").split(","));
//        assertTrue(crypto.verifyTransferList(transferList));
//    }
//
//    @Test
//    public void verifyTransferListReturnsFalse() {
//        List<String> transferList = Arrays.asList(("0.117813,114152,0.0.11667").split(","));
//        List<String> amountList = Arrays.asList(("-100,-100,100,100").split(","));
//        assertFalse(crypto.verifyEqualList(transferList, amountList));
//
//        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
//        doNothing().when(shellHelper).printError(valueCapture.capture());
//        crypto.verifyEqualList(transferList, amountList);
//        String actual = valueCapture.getValue();
//        String expected = "Please check that accountId is in the right format";
//        assertEquals(expected, actual);
//    }

    @Test
    public void verifyTransferInHbarsNotDeci() {
        List<String> amountList = Arrays.asList(("-10,-10,10,10").split(","));
        assertTrue(crypto.verifyHbarsInLong(amountList));

        boolean isTiny = false;
        assertTrue(crypto.verifySumOfTransfer(amountList, isTiny));
    }

    @Test
    public void verifyTransferInHbarsResolveDecimals() {
        List<String> amountList = Arrays.asList(("-2.1,-0.0005,1.0005,1.1").split(","));
        assertTrue(crypto.verifyHbarsInLong(amountList));

        boolean isTiny = false;
        assertTrue(crypto.verifySumOfTransfer(amountList, isTiny));
    }

    @Test
    public void verifyTransferInTinybarsNotDeci() {
        List<String> amountList = Arrays.asList(("-100,-100,100,100").split(","));
        assertTrue(crypto.verifyTinybarsInLong(amountList));

        boolean isTiny = true;
        assertTrue(crypto.verifySumOfTransfer(amountList, isTiny));
    }

    @Test
    public void verifyTransferInTinybarsResolveDecimals() {
        List<String> amountList = Arrays.asList(("-0.7,-10000.6,10000,0.7").split(","));
        assertFalse(crypto.verifyTinybarsInLong(amountList));

        boolean isTiny = true;
        assertFalse(crypto.verifySumOfTransfer(amountList, isTiny));
    }

//    @Test
//    public void recipientListInTiny() {
//        List<String> accountList = Arrays.asList("0.0.1001", "0.0.1002", "0.0.1003");
//        List<String> amountList = Arrays.asList("100", "9888486986", "10000001100000");
//        cryptoTransferMultiple.verifiedRecipientMap(accountList, amountList, true);
//        assertNotNull(cryptoTransferMultiple);
//
//        List<String> amountListSize = Arrays.asList("100", "10000001100000");
//        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
//        doNothing().when(shellHelper).printError(valueCapture.capture());
//        cryptoTransferMultiple.verifiedRecipientMap(accountList, amountListSize, true);
//        String actual = valueCapture.getValue();
//        String expected = "Lists aren't the same size";
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void nullIfAmountListContainsDecimals() {
//        List<String> accountList = Arrays.asList("0.0.1001", "0.0.1002", "0.0.1003");
//        List<String> amountList1 = Arrays.asList("0.1", "0.9888486986", "1000000.1100000");
//        ArgumentCaptor<String> valueCapture1 = ArgumentCaptor.forClass(String.class);
//        doNothing().when(shellHelper).printError(valueCapture1.capture());
//        cryptoTransferMultiple.verifiedRecipientMap(accountList, amountList1, true);
//        String actual1 = valueCapture1.getValue();
//        String expected1 = null;
//        assertEquals(expected1, actual1);
//    }
//
//    @Test
//    public void nullIfAmountListContainsZero() {
//        List<String> accountList = Arrays.asList("0.0.1001", "0.0.1002", "0.0.1003");
//        List<String> amountList2 = Arrays.asList("0", "0.9888486986", "1000000.1100000");
//        ArgumentCaptor<String> valueCapture2 = ArgumentCaptor.forClass(String.class);
//        doNothing().when(shellHelper).printError(valueCapture2.capture());
//        cryptoTransferMultiple.verifiedRecipientMap(accountList, amountList2, false);
//        String actual2 = valueCapture2.getValue();
//        String expected2 = null;
//        assertEquals(expected2, actual2);
//    }
    // @Test(expected = IllegalArgumentException.class)
    // public void testRecipientList1() {
    // List<String> accountList = Arrays.asList("1001", "1002", "1003");
    // List<String> amountList= Arrays.asList("100", "9888486986", "1000000
    // 1100000");
    // CryptoTransferMultiple cryptoTransferMultiple = new CryptoTransferMultiple();
    // cryptoTransferMultiple.verifiedRecipientMap(accountList, amountList);
    // }

    @Test
    public void testCryptoTransferMultipleArgs() {

//        @Command
//        class CryptoTransferMultipleAgain {
//
//            @Option(names = { "-a", "--accountId" }, split = " ", arity = "0..*")
//            private String[] recipient;
//
//            @Option(names = { "-r", "--recipientAmt" }, split = " ", arity = "0..*")
//            private String[] recipientAmt;
//
//        }
//
//        CryptoTransferMultipleAgain ct = CommandLine.populateCommand(new CryptoTransferMultipleAgain(), "-a=0.0.1001,0.0.1002,0.0.1003",
//                "-r=100,200,300");
//        assertEquals(Collections.singletonList("0.0.1001,0.0.1002,0.0.1003"), Arrays.asList(ct.recipient));
//        assertEquals(Collections.singletonList("100,200,300"), Arrays.asList(ct.recipientAmt));
//
//        CommandLine cmd = new CommandLine(new CryptoTransfer());
//        ParseResult result = cmd.parseArgs("-a=0.0.1111,0.0.2222,0.0.3333", "-r=1000,200,3000");
//        assertTrue(result.hasMatchedOption("a"));
//        assertTrue(result.hasMatchedOption("r"));
//        assertEquals(Arrays.asList("-a=0.0.1111,0.0.2222,0.0.3333", "-r=1000,200,3000"), result.originalArgs());
    }
}
