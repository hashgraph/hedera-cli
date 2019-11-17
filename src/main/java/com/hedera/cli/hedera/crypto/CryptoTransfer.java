package com.hedera.cli.hedera.crypto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@NoArgsConstructor
@Getter
@Setter
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Command
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
    private ValidateAccounts validateAccounts;

    @Autowired
    private ValidateAmount validateAmount;

    @Autowired
    private ValidateTransferList validateTransferList;

    @ArgGroup(exclusive = false, multiplicity = "1")
    private CryptoTransferOptions o;

    private List<CryptoTransferOptions> cryptoTransferOptionsList;

    private boolean skipPreview;
    private String isInfoCorrect;
    private String memoString = "";

    private List<String> transferList;
    private List<String> amountList;
    private List<String> senderList;
    private List<String> recipientList;
    private List<String> finalAmountList;
    private boolean isTiny;

    private AccountId account;
    private long amountInTiny;
    private Client client;
    private TransactionId transactionId;
    private CryptoTransferTransaction cryptoTransferTransaction;

    private String tinybarListArgs;
    private String hbarListArgs;
    private String senderListArgs;
    private String recipientListArgs;

    @Spec
    private CommandSpec spec;

    @Override
    public void run() {

        if (!validateAmount.check(o)) {
            return;
        }

        if (!validateAccounts.check(o)) {
            return;
        }

        if (!validateTransferList.verifyAmountList(o)) return;
        transferListToPromptPreviewMap();

        try {
            reviewAndExecute(hedera.getOperatorId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            // do nothing
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    public boolean isSkipPreview() {
        skipPreview = o.dependent.skipPreview;
        return skipPreview;
    }

    public void handle(String... args) {
        new CommandLine(this).execute(args);
        // this triggers this.run()
    }

    public void reviewAndExecute(AccountId operatorId) throws InvalidProtocolBufferException, TimeoutException, InterruptedException {
        // transfer preview for user
        Map<Integer, PreviewTransferList> map = transferListToPromptPreviewMap();
        memoString = accountManager.promptMemoString(inputReader);
        if (isSkipPreview()) {
            executeCryptoTransfer(operatorId);
        } else {
            // Prompt memostring input
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

    public boolean isTiny() {
        isTiny = validateAmount.isTiny(o);
        return isTiny;
    }

    public CryptoTransferTransaction addTransferList() {
        finalAmountList = getFinalAmountList();
        transferList = getTransferList();
        for (int i = 0; i < finalAmountList.size(); ++i) {
            amountInTiny = Long.parseLong(finalAmountList.get(i));
            account = AccountId.fromString(transferList.get(i));
            cryptoTransferTransaction.addTransfer(account, amountInTiny);
        }
        return cryptoTransferTransaction;
    }

    private void executeCryptoTransfer(AccountId operatorId) throws InvalidProtocolBufferException, TimeoutException, InterruptedException {
        TransactionReceipt transactionReceipt;
        byte[] signedTxnBytes;
        transactionId = new TransactionId(operatorId);
        senderList = validateAccounts.getSenderList(o);
        setSenderList(senderList);
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
        } finally {
            client.close();
        }
    }

    private byte[] senderSignsTransaction(Ed25519PrivateKey senderPrivKey, byte[] transactionData)
            throws InvalidProtocolBufferException {
        return Transaction.fromBytes(transactionData).sign(senderPrivKey).toBytes();
    }

    private byte[] signAndCreateTxBytesWithOperator() throws InvalidProtocolBufferException {
        byte[] signedTxnBytes = new byte[0];
        String senderPrivKeyInString;
        Ed25519PrivateKey senderPrivKey;
        for (int i = 0; i < senderList.size(); i++) {
            if (senderList.get(i).equals(hedera.getOperatorId().toString())) {
                signedTxnBytes = cryptoTransferTransaction.toBytes();
            } else {
                senderPrivKeyInString = inputReader.prompt(
                        "Input private key of sender: " + senderList.get(i) + " to sign transaction", "secret", false);
                if (!StringUtil.isNullOrEmpty(senderPrivKeyInString)) {
                    try {
                        senderPrivKey = Ed25519PrivateKey.fromString(senderPrivKeyInString);
                    } catch (Exception e) {
                        shellHelper.printError("Private key is not in the right ED25519 string format");
                        return null;
                    }
                    signedTxnBytes = senderSignsTransaction(senderPrivKey, cryptoTransferTransaction.toBytes());
                }
            }
        }
        return signedTxnBytes;
    }

    public void setFinalAmountList(List<String> finalAmountList) {
        this.finalAmountList = finalAmountList;
    }

    public Map<Integer, PreviewTransferList> transferListToPromptPreviewMap() {
        Map<Integer, PreviewTransferList> map = new HashMap<>();
        String acc;
        String amt;
        finalAmountList = validateTransferList.getFinalAmountList(o);
        setFinalAmountList(finalAmountList);
        transferList = validateAccounts.getTransferList(o);
        setTransferList(transferList);
        isTiny = isTiny();
        for (int i = 0; i < transferList.size(); ++i) {
            acc = transferList.get(i);
            if (isTiny) {
                amt = finalAmountList.get(i);
            } else {
                amt = validateAmount.convertLongToHbar(finalAmountList.get(i));
            }
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
