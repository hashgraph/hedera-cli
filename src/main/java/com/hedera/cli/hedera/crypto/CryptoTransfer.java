package com.hedera.cli.hedera.crypto;

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
import com.hedera.hashgraph.sdk.*;
import com.hedera.hashgraph.sdk.Transaction;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionRecord;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Spec;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String mPreview = "no";
    private boolean isTiny;
    private String isInfoCorrect;
    private String memoString = "";

    private List<String> transferList;
    private List<String> amountList;
    private List<String> senderList;
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

        hbarAmtArgs = cryptoTransferOptions.exclusive.recipientAmtHBars;
        tinybarAmtArgs = cryptoTransferOptions.exclusive.recipientAmtTinyBars;
        transferListArgs = cryptoTransferOptions.dependent.senderList + "," + cryptoTransferOptions.dependent.recipientList;
        mPreview = cryptoTransferOptions.dependent.mPreview;

        if (StringUtil.isNullOrEmpty(tinybarAmtArgs) && StringUtil.isNullOrEmpty(hbarAmtArgs)
                || (!StringUtil.isNullOrEmpty(tinybarAmtArgs) && !StringUtil.isNullOrEmpty(hbarAmtArgs))) {
            shellHelper.printError("You have to provide a transaction amount in hbars or tinybars");
            return;
        }

        if (!StringUtil.isNullOrEmpty(tinybarAmtArgs)) {
            // tinybars not empty
            shellHelper.printInfo("here in tiny loop");

            // Verify transferlist and amountlist are equal
            transferList = Arrays.asList(transferListArgs.split(","));
            amountList = Arrays.asList(tinybarAmtArgs.split(","));
            boolean listAreEqual = verifyEqualList(transferList, amountList);
            if (!listAreEqual) return;

            // Verify list of senders and recipients
            boolean transferListVerified = verifyTransferList(transferList);
            if (!transferListVerified) return;

            // Check sum of transfer is zero
            isTiny = true;
            boolean isZeroSum = sumOfTinybarsInLong(amountList);
            if (!isZeroSum) return;
        }
        if (!StringUtil.isNullOrEmpty(hbarAmtArgs)) {
            //hbars not empty
            shellHelper.printInfo("here in hbar loop");

            // Verify transferlist and amountlist are equal
            transferList = Arrays.asList(transferListArgs.split(","));
            amountList = Arrays.asList(hbarAmtArgs.split(","));
            boolean listAreEqual = verifyEqualList(transferList, amountList);
            if (!listAreEqual) return;

            // Verify list of senders and recipients
            boolean transferListVerified = verifyTransferList(transferList);
            if (!transferListVerified) return;

            // Check sum of transfer is zero
            isTiny = false;
            boolean isZeroSum = sumOfHbarsInLong(amountList);
            if (!isZeroSum) return;
        }

        // Preview for user
        Map<Integer, PreviewTransferList> map = transferListToPromptPreviewMap(transferList, amountList);
        // handle preview error gracefully here
        AccountId operatorId = hedera.getOperatorId();
        try {
            reviewAndExecute(operatorId, map);
        } catch (InvalidProtocolBufferException e) {
            shellHelper.printError(e.getMessage());
        }
    }

    public void reviewAndExecute(AccountId operatorId,
                                 Map<Integer, PreviewTransferList> map) throws InvalidProtocolBufferException {
        if ("no".equals(mPreview)) {
            executeCryptoTransfer(operatorId);
        } else if ("yes".equals(mPreview)) {
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
        } else {
            shellHelper.printError("Error in commandline");
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
            // transactions are not allowed to .sign() more than once, otherwise SDK throws "transaction already signed with key: "
            // If more than 2 senders are created in list and we do not setOperator when we instantiate a new Client,
            // SDK throws "java.lang.IllegalStateException: transaction builder failed validation: at least one transfer required "
//            client = hedera.createHederaClientWithoutSettingOperator();
//            cryptoTransferTransaction = new CryptoTransferTransaction(client);
//            cryptoTransferTransaction.sign(hedera.getOperatorKey());
//            cryptoTransferTransaction.setMemo(memoString);
//            cryptoTransferTransaction.setTransactionId(transactionId);
//            // the total sum of all transfers adds up to zero
//            // Dynamic population of transfer List
//            cryptoTransferTransaction = addTransferList();
//            // sender list exist, check if sender
//            signedTxnBytes = signAndCreateTxBytesWithoutOperator();
        } else {
            client = hedera.createHederaClient();
            cryptoTransferTransaction = new CryptoTransferTransaction(client);
            cryptoTransferTransaction.setMemo(memoString);
            cryptoTransferTransaction.setTransactionId(transactionId);
            cryptoTransferTransaction = addTransferList();
            signedTxnBytes = signAndCreateTxBytesWithOperator();
        }
        try {
            transactionReceipt = Transaction.fromBytes(client, signedTxnBytes)
                    .executeForReceipt();
            if (transactionReceipt.getStatus().toString().equals("SUCCESS")) {
                printAndSaveRecords(client, transactionId, operatorId);
            }
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    private byte[] signAndCreateTxBytesWithoutOperator() throws InvalidProtocolBufferException {
        byte[] signedTxnBytes = new byte[0];
        String senderPrivKeyInString;
        for (int i = 0; i < senderList.size(); i++) {
            if (senderList.get(i).equals(hedera.getOperatorId().toString())) {
                signedTxnBytes = cryptoTransferTransaction.sign(hedera.getOperatorKey()).toBytes();
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

    private CryptoTransferTransaction addTransferList() {
        for (int i = 0; i < amountList.size(); ++i) {
            System.out.println("transferlist " + transferList.get(i));
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

    private void printAndSaveRecords(Client client, TransactionId transactionId, AccountId operatorId) throws HederaException {
        TransactionRecord record;
        record = new TransactionRecordQuery(client)
                .setTransactionId(transactionId)
                .setPaymentDefault(5000000)
                .execute();
        printBalance(client, operatorId);
        // save all transaction record into ~/.hedera/[network_name]/transaction/[file_name].json
        if (record != null) {
            saveTransactionToJson(record);
        }
    }

    private byte[] senderSignsTransaction(Ed25519PrivateKey senderPrivKey, byte[] transactionData) throws InvalidProtocolBufferException {
        return Transaction.fromBytes(transactionData)
                .sign(senderPrivKey)
                .toBytes();
    }

    public Map<Integer, PreviewTransferList> transferListToPromptPreviewMap(List<String> transferList, List<String> amountList) {
        Map<Integer, PreviewTransferList> map = new HashMap<>();
        String acc;
        String amt;
        for (int i = 0; i < transferList.size(); ++i) {
            acc = transferList.get(i);
            amt = amountList.get(i);
            PreviewTransferList previewTransferList = new PreviewTransferList(AccountId.fromString(acc), amt);
            map.put(i, previewTransferList);
        }
        return map;
    }

    private String promptPreview(AccountId operatorId, Map<Integer, PreviewTransferList> previewTransferListMap) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String jsonStringTransferList = ow.writeValueAsString(previewTransferListMap);
            return inputReader.prompt("\nOperator\n" + operatorId + "\nTransfer List\n"
                    + jsonStringTransferList + "\n\nIs this correct?" + "\nyes/no");
        } catch (Exception e) {
            shellHelper.printError("Some error occurred");
            return null;
        }
    }

    public boolean verifyEqualList(List<String> transferList, List<String> amountList) {
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

    public boolean sumOfHbarsInLong(List<String> amountList) {
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
            System.out.println("sum of hbar is zero");
            zeroSum = true;
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

    public boolean sumOfTinybarsInLong(List<String> amountList) {
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
            System.out.println("sum of tiny is zero");
            zeroSum = true;
        }
        return zeroSum;
    }

    public long convertTinybarToLong(String amountInTinybar) {
        return Long.parseLong(amountInTinybar);
    }

    public boolean verifyZeroSum(long sum) {
        return sum == 0L;
    }

    private void saveTransactionToJson(TransactionRecord record) {
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
