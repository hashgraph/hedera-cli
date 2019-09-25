package com.hedera.cli.hedera.crypto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.utils.TransactionObj;
import com.hedera.cli.hedera.utils.Utils;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Transaction;
import com.hedera.hashgraph.sdk.TransactionRecord;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import jdk.jshell.execution.Util;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.*;

@Command(name = "multiple",
        description = "@|fg(225) Transfer hbars to multiple accounts with multiple senders"
                + "%nWhereby default account is the operator, ie the paying account for transaction fees,"
                + "%nwhile sender is the account transferring the hbars to the recipient(s)|@",
        helpCommand = true)
public class CryptoTransferMultiple implements Runnable {

    @Spec
    CommandSpec spec;

    @Option(names = {"-r", "--recipient"}, split = " ", arity = "1..*",
            description = "Recipient accountID to transfer to, shardNum and realmNum not needed"
                    + "%n@|bold,underline Usage:|@%n"
                    + "@|fg(yellow) transfer multiple -r=1001,1002,1003,-a=100,100,100|@")
    private String[] recipient;

    @Option(names = {"-a", "--recipientAmt"}, split = " ", arity = "1..*", description = "Amount to transfer")
    private String[] recipientAmt;

    private String senderAccountIDInString;
    private String memoString = "";

    private InputReader inputReader;
    private Ed25519PrivateKey senderPrivKey;

    public CryptoTransferMultiple(InputReader inputReader) {
        this.inputReader = inputReader;
    }

    @Override
    public void run() {
        try {

            // Cli prompt for input from user
            memoString = inputReader.prompt("Memo field");
            senderAccountIDInString = inputReader.prompt("Input sender accountID in the format xxxx");
            String transferAmountInStr = inputReader.prompt("Input transfer amount");
            String senderPrivKeyInString = inputReader.prompt("Input sender private key", "secret", false);
            senderPrivKey = Ed25519PrivateKey.fromString(senderPrivKeyInString);

            System.out.println(senderPrivKey);
            Hedera hedera = new Hedera();
            var recipientList = Arrays.asList(recipient);
            var amountList = Arrays.asList(recipientAmt);
            verifiedRecipientMap(recipientList, amountList);

            // Operator is the current default account user
            var operatorId = hedera.getOperatorId();

            // Currently set max fee as 1 hbar = 100,000,000 tinybars
            var client = hedera.createHederaClient().setMaxTransactionFee(100000000);

            // Create a multi-sender crypto transfer where sender does not have to pay
            // transaction fees = network fee + node fee
            var senderAccountID = AccountId.fromString("0.0." + senderAccountIDInString);
            BigInteger transferAmount = new BigInteger(transferAmountInStr);

            // Sender and recipient's total must always be zero
            var senderTotal = sumOfTransfer(recipientAmt);
            System.out.println("Sender total: " + -senderTotal);

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
                    throw new IllegalArgumentException("Please check your recipient list");
                }
                var account = value.accountId;
                var amount = value.amount;
                cryptoTransferTransaction.addTransfer(account, amount);
                System.out.println("Recipient " + key + " = " + "Account: " + value.accountId + " Amount: " + value.amount);
            });

            if (StringUtils.isEmpty(memoString)) {
                memoString = "";
            }
            // Sets the memo that is required in some transactions
            cryptoTransferTransaction.setMemo(memoString);

            // Get balance is always free, does not require any keys
            var senderBalanceBefore = client.getAccountBalance(senderAccountID);
            var operatorBalanceBefore = client.getAccountBalance(operatorId);
            System.out.println(senderAccountID + " sender balance BEFORE = " + senderBalanceBefore);
            System.out.println(operatorId + " operator balance BEFORE = " + operatorBalanceBefore);

            System.out.println("CryptoTransferTransaction");

            // Since there is more than 1 sender in this multi-sender transaction example
            // ie operator and sender,
            // Sender must obviously sign to allow funds to be transferred out
            var signedTxnBytes = senderSignsTransaction(client, senderPrivKey, cryptoTransferTransaction.toBytes());

            // Get records or get receipt or use mirror node :D
            // GetRecords are more expensive to call
            // GetReceipt is free
            TransactionRecord record = Transaction.fromBytes(client, signedTxnBytes)
                    .executeForRecord();

            System.out.println("transferring...");
            var operatorBalanceAfter = client.getAccountBalance(operatorId);
            var senderBalanceAfter = client.getAccountBalance(senderAccountID);

            System.out.println(senderAccountID + " sender balance AFTER = " + senderBalanceAfter);
            System.out.println(operatorId + " operator balance AFTER = " + operatorBalanceAfter);

            // save all transaction record into ~/.hedera/[network_name]/transaction/[file_name].json
            saveTransactionToJson(record);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveTransactionToJson(TransactionRecord record) {
        System.out.println("Transfer tx ID : " + record.getTransactionId().getAccountId().toString());
        System.out.println("Transfer tx ID valid start: " + record.getTransactionId().getValidStart().toString());
        System.out.println("Transfer tx ID valid start seconds: " + record.getTransactionId().getValidStart().getEpochSecond());
        System.out.println("Transfer tx ID valid start nano: " + record.getTransactionId().getValidStart().getNano());
        System.out.println("Transfer tx fee: " + record.getTransactionFee());
        System.out.println("Transfer consensus timestamp: " + record.getConsensusTimestamp());
        System.out.println("Transfer receipt status: " + record.getReceipt().getStatus());
        System.out.println("Transfer memo: " + record.getMemo());

        String txTimestamp = record.getTransactionId().getValidStart().getEpochSecond() + "-"
                + record.getTransactionId().getValidStart().getNano();
        String txID = record.getTransactionId().getAccountId().toString() + "-" + txTimestamp;

        TransactionObj txObj = new TransactionObj();
        txObj.setTxID(txID);
        txObj.setTxMemo(record.getMemo());
        txObj.setTxFee(BigInteger.valueOf(record.getTransactionFee()));
        txObj.setTxConsensusTimestamp(record.getConsensusTimestamp());
        txObj.setTxValidStart(txTimestamp);

        Utils utils = new Utils();
        utils.saveTransactionsToJson(txID, txObj);
    }

    private byte[] senderSignsTransaction(Client client, Ed25519PrivateKey senderPrivKey, byte[] transactionData) throws InvalidProtocolBufferException {
        return Transaction.fromBytes(client, transactionData)
                .sign(senderPrivKey)
                .toBytes();
    }

    public Map<Integer, Recipient> verifiedRecipientMap(List<String> accountList, List<String> amountList) {
        AccountId accountId;
        String acc, amt;
        Map<Integer, Recipient> map = new HashMap<>();
        long sum = 0;

        try {
            System.out.println(accountList);
            System.out.println(amountList);
            if (accountList.size() != amountList.size())
                throw new IllegalArgumentException("Lists aren't the same size");
            else {
                for (int i = 0; i < accountList.size(); ++i) {
                    acc = accountList.get(i);
                    amt = amountList.get(i);
                    if (StringUtils.isNumeric(acc)) {
                        if (isAccountId(acc) && isNumeric(amt)) {
                            accountId = AccountId.fromString("0.0." + acc);
                            var amount = new BigInteger(amt);
                            sum += amount.longValue();
                            Recipient recipient = new Recipient(accountId, amount.longValue());
                            map.put(i, recipient);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Recipients total is " + sum);
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

    private class Recipient {

        private AccountId accountId;
        private Long amount;

        private Recipient(AccountId accountId, Long amount) {
            this.accountId = accountId;
            this.amount = amount;
        }
    }
}
