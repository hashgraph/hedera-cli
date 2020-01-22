package com.hedera.cli.hedera.crypto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.validation.ValidateAccounts;
import com.hedera.cli.hedera.validation.ValidateAmount;
import com.hedera.cli.hedera.validation.ValidateTransferList;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.PreviewTransferList;
import com.hedera.cli.models.TransactionManager;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.proto.ResponseCodeEnum;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.HederaStatusException;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionRecord;
import com.hedera.hashgraph.sdk.TransactionRecordQuery;
import com.hedera.hashgraph.sdk.account.AccountBalanceQuery;
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
    private CryptoTransferPrompts cryptoTransferPrompts;

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

    private boolean skipPreview;
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

        // validation
        if (!validateAmount.check(o) || !validateAccounts.check(o) || !validateTransferList.verifyAmountList(o)) {
            return;
        }

        // preview
        transferListToPromptPreviewMap();

        // execute
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

    public void reviewAndExecute(AccountId operatorId) throws HederaStatusException, TimeoutException, InterruptedException {
        // transfer preview for user
        Map<Integer, PreviewTransferList> map = transferListToPromptPreviewMap();
        memoString = accountManager.promptMemoString(inputReader);
        if (isSkipPreview() || cryptoTransferPrompts.handlePromptPreview(operatorId, map)) {
            executeCryptoTransfer(operatorId);
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

    public void executeCryptoTransfer(AccountId operatorId) throws HederaStatusException, TimeoutException, InterruptedException {
        transactionId = new TransactionId(operatorId);
        senderList = validateAccounts.getSenderList(o);
        setSenderList(senderList);
        if (senderList.size() > 1) {
            shellHelper.printInfo("More than 2 senders not supported");
            return;
        }
        // If more than 2 senders are created in list, and client.setsOperator()
        // transactions are not allowed to .sign() more than once, otherwise SDK throws
        // "transaction already signed with key: "
        // If more than 2 senders are created in list and we do not setOperator when we
        // instantiate a new Client,
        // SDK throws "java.lang.IllegalStateException: transaction builder failed
        // validation: at least one transfer required "
        client = hedera.createHederaClient();

        cryptoTransferTransaction = new CryptoTransferTransaction();
        cryptoTransferTransaction.setTransactionMemo(memoString);
        cryptoTransferTransaction.setTransactionId(transactionId);
        cryptoTransferTransaction = addTransferList();
        // byte[] signedTxnBytes = signAndCreateTxBytesWithOperator();
        cryptoTransferTransaction.execute(client);
        
        try {
            TransactionReceipt receipt = transactionId.getReceipt(client);
            if (receipt.status.code == ResponseCodeEnum.SUCCESS_VALUE) {
                printAndSaveRecords(client, transactionId, operatorId);
            }
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }

    }

    // private byte[] signAndCreateTxBytesWithOperator() {
    //     byte[] signedTxnBytes = new byte[0];
    //     for (int i = 0; i < senderList.size(); i++) {
    //         String sender = senderList.get(i);
    //         if (sender.equals(hedera.getOperatorId().toString())) {
    //             signedTxnBytes = cryptoTransferTransaction.toString().getBytes();
    //         } else {
    //             signedTxnBytes = signAndCreateTxBytesWithPrivateKey(sender);
    //         }
    //     }
    //     return signedTxnBytes;
    // }

    private byte[] signAndCreateTxBytesWithPrivateKey(String sender) {
        String senderPrivKeyInString = inputReader.prompt(
            "Input private key of sender: " + sender + " to sign transaction", "secret", false);
        if (StringUtil.isNullOrEmpty(senderPrivKeyInString)) {
            return null;
        }
        
        try {
            Ed25519PrivateKey senderPrivKey = Ed25519PrivateKey.fromString(senderPrivKeyInString);
            byte[] signedBytes = cryptoTransferTransaction.build(client).sign(senderPrivKey).toBytes();
            // byte[] cryptoTransferTxBytes = cryptoTransferTransaction.toString().getBytes();
            // Transaction cryptoTransferTx = Transaction.fromBytes(cryptoTransferTxBytes);
            // byte[] cryptoTransferTxBytesSigned = cryptoTransferTx.sign(senderPrivKey).toBytes();
            return signedBytes;
        } catch (Exception e) {
            shellHelper.printError("Private key is not in the right ED25519 string format");
            return null;
        }
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



    private void printAndSaveRecords(Client client, TransactionId transactionId, AccountId operatorId)
            throws HederaStatusException, JsonProcessingException {
        TransactionRecord record;
        record = new TransactionRecordQuery()
            .setTransactionId(transactionId)
            .setMaxQueryPayment(5000000)
            .execute(client);
        printBalance(client, operatorId);
        // save all transaction record into
        // ~/.hedera/[network_name]/transaction/[file_name].json
        if (record != null) {
            transactionManager.saveTransactionRecord(record);
        }
    }

    private void printBalance(Client client, AccountId operatorId) {
        try {
            shellHelper.printInfo("transferring...");
            Hbar balance = new AccountBalanceQuery()
                .setAccountId(operatorId)
                .execute(client);
            long operatorBalanceAfter = balance.asTinybar();
            // Get balance is always free, does not require any keys
            shellHelper.print(operatorId + " operator balance AFTER = " + operatorBalanceAfter);
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

}
