package com.hedera.cli.hedera.crypto;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Transaction;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
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
            description = "Recipient to transfer to"
                    + "%n@|bold,underline Usage:|@%n"
                    + "@|fg(yellow) transfer multiple -r=1001,1002,1003,-a=100,100,100|@")
    private String[] recipient;

    @Option(names = {"-a", "--recipientAmt"}, split = " ", arity = "1..*", description = "Amount to transfer")
    private String[] recipientAmt;

    @Option(names = {"-s", "--senderAccountID"}, arity = "1", description = "AccountID of sender who is transferring")
    private String senderAccountIDInString;

    private InputReader inputReader;
    private Ed25519PrivateKey senderPrivKey;

    public CryptoTransferMultiple(InputReader inputReader) {
        this.inputReader = inputReader;
    }

    @Override
    public void run() {
        try {
            senderAccountIDInString = inputReader.prompt("Input sender accountID");
            String transferAmountInStr = inputReader.prompt("Input transfer amount");
            String senderPrivKeyInString = inputReader.prompt("Input sender private key", "secret", false);
            senderPrivKey = Ed25519PrivateKey.fromString(senderPrivKeyInString);
            System.out.println(senderAccountIDInString + ":" + senderPrivKeyInString);
            Hedera hedera = new Hedera();
            var recipientList = Arrays.asList(recipient);
            var amountList = Arrays.asList(recipientAmt);
            verifiedRecipientMap(recipientList, amountList);

            var operatorId = hedera.getOperatorId();
            var client = hedera.createHederaClient().setMaxTransactionFee(100000000);

            var senderAccountID = AccountId.fromString("0.0." + senderAccountIDInString);
            BigInteger transferAmount = new BigInteger(transferAmountInStr);

            var senderTotal = sumOfTransfer(recipientAmt);
            System.out.println("Sender total: " + -senderTotal);
            var map = verifiedRecipientMap(recipientList, amountList);
            CryptoTransferTransaction cryptoTransferTransaction = new CryptoTransferTransaction(client);
            cryptoTransferTransaction.addTransfer(senderAccountID, -transferAmount.longValue());
            System.out.println(senderTotal + " = " + transferAmount.longValue());

            map.forEach((key, value) -> {
                if (map.size() != amountList.size()) {
                    throw new IllegalArgumentException("Please check your recipient list");
                }
                var account = value.accountId;
                var amount = value.amount;
                cryptoTransferTransaction.addTransfer(account, amount);
                System.out.println("Recipient " + key + ":" + "Account: " + value.accountId + " Amount: " + value.amount);
            });

            var senderBalanceBefore = client.getAccountBalance(senderAccountID);
            var operatorBalanceBefore = client.getAccountBalance(operatorId);
            System.out.println(senderAccountID + " sender balance = " + senderBalanceBefore);
            System.out.println(operatorId + " operator balance = " + operatorBalanceBefore);

            System.out.println("CryptoTransferTransaction");

            var signedTxnBytes = senderSignsTransaction(client, senderPrivKey, cryptoTransferTransaction.toBytes());

            Transaction.fromBytes(client, signedTxnBytes)
                    .executeForReceipt();

            System.out.println("transferring...");
            var operatorBalanceAfter = client.getAccountBalance(operatorId);
            var senderBalanceAfter = client.getAccountBalance(senderAccountID);

            System.out.println(senderAccountID + "sender balance = " + senderBalanceAfter);
            System.out.println(operatorId + " operator balance = " + operatorBalanceAfter);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
