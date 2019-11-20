package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.PreviewTransferList;
import com.hedera.cli.models.TransactionManager;
import com.hedera.cli.shell.ShellHelper;

import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    private ValidateTransferList validateTransferList;

    @Mock
    private Hedera hedera;

    @Mock
    private InputReader inputReader;

    @Mock
    private TransactionManager transactionManager;

    @Mock
    private CryptoTransferTransaction cryptoTransferTransaction;

    private CryptoTransferOptions cryptoTransferOptions;
    private CryptoTransferOptions.Dependent dependent;
    private List<String> expectedAmountList;
    private List<String> expectedTransferList;
    private List<String> senderList;
    private List<String> recipientList;
    private String sender;
    private String recipient1;
    private String recipient2;
    private String senderAmt;
    private String recipient1Amt;
    private String recipient2Amt;
    private String someMemo;
    private String senderListArgs;
    private String recipientListArgs;
    private String tinybarListArgs;
    private AccountId operatorId;

    @BeforeEach
    public void setUp() {
        sender = "0.0.1001";
        senderList = new ArrayList<>();
        senderList.add(sender);

        recipient1 = "0.0.1002";
        recipient2 = "0.0.1003";
        recipientList = new ArrayList<>();
        recipientList.add(recipient1);
        recipientList.add(recipient2);

        senderAmt = "-1400";
        recipient1Amt = "1000";
        recipient2Amt = "400";

        tinybarListArgs = senderAmt + "," + recipient1Amt + "," + recipient2Amt;
        senderListArgs = sender;
        recipientListArgs = recipient1 + "," + recipient2;

        expectedAmountList = new ArrayList<>();
        expectedAmountList.add(senderAmt);
        expectedAmountList.add(recipient1Amt);
        expectedAmountList.add(recipient2Amt);

        expectedTransferList = new ArrayList<>();
        expectedTransferList.add(sender);
        expectedTransferList.add(recipient1);
        expectedTransferList.add(recipient2);

        someMemo = "some memo";

        operatorId = hedera.getOperatorId();
    }

    @Test
    public void dependenciesExist() {
        cryptoTransfer.setShellHelper(shellHelper);
        assertEquals(shellHelper, cryptoTransfer.getShellHelper());
        cryptoTransfer.setAccountManager(accountManager);
        assertEquals(accountManager, cryptoTransfer.getAccountManager());
        cryptoTransfer.setHedera(hedera);
        assertEquals(hedera, cryptoTransfer.getHedera());
        cryptoTransfer.setInputReader(inputReader);
        assertEquals(inputReader, cryptoTransfer.getInputReader());
        cryptoTransfer.setTransactionManager(transactionManager);
        assertEquals(transactionManager, cryptoTransfer.getTransactionManager());
        cryptoTransfer.setO(cryptoTransferOptions);
        assertEquals(cryptoTransferOptions, cryptoTransfer.getO());
        cryptoTransfer.setValidateAccounts(validateAccounts);
        assertEquals(validateAccounts, cryptoTransfer.getValidateAccounts());
        cryptoTransfer.setValidateAmount(validateAmount);
        assertEquals(validateAmount, cryptoTransfer.getValidateAmount());
        cryptoTransfer.setValidateTransferList(validateTransferList);
        assertEquals(validateTransferList, cryptoTransfer.getValidateTransferList());
    }

    @Test
    public void settersAndGetters() {
        cryptoTransfer.setMemoString("hello");
        assertEquals("hello", cryptoTransfer.getMemoString());
        cryptoTransfer.setSenderList(senderList);
        assertEquals(senderList, cryptoTransfer.getSenderList());
        cryptoTransfer.setRecipientList(recipientList);
        assertEquals(recipientList, cryptoTransfer.getRecipientList());
        cryptoTransfer.setAmountList(expectedAmountList);
        assertEquals(expectedAmountList, cryptoTransfer.getAmountList());
        cryptoTransfer.setClient(hedera.createHederaClient());
        assertEquals(hedera.createHederaClient(), cryptoTransfer.getClient());
        cryptoTransfer.setIsInfoCorrect("yes");
        assertEquals("yes", cryptoTransfer.getIsInfoCorrect());
        cryptoTransfer.setAccount(AccountId.fromString(sender));
        assertEquals(AccountId.fromString(sender), cryptoTransfer.getAccount());
        cryptoTransfer.setSenderListArgs(senderListArgs);
        assertEquals(senderListArgs, cryptoTransfer.getSenderListArgs());
        cryptoTransfer.setRecipientListArgs(recipientListArgs);
        assertEquals(recipientListArgs, cryptoTransfer.getRecipientListArgs());
        cryptoTransfer.setTinybarListArgs(tinybarListArgs);
        assertEquals(tinybarListArgs, cryptoTransfer.getTinybarListArgs());
    }

    @Test
    public void isSkipPreviewTrue() {
        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(true);
        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransfer.setO(cryptoTransferOptions);
        assertTrue(cryptoTransfer.isSkipPreview());
    }

    @Test
    public void isTinyFalse() {
        when(validateAmount.isTiny(cryptoTransferOptions)).thenReturn(false);
        assertFalse(cryptoTransfer.isTiny());
    }

    @Test
    public void isTinyTrue() {
        when(validateAmount.isTiny(cryptoTransferOptions)).thenReturn(true);
        assertTrue(cryptoTransfer.isTiny());
    }

    @Test
    public void addTransferList() {
        cryptoTransfer.setFinalAmountList(expectedAmountList);
        cryptoTransfer.setTransferList(expectedTransferList);
        cryptoTransfer.setCryptoTransferTransaction(cryptoTransfer.addTransferList());
        assertEquals(cryptoTransferTransaction, cryptoTransfer.getCryptoTransferTransaction());
    }

    @Test
    public void transferListToPromptPreviewMap() {
        when(validateAccounts.getTransferList(any())).thenReturn(expectedTransferList);
        when(validateTransferList.getFinalAmountList(any())).thenReturn(expectedAmountList);

        Map<Integer, PreviewTransferList> expectedMap = new HashMap<>();
        PreviewTransferList previewTransferList = new PreviewTransferList(AccountId.fromString(sender), senderAmt);
        PreviewTransferList previewTransferList1 = new PreviewTransferList(AccountId.fromString(recipient1), recipient1Amt);
        PreviewTransferList previewTransferList2 = new PreviewTransferList(AccountId.fromString(recipient2), recipient2Amt);
        expectedMap.put(0, previewTransferList);
        expectedMap.put(1, previewTransferList1);
        expectedMap.put(2, previewTransferList2);

        Map<Integer, PreviewTransferList> actualMap = cryptoTransfer.transferListToPromptPreviewMap();
        assertEquals(expectedMap.get(0).getAccountId(), actualMap.get(0).getAccountId());
        assertEquals(expectedMap.get(1).getAccountId(), actualMap.get(1).getAccountId());
        assertEquals(expectedMap.get(2).getAccountId(), actualMap.get(2).getAccountId());
    }

    @Test
    public void transferListToPromptPreviewMapIsTiny() {
        when(validateAmount.isTiny(any())).thenReturn(true);
        when(validateAccounts.getTransferList(any())).thenReturn(expectedTransferList);
        when(validateTransferList.getFinalAmountList(any())).thenReturn(expectedAmountList);

        Map<Integer, PreviewTransferList> expectedMap = new HashMap<>();
        PreviewTransferList previewTransferList = new PreviewTransferList(AccountId.fromString(sender), senderAmt);
        PreviewTransferList previewTransferList1 = new PreviewTransferList(AccountId.fromString(recipient1), recipient1Amt);
        PreviewTransferList previewTransferList2 = new PreviewTransferList(AccountId.fromString(recipient2), recipient2Amt);
        expectedMap.put(0, previewTransferList);
        expectedMap.put(1, previewTransferList1);
        expectedMap.put(2, previewTransferList2);

        Map<Integer, PreviewTransferList> actualMap = cryptoTransfer.transferListToPromptPreviewMap();
        assertEquals(expectedMap.get(0).getAccountId(), actualMap.get(0).getAccountId());
        assertEquals(expectedMap.get(1).getAccountId(), actualMap.get(1).getAccountId());
        assertEquals(expectedMap.get(2).getAccountId(), actualMap.get(2).getAccountId());
    }

    @Test
    public void promptPreviewIncorrect() throws InvalidProtocolBufferException, InterruptedException,
            TimeoutException, JsonProcessingException {

        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(false);
        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransfer.setO(cryptoTransferOptions);

        when(validateAmount.isTiny(any())).thenReturn(true);
        when(validateAccounts.getTransferList(any())).thenReturn(expectedTransferList);
        when(validateTransferList.getFinalAmountList(any())).thenReturn(expectedAmountList);
        when(accountManager.promptMemoString(inputReader)).thenReturn(someMemo);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Map<Integer, PreviewTransferList> expectedMap = new HashMap<>();
        PreviewTransferList previewTransferList = new PreviewTransferList(AccountId.fromString(sender), senderAmt);
        PreviewTransferList previewTransferList1 = new PreviewTransferList(AccountId.fromString(recipient1), recipient1Amt);
        PreviewTransferList previewTransferList2 = new PreviewTransferList(AccountId.fromString(recipient2), recipient2Amt);
        expectedMap.put(0, previewTransferList);
        expectedMap.put(1, previewTransferList1);
        expectedMap.put(2, previewTransferList2);
        String jsonStringTransferList = ow.writeValueAsString(expectedMap);
        String prompt = "\nOperator\n" + operatorId + "\nTransfer List\n" + jsonStringTransferList
                + "\n\nIs this correct?" + "\nyes/no";
        when(inputReader.prompt(prompt)).thenReturn("no");

        cryptoTransfer.reviewAndExecute(operatorId);

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).print(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "Nope, incorrect, let's make some changes";
        assertEquals(expected, actual);
    }

    @Test
    public void promptPreviewCorrectAndExecute() throws InvalidProtocolBufferException, InterruptedException,
            TimeoutException, JsonProcessingException {

        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(false);
        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransfer.setO(cryptoTransferOptions);

        when(validateAmount.isTiny(any())).thenReturn(true);
        when(validateAccounts.getTransferList(any())).thenReturn(expectedTransferList);
        when(validateTransferList.getFinalAmountList(any())).thenReturn(expectedAmountList);
        when(accountManager.promptMemoString(inputReader)).thenReturn(someMemo);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Map<Integer, PreviewTransferList> expectedMap = new HashMap<>();
        PreviewTransferList previewTransferList = new PreviewTransferList(AccountId.fromString(sender), senderAmt);
        PreviewTransferList previewTransferList1 = new PreviewTransferList(AccountId.fromString(recipient1), recipient1Amt);
        PreviewTransferList previewTransferList2 = new PreviewTransferList(AccountId.fromString(recipient2), recipient2Amt);
        expectedMap.put(0, previewTransferList);
        expectedMap.put(1, previewTransferList1);
        expectedMap.put(2, previewTransferList2);
        String jsonStringTransferList = ow.writeValueAsString(expectedMap);
        String prompt = "\nOperator\n" + operatorId + "\nTransfer List\n" + jsonStringTransferList
                + "\n\nIs this correct?" + "\nyes/no";
        when(inputReader.prompt(prompt)).thenReturn("yes");

        CryptoTransfer cryptoTransfer1 = Mockito.spy(cryptoTransfer);
        doNothing().when(cryptoTransfer1).executeCryptoTransfer(any());

        cryptoTransfer1.reviewAndExecute(operatorId);

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).print(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "Info is correct, senders will need to sign the transaction to release funds";
        assertEquals(expected, actual);

    }

    @Test
    public void noPreviewExecute() throws InvalidProtocolBufferException, InterruptedException,
            TimeoutException {

        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(true);
        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransfer.setO(cryptoTransferOptions);

        when(validateAmount.isTiny(any())).thenReturn(true);
        when(validateAccounts.getTransferList(any())).thenReturn(expectedTransferList);
        when(validateTransferList.getFinalAmountList(any())).thenReturn(expectedAmountList);
        when(accountManager.promptMemoString(inputReader)).thenReturn(someMemo);

        CryptoTransfer cryptoTransfer1 = Mockito.spy(cryptoTransfer);
        doNothing().when(cryptoTransfer1).executeCryptoTransfer(any());

        cryptoTransfer1.reviewAndExecute(operatorId);
        verify(cryptoTransfer1).reviewAndExecute(any());
    }

    @Test
    public void promptPreviewError() throws JsonProcessingException, InvalidProtocolBufferException, InterruptedException, TimeoutException {

        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(false);
        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransfer.setO(cryptoTransferOptions);

        when(validateAmount.isTiny(any())).thenReturn(true);
        when(validateAccounts.getTransferList(any())).thenReturn(expectedTransferList);
        when(validateTransferList.getFinalAmountList(any())).thenReturn(expectedAmountList);
        when(accountManager.promptMemoString(inputReader)).thenReturn(someMemo);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Map<Integer, PreviewTransferList> expectedMap = new HashMap<>();
        String jsonStringTransferList = ow.writeValueAsString(expectedMap);
        String prompt = "\nOperator\n" + operatorId + "\nTransfer List\n" + jsonStringTransferList
                + "\n\nIs this correct?" + "\nyes/no";
        when(inputReader.prompt(prompt)).thenReturn("again");
        cryptoTransfer.reviewAndExecute(operatorId);

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper, times(2)).printError(valueCapture.capture());
        List<String> actual = valueCapture.getAllValues();
        String expected = "Some error occurred";
        assertEquals(expected, actual.get(0));
        String expected1 = "Input must be either yes or no";
        assertEquals(expected1, actual.get(1));
    }

    @Test
    public void executeCryptoTransferRun() throws InvalidProtocolBufferException, InterruptedException, TimeoutException {
        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(true);
        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransfer.setO(cryptoTransferOptions);

        when(validateAmount.check(cryptoTransferOptions)).thenReturn(true);
        when(validateAccounts.check(cryptoTransferOptions)).thenReturn(true);
        when(validateTransferList.verifyAmountList(cryptoTransferOptions)).thenReturn(true);
        when(accountManager.promptMemoString(inputReader)).thenReturn(someMemo);

        when(validateAmount.isTiny(cryptoTransferOptions)).thenReturn(true);
        when(validateAccounts.getTransferList(cryptoTransferOptions)).thenReturn(expectedTransferList);
        when(validateTransferList.getFinalAmountList(cryptoTransferOptions)).thenReturn(expectedAmountList);
        when(accountManager.promptMemoString(inputReader)).thenReturn(someMemo);

        Map<Integer, PreviewTransferList> expectedMap = new HashMap<>();
        PreviewTransferList previewTransferList = new PreviewTransferList(AccountId.fromString(sender), senderAmt);
        PreviewTransferList previewTransferList1 = new PreviewTransferList(AccountId.fromString(recipient1), recipient1Amt);
        PreviewTransferList previewTransferList2 = new PreviewTransferList(AccountId.fromString(recipient2), recipient2Amt);
        expectedMap.put(0, previewTransferList);
        expectedMap.put(1, previewTransferList1);
        expectedMap.put(2, previewTransferList2);

        cryptoTransfer.setFinalAmountList(expectedAmountList);
        cryptoTransfer.setTransferList(expectedTransferList);
        cryptoTransfer.setCryptoTransferTransaction(cryptoTransfer.addTransferList());

        CryptoTransfer cryptoTransfer1 = Mockito.spy(cryptoTransfer);
        doNothing().when(cryptoTransfer1).executeCryptoTransfer(any());
        cryptoTransfer1.run();
        verify(cryptoTransfer1).executeCryptoTransfer(any());
    }

    @Test
    public void executeCryptoTransferValidateAmountFalse() {
        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(true);
        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransfer.setO(cryptoTransferOptions);

        when(validateAmount.check(cryptoTransferOptions)).thenReturn(false);
        cryptoTransfer.run();
        verify(validateAmount, times(1)).check(cryptoTransferOptions);
        verify(validateAccounts, times(0)).check(cryptoTransferOptions);
    }

    @Test
    public void executeCryptoTransferValidateAccounttFalse() {
        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(true);
        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransfer.setO(cryptoTransferOptions);

        when(validateAmount.check(cryptoTransferOptions)).thenReturn(true);
        when(validateAccounts.check(cryptoTransferOptions)).thenReturn(false);
        cryptoTransfer.run();
        verify(validateAmount, times(1)).check(cryptoTransferOptions);
        verify(validateAccounts, times(1)).check(cryptoTransferOptions);
        verify(validateTransferList, times(0)).verifyAmountList(cryptoTransferOptions);
    }
}
