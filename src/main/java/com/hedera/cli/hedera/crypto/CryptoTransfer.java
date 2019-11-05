package com.hedera.cli.hedera.crypto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.PreviewTransferList;
import com.hedera.cli.models.TransactionManager;
import com.hedera.cli.models.TransactionObj;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.HederaException;
import com.hedera.hashgraph.sdk.Transaction;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionRecord;
import com.hedera.hashgraph.sdk.TransactionRecordQuery;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@NoArgsConstructor
@Getter
@Setter
@Component
public class CryptoTransfer implements Runnable {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private InputReader inputReader;

    @Autowired
    private AccountManager accountManager;

    @Autowired
    private TransactionManager transactionManager;

    @Autowired
    private CryptoTransferOptions cryptoTransferOptions;

    @Spec
    private CommandSpec spec;

    @ArgGroup(exclusive = false, multiplicity = "1")
    private List<CryptoTransferOptions> cryptoTransferOptionsList;

    private String transferListArgs;
    private String tinybarAmtArgs;
    private String hbarAmtArgs;
    private boolean skipPreview;
    private boolean isTiny = true;
    private String isInfoCorrect;
    private String memoString = "";

    private List<String> transferList;
    private List<String> amountList;
    private List<String> senderList;
    private List<String> recipientList;
    private AccountId account;
    private long amountInTiny;
    private Client client;
    private TransactionId transactionId;
    private CryptoTransferTransaction cryptoTransferTransaction;

    @Override
    public void run() {

        for (int i = 0; i < cryptoTransferOptionsList.size(); i++) {
            // get the exclusive arg by user ie tinybars or hbars
            cryptoTransferOptions = cryptoTransferOptionsList.get(i);
        }

        hbarAmtArgs = cryptoTransferOptions.exclusive.transferListAmtHBars;
        tinybarAmtArgs = cryptoTransferOptions.exclusive.transferListAmtTinyBars;
        transferListArgs = cryptoTransferOptions.dependent.senderList + ","
                + cryptoTransferOptions.dependent.recipientList;
        skipPreview = cryptoTransferOptions.dependent.skipPreview;
        senderList = Arrays.asList((cryptoTransferOptions.dependent.senderList).split(","));
        recipientList = Arrays.asList((cryptoTransferOptions.dependent.recipientList).split(","));

        // additional validation
        if (StringUtil.isNullOrEmpty(tinybarAmtArgs) && StringUtil.isNullOrEmpty(hbarAmtArgs)) {
            shellHelper.printError("You have to provide transaction amounts in hbars or tinybars");
            return;
        }

        // additional validation
        if (!StringUtil.isNullOrEmpty(tinybarAmtArgs) && !StringUtil.isNullOrEmpty(hbarAmtArgs)) {
            shellHelper.printError("Transfer amounts must either be in hbars or tinybars, not both");
            return;
        }

        transferList = Arrays.asList(transferListArgs.split(","));
        if (!StringUtil.isNullOrEmpty(tinybarAmtArgs)) {
            // using tinybars
            amountList = Arrays.asList(tinybarAmtArgs.split(","));
        } else {
            // using hbars
            amountList = Arrays.asList(hbarAmtArgs.split(","));
            isTiny = false;
        }

        if (!validateUserInput(senderList, recipientList, transferList, amountList, isTiny)) {
            return;
        }

        // handle preview error gracefully here
        AccountId operatorId = hedera.getOperatorId();
        try {
            reviewAndExecute(operatorId, senderList, recipientList, transferList, amountList);
        } catch (InvalidProtocolBufferException e) {
            shellHelper.printError(e.getMessage());
        }
    }

    private boolean validateUserInput(List<String> senderList, List<String> recipientList, List<String> transferList,
            List<String> amountList, boolean isTiny) {
        // Verify transferlist and amountlist are equal
        if (!verifyEqualList(senderList, recipientList, transferList, amountList)) {
            return false;
        }
        // Verify list of senders and recipients
        if (!verifyTransferList(transferList)) {
            return false;
        }
        // Check sum of transfer is zero
        return isSumZero(senderList, recipientList, amountList, isTiny);
    }

    public void reviewAndExecute(AccountId operatorId, List<String> senderList, List<String> recipientList,
            List<String> transferList, List<String> amountList) throws InvalidProtocolBufferException {
        // transfer preview for user
        Map<Integer, PreviewTransferList> map = transferListToPromptPreviewMap(senderList, recipientList, transferList,
                amountList);

        if (skipPreview) {
            executeCryptoTransfer(operatorId);
        } else {
            // Prompt memostring input
            memoString = accountManager.promptMemoString(inputReader);
            isInfoCorrect = promptPreview(operatorId, map);
            if ("yes".equals(isInfoCorrect)) {
                shellHelper.print("Info is correct, senders will need to sign the transaction to release funds");
                executeCryptoTransfer(operatorId);
            } else if ("no".equals(isInfoCorrect)) {
                shellHelper.print("Nope, incorrect, let's make some changes");
            } else {
                shellHelper.printError("Input must be either yes or no");
            }
        }
    }

    private void executeCryptoTransfer(AccountId operatorId) throws InvalidProtocolBufferException {
        TransactionReceipt transactionReceipt;
        byte[] signedTxnBytes;
        transactionId = new TransactionId(operatorId);
        senderList = Arrays.asList((cryptoTransferOptions.dependent.senderList).split(","));

        // SDKs does not currently support more than 2 senders.
        // Instantiating Client client = hedera.createHederaClient() sets the operator
        // ie 1 operator, 1 sender, x recipients (supported)
        // ie 1 operator, 0 sender, x recipients (supported)
        if (senderList.size() > 1) {
            shellHelper.printError("Currently does not support more than 2 senders");
            return;
            // If more than 2 senders are created in list, and client.setsOperator()
            // transactions are not allowed to .sign() more than once, otherwise SDK throws
            // "transaction already signed with key: "
            // If more than 2 senders are created in list and we do not setOperator when we
            // instantiate a new Client,
            // SDK throws "java.lang.IllegalStateException: transaction builder failed
            // validation: at least one transfer required "
        } else {
            client = hedera.createHederaClient();
            cryptoTransferTransaction = new CryptoTransferTransaction(client);
            cryptoTransferTransaction.setMemo(memoString);
            cryptoTransferTransaction.setTransactionId(transactionId);
            cryptoTransferTransaction = addTransferList();
            signedTxnBytes = signAndCreateTxBytesWithOperator();
        }
        try {
            transactionReceipt = Transaction.fromBytes(client, signedTxnBytes).executeForReceipt();
            if (transactionReceipt.getStatus().toString().equals("SUCCESS")) {
                printAndSaveRecords(client, transactionId, operatorId);
            }
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    private byte[] signAndCreateTxBytesWithOperator() throws InvalidProtocolBufferException {
        byte[] signedTxnBytes = new byte[0];
        String senderPrivKeyInString;
        for (int i = 0; i < senderList.size(); i++) {
            if (senderList.get(i).equals(hedera.getOperatorId().toString())) {
                signedTxnBytes = cryptoTransferTransaction.toBytes();
            } else {
                senderPrivKeyInString = inputReader.prompt(
                        "Input private key of sender: " + senderList.get(i) + " to sign transaction", "secret", false);
                if (!StringUtil.isNullOrEmpty(senderPrivKeyInString)) {
                    Ed25519PrivateKey senderPrivKey = Ed25519PrivateKey.fromString(senderPrivKeyInString);
                    signedTxnBytes = senderSignsTransaction(senderPrivKey, cryptoTransferTransaction.toBytes());
                }
            }
        }
        return signedTxnBytes;
    }

    public CryptoTransferTransaction addTransferList() {
        for (int i = 0; i < amountList.size(); ++i) {
            if (isTiny) {
                amountInTiny = Long.parseLong(amountList.get(i));
                account = AccountId.fromString(transferList.get(i));
            } else {
                amountInTiny = convertHbarToLong(amountList.get(i));
                account = AccountId.fromString(transferList.get(i));
            }
            cryptoTransferTransaction.addTransfer(account, amountInTiny);
        }
        return cryptoTransferTransaction;
    }

    private void printAndSaveRecords(Client client, TransactionId transactionId, AccountId operatorId)
            throws HederaException, JsonProcessingException {
        TransactionRecord record;
        record = new TransactionRecordQuery(client).setTransactionId(transactionId).setPaymentDefault(5000000)
                .execute();
        printBalance(client, operatorId);
        // save all transaction record into
        // ~/.hedera/[network_name]/transaction/[file_name].json
        if (record != null) {
            saveTransactionToJson(record);
        }
    }

    private byte[] senderSignsTransaction(Ed25519PrivateKey senderPrivKey, byte[] transactionData)
            throws InvalidProtocolBufferException {
        return Transaction.fromBytes(transactionData).sign(senderPrivKey).toBytes();
    }

    public Map<Integer, PreviewTransferList> transferListToPromptPreviewMap(List<String> senderList,
            List<String> recipientList, List<String> transferList, List<String> amountList) {
        ArrayList<String> finalAmountList = new ArrayList<>(amountList);
        if (isSingleSenderRecipientAmount(senderList, recipientList, amountList)) {
            String amount = "-" + String.valueOf(amountList.get(0));
            finalAmountList.add(0, amount);
            this.amountList = finalAmountList;
        }

        Map<Integer, PreviewTransferList> map = new HashMap<>();
        String acc;
        String amt;
        for (int i = 0; i < transferList.size(); ++i) {
            acc = transferList.get(i);
            amt = finalAmountList.get(i);
            PreviewTransferList previewTransferList = new PreviewTransferList(AccountId.fromString(acc), amt);
            map.put(i, previewTransferList);
        }
        return map;
    }

    private String promptPreview(AccountId operatorId, Map<Integer, PreviewTransferList> previewTransferListMap) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String jsonStringTransferList = ow.writeValueAsString(previewTransferListMap);
            return inputReader.prompt("\nOperator\n" + operatorId + "\nTransfer List\n" + jsonStringTransferList
                    + "\n\nIs this correct?" + "\nyes/no");
        } catch (Exception e) {
            shellHelper.printError("Some error occurred");
            return null;
        }
    }

    public boolean verifyEqualList(List<String> senderList, List<String> recipientList, List<String> transferList,
            List<String> amountList) {
        // support the declaration `transfer -s 0.0.1001 -r 0.0.1002 -hb 10_000` so user
        // does not have to provide additional negative amount
        if (isSingleSenderRecipientAmount(senderList, recipientList, amountList)) {
            return true;
        }
        if (transferList.size() != amountList.size()) {
            shellHelper.printError("Lists aren't the same size");
            return false;
        }
        return true;
    }

    public boolean verifyTransferList(List<String> transferList) {
        String accountId;
        for (int i = 0; i < transferList.size(); i++) {
            accountId = transferList.get(i);
            if (!accountManager.isAccountId(accountId)) {
                shellHelper.printError("Please check that accountId is in the right format");
                return false;
            }
        }
        return true;
    }

    private boolean isSumZero(List<String> senderList, List<String> recipientList, List<String> amountList, boolean isTiny) {
        if (isTiny) {
            return sumOfTinybarsInLong(senderList, recipientList, amountList);
        }
        return sumOfHbarsInLong(senderList, recipientList, amountList);
    }

    public boolean sumOfHbarsInLong(List<String> senderList, List<String> recipientList, List<String> amountList) {
        if (isSingleSenderRecipientAmount(senderList, recipientList, amountList)) {
            return true;
        }

        long sum = 0;
        boolean zeroSum = false;
        long hbarsToTiny;

        for (int i = 0; i < amountList.size(); i++) {
            if ("0".equals(amountList.get(i))) {
                shellHelper.printError("Hbars must be more or less than 0");
                return zeroSum;
            }
            hbarsToTiny = convertHbarToLong(amountList.get(i));
            sum += hbarsToTiny;
        }
        if (verifyZeroSum(sum)) {
            zeroSum = true;
        } else {
            shellHelper.printError("Invalid transfer list. Your transfer list must sum up to 0");
        }
        return zeroSum;
    }

    public long convertHbarToLong(String amountInHbar) {
        long hbarsToTiny;
        try {
            long amountInHbars = Long.parseLong(amountInHbar);
            long tinyConversion = 100000000L;
            hbarsToTiny = amountInHbars * tinyConversion;
        } catch (Exception e) {
            BigDecimal bd = new BigDecimal(amountInHbar);
            BigDecimal bdConvertTiny = bd.multiply(new BigDecimal("100000000"));
            hbarsToTiny = Long.parseLong(bdConvertTiny.toPlainString().split("\\.")[0]);
        }
        return hbarsToTiny;
    }

    public boolean sumOfTinybarsInLong(List<String> senderList, List<String> recipientList, List<String> amountList) {
        if (isSingleSenderRecipientAmount(senderList, recipientList, amountList)) {
            return true;
        }

        long sum = 0;
        long tinyBarsVerified;
        boolean zeroSum = false;

        for (int i = 0; i < amountList.size(); i++) {
            if ("0".equals(amountList.get(i))) {
                shellHelper.printError("Tinybars must be more or less than 0");
                return zeroSum;
            }
            try {
                tinyBarsVerified = convertTinybarToLong(amountList.get(i));
            } catch (Exception e) {
                shellHelper.printError("Tinybars must not be a decimal");
                return zeroSum;
            }
            sum += tinyBarsVerified;
        }
        if (verifyZeroSum(sum)) {
            zeroSum = true;
        } else {
            shellHelper.printError("Invalid transfer list. Your transfer list must sum up to 0");
        }
        return zeroSum;
    }

    private boolean isSingleSenderRecipientAmount(List<String> senderList, List<String> recipientList, List<String> amountList) {
        return senderList.size() == 1 && recipientList.size() == 1 && amountList.size() == 1;
    }

    public long convertTinybarToLong(String amountInTinybar) {
        return Long.parseLong(amountInTinybar);
    }

    public boolean verifyZeroSum(long sum) {
        return sum == 0L;
    }

    private void saveTransactionToJson(TransactionRecord record) throws JsonProcessingException {
        shellHelper.printSuccess("Transfer receipt status: " + record.getReceipt().getStatus());
        shellHelper.printSuccess("Transfer transaction fee: " + record.getTransactionFee());
        shellHelper.printSuccess("Transfer consensus timestamp: " + record.getConsensusTimestamp());
        shellHelper.printSuccess("Transfer memo: " + record.getMemo());

        String txID = printTransactionId(record.getTransactionId());

        TransactionObj txObj = new TransactionObj();
        txObj.setTxID(txID);
        txObj.setTxMemo(record.getMemo());
        txObj.setTxFee(record.getTransactionFee());
        txObj.setTxConsensusTimestamp(record.getConsensusTimestamp());
        txObj.setTxValidStart(record.getTransactionId().getValidStart().getEpochSecond() + "-"
                + record.getTransactionId().getValidStart().getNano());

        transactionManager.saveTransactionsToJson(txID, txObj);
    }

    private void printBalance(Client client, AccountId operatorId) {
        try {
            shellHelper.printInfo("transferring...");
            long operatorBalanceAfter = client.getAccountBalance(operatorId);
            // Get balance is always free, does not require any keys
            shellHelper.print(operatorId + " operator balance AFTER = " + operatorBalanceAfter);
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    private String printTransactionId(TransactionId transactionId) {
        String txTimestamp = transactionId.getValidStart().getEpochSecond() + "-"
                + transactionId.getValidStart().getNano();
        String txID = transactionId.getAccountId().toString() + "-" + txTimestamp;
        shellHelper.printSuccess("TransactionID : " + txID);
        return txID;
    }
}