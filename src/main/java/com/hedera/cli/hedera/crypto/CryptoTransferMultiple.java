package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TransactionBuilder;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;
import com.hedera.hashgraph.sdk.proto.*;
import io.grpc.MethodDescriptor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import shadow.org.codehaus.plexus.util.StringUtils;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.*;

@Command(name= "multiple",
        description = "@|fg(magenta) Transfer hbars to multiple accounts|@",
        helpCommand = true)
public class CryptoTransferMultiple  extends TransactionBuilder<CryptoTransferTransaction> implements Runnable {

    @Option(names = {"-r", "--recipient"}, split = " ", arity = "0..*",
            description = "Recipient to transfer to"
                    +"%n@|bold,underline Usage:|@%n"
                    + "@|fg(yellow) transfer multiple -r=1001,1002,1003,-a=100,100,100|@")
    private String[] recipient;

    @Option(names = {"-a", "--recipientAmt"}, split = " ", arity = "0..*", description = "Amount to transfer")
    private String[] recipientAmt;

    long sum = 0;

    private final CryptoTransferTransactionBody.Builder builder = bodyBuilder.getCryptoTransferBuilder();
    private final TransferList.Builder transferList = builder.getTransfersBuilder();

    protected CryptoTransferMultiple(@Nullable Client client) {
        super(client);
    }

    @Override
    public void run() {

        var recipientList = Arrays.asList(recipient);
        var amountList = Arrays.asList(recipientAmt);
        verifiedRecipientMap(recipientList, amountList);
        var operatorId = Hedera.getOperatorId();

        var map = verifiedRecipientMap(recipientList,amountList);
        map.forEach((key, value) -> {
            if (map.size() != amountList.size()) {
                throw new IllegalArgumentException("Please check your recipient list");
            }
            var account = value.accountId.toProto();
            var amount = value.amount;
            transferList.addAccountAmounts(
                    AccountAmount.newBuilder()
                    .setAccountID(account)
                    .setAmount(amount));
            System.out.println(key + ":" + "Account: " + value.accountId + " Amount: " + value.amount);
        });
        System.out.println(transferList);
//        new CryptoTransferTransaction(client)
//                .addTransfer(transferList.build());
    }

    public Map<Integer, Recipient> verifiedRecipientMap(List<String > accountList, List<String> amountList) {
        AccountId accountId;
        String acc, amt;
        Map<Integer, Recipient> map = new HashMap<>();
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
        System.out.println("Total sum is " + sum);
        return map;
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

    @Override
    protected void doValidate() {

    }

    @Override
    protected MethodDescriptor<Transaction, TransactionResponse> getMethod() {
        return null;
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
