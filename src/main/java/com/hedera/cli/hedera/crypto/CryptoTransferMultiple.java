package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.*;

@Command(name= "multiple",
        description = "@|fg(magenta) Transfer hbars to multiple accounts|@",
        helpCommand = true)
public class CryptoTransferMultiple implements Runnable {

    @Option(names = {"-r", "--recipient"}, split = " ", arity = "0..*",
            description = "Recipient to transfer to"
                    +"%n@|bold,underline Usage:|@%n"
                    + "@|fg(yellow) transfer multiple -r=1001,1002,1003,-a=100,100,100|@")
    private String[] recipient;

    @Option(names = {"-a", "--recipientAmt"}, split = " ", arity = "0..*", description = "Amount to transfer")
    private String[] recipientAmt;

    @Override
    public void run() {

        try {
            Hedera hedera = new Hedera();
            var recipientList = Arrays.asList(recipient);
            var amountList = Arrays.asList(recipientAmt);
            verifiedRecipientMap(recipientList, amountList);
            var operatorId = Hedera.getOperatorId();
            var client = hedera.createHederaClient();

            var senderTotal = sumOfTransfer(recipientAmt);
            System.out.println("Sender total: " + -senderTotal);
            var map = verifiedRecipientMap(recipientList,amountList);
            CryptoTransferTransaction cryptoTransferTransaction = new CryptoTransferTransaction(client);
            cryptoTransferTransaction.addTransfer(operatorId, -senderTotal);
            map.forEach((key, value) -> {
                if (map.size() != amountList.size()) {
                    throw new IllegalArgumentException("Please check your recipient list");
                }
                var account = value.accountId;
                var amount = value.amount;
                cryptoTransferTransaction.addTransfer(account, amount);
                System.out.println("Recipient "+ key + ":" + "Account: " + value.accountId + " Amount: " + value.amount);
            });
            var senderBalanceBefore = client.getAccountBalance(operatorId);
            System.out.println("" + operatorId + " balance = " + senderBalanceBefore);
                System.out.println("CryptoTransferTransaction");
                cryptoTransferTransaction.build().execute();
                System.out.println("transferring...");
            var senderBalanceAfter = client.getAccountBalance(operatorId);
            System.out.println("" + operatorId + " balance = " + senderBalanceAfter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, Recipient> verifiedRecipientMap(List<String > accountList, List<String> amountList) {
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
        if(str == null || str.isEmpty()) {
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
        if(str == null || str.isEmpty()) {
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
        } catch(Exception e) {
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
