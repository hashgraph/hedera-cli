package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.doNothing;

import java.util.Arrays;
import java.util.List;

import com.hedera.cli.hedera.utils.AccountManager;
import com.hedera.cli.shell.ShellHelper;
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
    private CryptoTransferMultiple cryptoTransferMultiple;

    @Mock
    private AccountManager accountManager;

    @Mock
    private ShellHelper shellHelper;

    @BeforeEach
    public void init() {
        accountManager = cryptoTransferMultiple.getAccountManager();
        assertNotNull(accountManager);

        shellHelper = cryptoTransferMultiple.getShellHelper();
        assertNotNull(shellHelper);
    }

    @Test
    public void testIsNumeric() {

        String str = "111111111";
        assertTrue(cryptoTransferMultiple.isNumeric(str));

        String str1 = " ";
        assertFalse(cryptoTransferMultiple.isNumeric(str1));

        String str2 = null;
        assertFalse(cryptoTransferMultiple.isNumeric(str2));

        String str3 = "0.1";
        assertFalse(cryptoTransferMultiple.isNumeric(str3));

        String str4 = "-9";
        assertFalse(cryptoTransferMultiple.isNumeric(str4));
    }

    @Test
    public void recipientListInTiny() {
        List<String> accountList = Arrays.asList("0.0.1001", "0.0.1002", "0.0.1003");
        List<String> amountList = Arrays.asList("100", "9888486986", "10000001100000");
        cryptoTransferMultiple.verifiedRecipientMap(accountList, amountList, true);
        assertNotNull(cryptoTransferMultiple);

        List<String> amountListSize = Arrays.asList("100", "10000001100000");
        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        doNothing().when(shellHelper).printError(valueCapture.capture());
        cryptoTransferMultiple.verifiedRecipientMap(accountList, amountListSize, true);
        String actual = valueCapture.getValue();
        String expected = "Lists aren't the same size";
        assertEquals(expected, actual);
    }

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
