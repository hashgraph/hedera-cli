package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import shadow.org.codehaus.plexus.util.StringUtils;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Command(name= "multiple",
        description = "@|fg(magenta) Transfer hbars to multiple accounts|@",
        helpCommand = true)
public class CryptoTransferMultiple implements Runnable  {

    @Option(names = {"-r", "--recipient"}, split = " ", arity = "0..*",
            description = "Recipient to transfer to"
                    +"%n@|bold,underline Usage:|@%n"
                    + "@|fg(yellow) transfer multiple -r=1001,1002,1003,-a=100,100,100|@")
    private String[] recipient;

    @Option(names = {"-a", "--recipientAmt"}, split = " ", arity = "0..*", description = "Amount to transfer")
    private String[] recipientAmt;

    @Override
    public void run() {
        System.out.println("TODO multiple transfer list");
        System.out.println(Arrays.asList(recipient));
        System.out.println(Arrays.asList(recipientAmt));
        System.out.println(recipient.length + " " + recipientAmt.length);

        var recipientList = Arrays.asList(recipient);
        var amountList = Arrays.asList(recipientAmt);
        System.out.println(recipientList);
        System.out.println(amountList);
        recipientList(recipientList, amountList);
        var operatorId = Hedera.getOperatorId();
        var client = Hedera.createHederaClient();

        // TODO figure out sdk for cryptotransfer
//        new CryptoTransferTransaction(client)
//                .addSender(operatorId, 100)
//                .addTransfer();

    }

    public void recipientList(List<String > accountList, List<String> amountList) {
        AccountId accountId;
        String acc, amt;
        Map<Integer, Recipient> map = new HashMap<>();
        long sum = 0;

        try {
            System.out.println(accountList);
            System.out.println(amountList);
            if (accountList.size() != amountList.size())
                System.err.println("Lists aren't the same size");
            else {
                for (int i = 0; i < accountList.size(); ++i) {

                    acc = accountList.get(i);
                    amt = amountList.get(i);
                    var cleanAccString = StringUtils.isNumeric(acc);
                    if (cleanAccString) {
                        if (isAccountId(acc) && isNumeric(amt)) {
                            System.out.println(acc);
                            accountId = AccountId.fromString("0.0." + acc);
                            var amount = new BigInteger(amt);
                            sum += amount.longValue();
                            Recipient recipient = new Recipient(accountId, amount.longValue());
                            map.put(i, recipient);
                        }
                    }
                }
                System.out.println("Total sum is " + sum);
                map.forEach((key, value) -> {
                    if (map.size() != amountList.size()) {
                        throw new IllegalArgumentException("Please check your recipient list");
                    }
                    System.out.println(key + ":" + "Acc: " + value.accountId + " Amt: " + value.amount);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        if (str.matches("^[0]+$")) {
            return false;
        }

        try {
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
