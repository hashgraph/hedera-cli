package com.hedera.cli.hedera.crypto;

import org.junit.Test;
import picocli.CommandLine;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.*;
import static org.junit.Assert.*;


public class CryptoTransferMultipleTest {

    @Test
    public void testIsNumeric() {

        String str = "111111111";
        CryptoTransferMultiple cryptoTransferMultiple = new CryptoTransferMultiple();
        assertTrue(cryptoTransferMultiple.isNumeric(str));

        String str1 = " ";
        CryptoTransferMultiple cryptoTransferMultiple1 = new CryptoTransferMultiple();
        assertFalse(cryptoTransferMultiple1.isNumeric(str1));

        String str2 = null;
        CryptoTransferMultiple cryptoTransferMultiple2 = new CryptoTransferMultiple();
        assertFalse(cryptoTransferMultiple2.isNumeric(str2));

        String str3 = "0.1";
        CryptoTransferMultiple cryptoTransferMultiple3 = new CryptoTransferMultiple();
        assertFalse(cryptoTransferMultiple3.isNumeric(str3));

        String str4 = "-9";
        CryptoTransferMultiple cryptoTransferMultiple4 = new CryptoTransferMultiple();
        assertFalse(cryptoTransferMultiple4.isNumeric(str4));
    }

    @Test
    public void testIsAccountId() {
        String str = "1001";
        CryptoTransferMultiple cryptoTransferMultiple = new CryptoTransferMultiple();
        assertTrue(cryptoTransferMultiple.isAccountId(str));

        String str1 = " ";
        CryptoTransferMultiple cryptoTransferMultiple1 = new CryptoTransferMultiple();
        assertFalse(cryptoTransferMultiple1.isAccountId(str1));

        String str2 = "10a01";
        CryptoTransferMultiple cryptoTransferMultiple2 = new CryptoTransferMultiple();
        assertFalse(cryptoTransferMultiple2.isAccountId(str2));

        String str3 = "000";
        CryptoTransferMultiple cryptoTransferMultiple3 = new CryptoTransferMultiple();
        assertFalse(cryptoTransferMultiple3.isAccountId(str3));

        String str4 = "-";
        CryptoTransferMultiple cryptoTransferMultiple4 = new CryptoTransferMultiple();
        assertFalse(cryptoTransferMultiple4.isAccountId(str4));
    }

    @Test
    public void testRecipientList() {
        List<String> accountList = Arrays.asList("1001", "1002", "1003");
        List<String> amountList= Arrays.asList("100", "9888486986", "10000001100000");
        CryptoTransferMultiple cryptoTransferMultiple = new CryptoTransferMultiple();
        cryptoTransferMultiple.verifiedRecipientMap(accountList, amountList);
    }

//    @Test(expected = IllegalArgumentException.class)
//    public void testRecipientList1() {
//        List<String> accountList = Arrays.asList("1001", "1002", "1003");
//        List<String> amountList= Arrays.asList("100", "9888486986", "1000000 1100000");
//        CryptoTransferMultiple cryptoTransferMultiple = new CryptoTransferMultiple();
//        cryptoTransferMultiple.verifiedRecipientMap(accountList, amountList);
//    }

    @Test
    public void testCryptoTransferMultipleArgs() {

        @Command
        class CryptoTransferMultiple {

            @Option(names = {"-r", "--recipient"}, split = " ", arity = "0..*")
            private String[] recipient;

            @Option(names = {"-a", "--recipientAmt"}, split = " ", arity = "0..*")
            private String[] recipientAmt;

        }

        CryptoTransferMultiple ct = CommandLine.populateCommand(new CryptoTransferMultiple(), "-r=1001,1002,1003","-a=100,200,300");
        assertEquals(Collections.singletonList("1001,1002,1003"), Arrays.asList(ct.recipient));
        assertEquals(Collections.singletonList("100,200,300"), Arrays.asList(ct.recipientAmt));

        CommandLine cmd = new CommandLine(new CryptoTransfer());
        ParseResult result = cmd.parseArgs("-r=1111,2222,3333", "-a=1000,200,3000");
        assertTrue(result.hasMatchedOption("r"));
        assertTrue(result.hasMatchedOption("a"));
        assertEquals(Arrays.asList("-r=1111,2222,3333","-a=1000,200,3000"), result.originalArgs());
    }
}
