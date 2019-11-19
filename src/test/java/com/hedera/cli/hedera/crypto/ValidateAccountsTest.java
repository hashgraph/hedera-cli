package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;
import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
public class ValidateAccountsTest {

    @InjectMocks
    private ValidateAccounts validateAccounts;

    @Mock
    private Hedera hedera;

    @Mock
    private ShellHelper shellHelper;

    private CryptoTransferOptions cryptoTransferOptions;
    private CryptoTransferOptions.Exclusive exclusive;
    private CryptoTransferOptions.Dependent dependent;

    private String sender1;
    private String sender2;
    private String senderListArgs;
    private List<String> senderList;
    private String recipient1;
    private String recipient2;
    private String recipientListArgs;
    private List<String> recipientList;

    @BeforeEach
    public void setUp() {
        sender1 = "0.0.1001";
        sender2 = "0.0.1002";
        senderList = new ArrayList<>();
        senderList.add(sender1);
        senderList.add(sender2);

        recipient1 = "0.0.1003";
        recipient2 = "0.0.1004";
        recipientList = new ArrayList<>();
        recipientList.add(recipient1);
        recipientList.add(recipient2);

        senderListArgs = sender1 + "," + sender2;
        recipientListArgs = recipient1 + "," + recipient2;
    }

    @Test
    public void assertAutowiredDependenciesNotNull() {
        validateAccounts.setHedera(hedera);
        assertNotNull(validateAccounts.getHedera());
        validateAccounts.setShellHelper(shellHelper);
        assertNotNull(validateAccounts.getShellHelper());
        validateAccounts.setRecipientList(recipientList);
        assertEquals(recipientList, validateAccounts.getRecipientList());
        validateAccounts.setSenderList(senderList);
        assertEquals(senderList, validateAccounts.getSenderList());
    }


    @Test
    public void senderListHasOperatorTrue() {
        dependent = new CryptoTransferOptions.Dependent();

        dependent.setSkipPreview(false);
        dependent.setRecipientList(recipientListArgs);
        dependent.setSenderList(senderListArgs);

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtHBars("");
        exclusive.setTransferListAmtTinyBars("");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);

        when(hedera.getOperatorId()).thenReturn(AccountId.fromString("0.0.1001"));
        validateAccounts.setCryptoTransferOptions(cryptoTransferOptions);
        assertEquals(cryptoTransferOptions, validateAccounts.getCryptoTransferOptions());
        boolean senderListHasOperator = validateAccounts.senderListHasOperator(cryptoTransferOptions);
        assertTrue(senderListHasOperator);
    }

    @Test
    public void senderListHasNoOperator() {
        dependent = new CryptoTransferOptions.Dependent();

        dependent.setSkipPreview(false);
        dependent.setRecipientList(recipientListArgs);
        dependent.setSenderList(senderListArgs);

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtHBars("");
        exclusive.setTransferListAmtTinyBars("");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);

        when(hedera.getOperatorId()).thenReturn(AccountId.fromString("0.0.1005"));
        validateAccounts.setCryptoTransferOptions(cryptoTransferOptions);

        boolean senderListHasOperator = validateAccounts.senderListHasOperator(cryptoTransferOptions);
        assertFalse(senderListHasOperator);
    }

    @Test
    public void transferListConcatsSenderAndRecipients() {
        dependent = new CryptoTransferOptions.Dependent();

        dependent.setSkipPreview(false);
        dependent.setRecipientList(recipientListArgs);
        dependent.setSenderList(senderListArgs);

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtHBars("");
        exclusive.setTransferListAmtTinyBars("");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);

        List<String> expectedTxList = new ArrayList<>();
        expectedTxList.add("0.0.1001");
        expectedTxList.add("0.0.1002");
        expectedTxList.add("0.0.1003");
        expectedTxList.add("0.0.1004");
        List<String> actualTxList = validateAccounts.getTransferList(cryptoTransferOptions);
        assertEquals(expectedTxList, actualTxList);
    }

    @Test
    public void senderAndRecipientListArgsExist() {
        dependent = new CryptoTransferOptions.Dependent();

        dependent.setSkipPreview(false);
        dependent.setRecipientList(recipientListArgs);
        dependent.setSenderList(senderListArgs);

        exclusive = new CryptoTransferOptions.Exclusive();

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);

        validateAccounts.setCryptoTransferOptions(cryptoTransferOptions);
        assertTrue(validateAccounts.check(cryptoTransferOptions));

        List<String> actualSenderList = validateAccounts.getSenderList(cryptoTransferOptions);
        List<String> actualRecipientList = validateAccounts.getRecipientList(cryptoTransferOptions);
        assertEquals(senderList, actualSenderList);
        assertEquals(recipientList, actualRecipientList);
        assertEquals(senderList, validateAccounts.getSenderList());
        assertEquals(recipientList, validateAccounts.getRecipientList());
        assertEquals("0.0.1001,0.0.1002", validateAccounts.getSenderListArgs());
        assertEquals("0.0.1003,0.0.1004", validateAccounts.getRecipientListArgs());
    }

    @Test
    public void checkFalseSenderNull() {
        dependent = new CryptoTransferOptions.Dependent();

        dependent.setSkipPreview(false);
        dependent.setSenderList("0.1001,0.0.1004");
        dependent.setRecipientList(recipientListArgs);

        exclusive = new CryptoTransferOptions.Exclusive();

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);

        validateAccounts.setCryptoTransferOptions(cryptoTransferOptions);
        boolean incorrect = validateAccounts.check(cryptoTransferOptions);
        assertFalse(incorrect);

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).printError(valueCapture.capture());

        String actual = valueCapture.getAllValues().get(0);
        String expected = "Invalid account id in list";
        assertEquals(expected, actual);
    }

    @Test
    public void checkFalseRecipientNull() {
        dependent = new CryptoTransferOptions.Dependent();

        dependent.setSkipPreview(false);
        dependent.setSenderList(senderListArgs);
        dependent.setRecipientList("0.1003,0.0.1004");

        exclusive = new CryptoTransferOptions.Exclusive();

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);

        validateAccounts.setCryptoTransferOptions(cryptoTransferOptions);
        assertFalse(validateAccounts.check(cryptoTransferOptions));

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).printError(valueCapture.capture());

        String actual = valueCapture.getAllValues().get(0);
        String expected = "Invalid account id in list";
        assertEquals(expected, actual);
    }

    @Test
    public void senderListArgsNullOrEmpty() {
        dependent = new CryptoTransferOptions.Dependent();

        dependent.setSkipPreview(false);
        dependent.setRecipientList(recipientListArgs);
        dependent.setSenderList("");

        exclusive = new CryptoTransferOptions.Exclusive();

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);

        when(hedera.getOperatorId()).thenReturn(AccountId.fromString("0.0.1005"));
        validateAccounts.setCryptoTransferOptions(cryptoTransferOptions);
        verify(hedera).getOperatorId();
    }
}
