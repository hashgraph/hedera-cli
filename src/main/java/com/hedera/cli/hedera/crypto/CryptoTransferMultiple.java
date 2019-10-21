package com.hedera.cli.hedera.crypto;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.utils.Utils;
import com.hedera.cli.models.Recipient;
import com.hedera.cli.models.Sender;
import com.hedera.cli.models.TransactionObj;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Transaction;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionRecord;
import com.hedera.hashgraph.sdk.TransactionRecordQuery;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@NoArgsConstructor
@Setter
@Component
@Command(name = "multiple", description = "@|fg(225) Transfer hbars to multiple accounts with multiple senders"
        + "%nWhereby default account is the operator, ie the paying account for transaction fees,"
        + "%nwhile sender is the account transferring the hbars to the recipient(s)|@", helpCommand = true)
public class CryptoTransferMultiple implements Runnable {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private Utils utils;

    @Spec
    private CommandSpec spec;

    @Option(names = { "-a",
            "--accountId" }, split = " ", arity = "1..*", required = true, description = "Recipient accountID to transfer to, shardNum and realmNum not needed"
                    + "%n@|bold,underline Usage:|@%n"
                    + "@|fg(yellow) transfer multiple -a=1001,1002,1003 -r=100,100,100|@")
    private String[] recipient;

    @Option(names = { "-r",
            "--recipientAmt" }, split = " ", arity = "1..*", required = true, description = "Amount to transfer in tinybar")
    private String[] recipientAmt;

    @Option(names = { "-n",
            "noPreview" }, arity = "0..1", defaultValue = "yes", fallbackValue = "no", description = "Cryptotransfer preview option with optional parameter\n"
                    + "Default: ${DEFAULT-VALUE},\n" + "if specified without parameters: ${FALLBACK-VALUE}")
    private String mPreview = "no";

    private String senderAccountIDInString;
    private String memoString = "";

    private InputReader inputReader;
    private Ed25519PrivateKey senderPrivKey;

    private String isInfoCorrect;

    @Override
    public void run() {
        try {

            // Cli prompt for input from user
            memoString = inputReader.prompt("Memo field");
            senderAccountIDInString = inputReader.prompt("Input sender accountID in the format xxxx");
            String transferAmountInStr = inputReader.prompt("Input transfer amount");

            // Hedera hedera = new Hedera(context);
            var recipientList = Arrays.asList(recipient);
            var amountList = Arrays.asList(recipientAmt);
            // Operator is the current default account user
            var operatorId = hedera.getOperatorId();
            var client = hedera.createHederaClient();

            // Create a multi-sender crypto transfer where sender does not have to pay
            // transaction fees = network fee + node fee
            var senderAccountID = AccountId.fromString("0.0." + senderAccountIDInString);
            BigInteger transferAmount = new BigInteger(transferAmountInStr);

            // Sender and recipient's total must always be zero
            long senderTotal = sumOfTransfer(recipientAmt) - transferAmount.longValue();
            if (senderTotal != 0) {
                shellHelper.printError("Transaction total amount must add up to a zero sum!");
            }

            // Simple check, can be more comprehensive
            var map = verifiedRecipientMap(recipientList, amountList);

            // Create a crypto transfer transaction
            CryptoTransferTransaction cryptoTransferTransaction = new CryptoTransferTransaction(client);

            // .addTransfer can be called as many times as you want as long as
            // the total sum of all transfers adds up to zero
            cryptoTransferTransaction.addTransfer(senderAccountID, -transferAmount.longValue());

            // Dynamic population of recipient List
            map.forEach((key, value) -> {
                if (map.size() != amountList.size()) {
                    shellHelper.printError("Please check your recipient list");
                }
                var account = value.accountId;
                var amount = value.amount;
                cryptoTransferTransaction.addTransfer(account, amount);
            });

            if (StringUtils.isEmpty(memoString)) {
                memoString = "";
            }
            // Sets the memo that is required in some transactions
            cryptoTransferTransaction.setMemo(memoString);

            // Save info
            Sender sender = new Sender();
            sender.setAccountId(senderAccountID);
            sender.setAmount(transferAmount);
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String jsonStringSender = ow.writeValueAsString(sender);
            String jsonStringRecipient = ow.writeValueAsString(map);

            // handle preview error gracefully here
            if (noPreview(mPreview).equals("no")) {
                // do not show preview
                executeCryptoTransferMultiple(hedera, senderAccountID, operatorId, cryptoTransferTransaction);
            } else if (noPreview(mPreview).equals("yes")) {
                // show preview and execute cryptotransfer
                isInfoCorrect = promptPreview(operatorId, jsonStringSender, jsonStringRecipient);
                if ("yesy".equals(isInfoCorrect)) {
                    shellHelper.print("Info is correct, let's go!");
                    executeCryptoTransferMultiple(hedera, senderAccountID, operatorId, cryptoTransferTransaction);
                } else if ("no".equals(isInfoCorrect)) {
                    shellHelper.print("Nope, incorrect, let's make some changes");
                } else {
                    shellHelper.printError("Input must either been yes or no");
                }
            } else {
                shellHelper.printError("Error in commandline");
            }

        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    private String promptPreview(AccountId operatorId, String jsonStringSender, String jsonStringRecipient) {
        return inputReader.prompt("\nOperator\n" + operatorId + "\nSender\n" + jsonStringSender + "\nRecipient\n"
                + jsonStringRecipient + "\n\nIs this correct?" + "\nyes/no");
    }

    private void executeCryptoTransferMultiple(Hedera hedera, AccountId senderAccountID, AccountId operatorId,
            CryptoTransferTransaction cryptoTransferTransaction) {
        try {
            var client = hedera.createHederaClient();
            var senderBalanceBefore = client.getAccountBalance(senderAccountID);
            var operatorBalanceBefore = client.getAccountBalance(operatorId);
            shellHelper.print(senderAccountID + " sender balance BEFORE = " + senderBalanceBefore);
            shellHelper.print(operatorId + " operator balance BEFORE = " + operatorBalanceBefore);
            TransactionRecord record;
            // if accountId of sender is the same as the operatorId, only sign once
            if (senderAccountID.toString().equals(hedera.getOperatorId().toString())) {
                TransactionId txId = Transaction.fromBytes(client, cryptoTransferTransaction.toBytes()).execute();
                TransactionRecordQuery q = new TransactionRecordQuery(client).setTransactionId(txId);
                record = q.execute();
            } else {
                // Since there is more than 1 sender in this multi-sender transaction example
                // ie operator and sender are different,
                // Sender must obviously sign to allow funds to be transferred out
                String senderPrivKeyInString = inputReader.prompt("Input sender private key", "secret", false);
                senderPrivKey = Ed25519PrivateKey.fromString(senderPrivKeyInString);
                var signedTxnBytes = senderSignsTransaction(client, senderPrivKey, cryptoTransferTransaction.toBytes());
                
                TransactionId txId = Transaction.fromBytes(client, signedTxnBytes).execute();
                TransactionRecordQuery q = new TransactionRecordQuery(client).setTransactionId(txId);
                record = q.execute();
            }

            shellHelper.printInfo("transferring...");
            var operatorBalanceAfter = client.getAccountBalance(operatorId);
            var senderBalanceAfter = client.getAccountBalance(senderAccountID);

            // Get balance is always free, does not require any keys
            shellHelper.print(senderAccountID + " sender balance AFTER = " + senderBalanceAfter);
            shellHelper.print(operatorId + " operator balance AFTER = " + operatorBalanceAfter);
            // save all transaction record into
            // ~/.hedera/[network_name]/transaction/[file_name].json
            saveTransactionToJson(record);
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    private void saveTransactionToJson(TransactionRecord record) {
        shellHelper.printSuccess("Success!");
        shellHelper.printSuccess("Transfer tx fee: " + record.getTransactionFee());
        shellHelper.printSuccess("Transfer consensus timestamp: " + record.getConsensusTimestamp());
        shellHelper.printSuccess("Transfer receipt status: " + record.getReceipt().getStatus());
        shellHelper.printSuccess("Transfer memo: " + record.getMemo());

        String txTimestamp = record.getTransactionId().getValidStart().getEpochSecond() + "-"
                + record.getTransactionId().getValidStart().getNano();
        String txID = record.getTransactionId().getAccountId().toString() + "-" + txTimestamp;
        shellHelper.printSuccess("Transfer tx ID : " + txID);

        TransactionObj txObj = new TransactionObj();
        txObj.setTxID(txID);
        txObj.setTxMemo(record.getMemo());
        txObj.setTxFee(BigInteger.valueOf(record.getTransactionFee()));
        txObj.setTxConsensusTimestamp(record.getConsensusTimestamp());
        txObj.setTxValidStart(txTimestamp);

        utils.saveTransactionsToJson(txID, txObj);
    }

    private byte[] senderSignsTransaction(Client client, Ed25519PrivateKey senderPrivKey, byte[] transactionData)
            throws InvalidProtocolBufferException {
        return Transaction.fromBytes(client, transactionData).sign(senderPrivKey).toBytes();
    }

    public Map<Integer, Recipient> verifiedRecipientMap(List<String> accountList, List<String> amountList) {
        AccountId accountId;
        String acc;
        String amt;
        Map<Integer, Recipient> map = new HashMap<>();

        try {
            if (accountList.size() != amountList.size())
                shellHelper.printError("Lists aren't the same size");
            else {
                for (int i = 0; i < accountList.size(); ++i) {
                    acc = accountList.get(i);
                    amt = amountList.get(i);
                    if (StringUtils.isNumeric(acc)) {
                        if (isAccountId(acc) && isNumeric(amt)) {
                            accountId = AccountId.fromString("0.0." + acc);
                            var amount = new BigInteger(amt);
                            Recipient recipient1 = new Recipient(accountId, amount.longValue());
                            map.put(i, recipient1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
        return map;
    }

    public long sumOfTransfer(String[] recipientAmt) {
        long sum = 0;

        for (String amt : recipientAmt) {
            var amount = new BigInteger(amt);
            sum += amount.longValue();
        }
        return sum;
    }

    public boolean isNumeric(final String str) {
        // checks null or empty
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public boolean isAccountId(final String str) {
        // checks null or empty
        if (str == null || str.isEmpty()) {
            return false;
        }
        // checks if accountId only contains 0
        if (str.matches("^[0]+$")) {
            return false;
        }
        try {
            // parse string to make sure it can be of type AccountId
            AccountId.fromString("0.0." + str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String noPreview(String preview) {
        if ("no".equals(preview)) {
            mPreview = preview;
        } else if ("yes".equals(preview)) {
            mPreview = preview;
        } else {
            throw new CommandLine.ParameterException(spec.commandLine(), "Option -y removes preview");
        }
        return mPreview;
    }
}
