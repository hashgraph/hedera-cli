package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
import com.hedera.hashgraph.sdk.TransactionBuilder;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;
import com.hedera.hashgraph.sdk.proto.*;
import io.grpc.MethodDescriptor;
import org.jetbrains.annotations.NotNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import shadow.org.codehaus.plexus.util.StringUtils;

import java.math.BigInteger;
import java.util.*;
import org.junit.Assert.*;

import static org.junit.Assert.*;


public class CryptoTransferMultipleTest {

    @Test
    public void testIsNumeric() {
        var client = Hedera.createHederaClient();

        String str = "111111111";
        CryptoTransferMultiple cryptoTransferMultiple = new CryptoTransferMultiple(client);
        assertTrue(cryptoTransferMultiple.isNumeric(str));

        String str1 = " ";
        CryptoTransferMultiple cryptoTransferMultiple1 = new CryptoTransferMultiple(client);
        assertFalse(cryptoTransferMultiple1.isNumeric(str1));

        String str2 = null;
        CryptoTransferMultiple cryptoTransferMultiple2 = new CryptoTransferMultiple(client);
        assertFalse(cryptoTransferMultiple2.isNumeric(str2));

        String str3 = "0.1";
        CryptoTransferMultiple cryptoTransferMultiple3 = new CryptoTransferMultiple(client);
        assertFalse(cryptoTransferMultiple3.isNumeric(str3));

        String str4 = "-9";
        CryptoTransferMultiple cryptoTransferMultiple4 = new CryptoTransferMultiple(client);
        assertFalse(cryptoTransferMultiple4.isNumeric(str4));
    }

    @Test
    public void testIsAccountId() {
        var client = Hedera.createHederaClient();

        String str = "1001";
        CryptoTransferMultiple cryptoTransferMultiple = new CryptoTransferMultiple(client);
        assertTrue(cryptoTransferMultiple.isAccountId(str));

        String str1 = " ";
        CryptoTransferMultiple cryptoTransferMultiple1 = new CryptoTransferMultiple(client);
        assertFalse(cryptoTransferMultiple1.isAccountId(str1));

        String str2 = "10a01";
        CryptoTransferMultiple cryptoTransferMultiple2 = new CryptoTransferMultiple(client);
        assertFalse(cryptoTransferMultiple2.isAccountId(str2));

        String str3 = "000";
        CryptoTransferMultiple cryptoTransferMultiple3 = new CryptoTransferMultiple(client);
        assertFalse(cryptoTransferMultiple3.isAccountId(str3));

        String str4 = "-";
        CryptoTransferMultiple cryptoTransferMultiple4 = new CryptoTransferMultiple(client);
        assertFalse(cryptoTransferMultiple4.isAccountId(str4));
    }

    @Test
    public void testRecipientList() {
        var client = Hedera.createHederaClient();

        List<String> accountList = Arrays.asList("1001", "1002", "1003");
        List<String> amountList= Arrays.asList("100", "9888486986", "10000001100000");
        CryptoTransferMultiple cryptoTransferMultiple = new CryptoTransferMultiple(client);
        cryptoTransferMultiple.verifiedRecipientMap(accountList, amountList);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testRecipientList1() {
//        thrown.expect(IllegalArgumentException.class);
//        thrown.expectMessage("Please check your recipient list");
//        thrown.reportMissingExceptionWithMessage("Exception expected");
//        List<String> accountList = Arrays.asList("1001", "1002", "1003");
//        List<String> amountList= Arrays.asList("100", "9888486986", "1000000 1100000");
//        CryptoTransferMultiple cryptoTransferMultiple = new CryptoTransferMultiple();
//        cryptoTransferMultiple.recipientList(accountList, amountList);
    }
}
