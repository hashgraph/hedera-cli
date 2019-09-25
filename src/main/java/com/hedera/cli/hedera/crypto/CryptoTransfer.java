package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;
import com.hedera.hashgraph.sdk.HederaException;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;
import com.hedera.cli.hedera.Hedera;

import java.math.BigInteger;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "single",
        description = "@|fg(225) Transfer hbars to a single account|@%n",
        helpCommand = true)
public class CryptoTransfer implements Runnable {

    @Option(names = {"-r", "--recipient"}, arity = "1", description = "Recipient to transfer to"
            + "%n@|bold,underline Usage:|@%n"
            + "@|fg(yellow) transfer single -r=1234,-a=100|@")
    private String recipient;

    @Option(names = {"-a", "--recipientAmt"}, arity = "1", description = "Amount to transfer")
    private String recipientAmt;

    private String memoString;

    private InputReader inputReader;
    private String isInfoCorrect;

    public CryptoTransfer(InputReader inputReader) {
        this.inputReader = inputReader;
    }

    @Override
    public void run() {
        try {
            memoString = inputReader.prompt("Memo field");
            Hedera hedera = new Hedera();
            var operatorId = hedera.getOperatorId();
            var client = hedera.createHederaClient();
            var recipientId = AccountId.fromString("0.0." + recipient);
            System.out.println(recipientId);
            var amount = new BigInteger(recipientAmt);

            isInfoCorrect = inputReader.prompt("\nOperator: " + operatorId
                    + "\nRecipient: " + recipientId + "\nAmount: " + amount
                    + "\n\n yes/no \n");
            if (isInfoCorrect.contains("yes")) {
                System.out.println("Info is correct, let's go!");

                var senderBalanceBefore = client.getAccountBalance(operatorId);
                var receiptBalanceBefore = client.getAccountBalance(recipientId);
                System.out.println("" + operatorId + " balance = " + senderBalanceBefore);
                System.out.println("" + recipientId + " balance = " + receiptBalanceBefore);
                new CryptoTransferTransaction(client)
                        // .addSender and .addRecipient can be called as many times as you want as long
                        // as the total sum from
                        // both sides is equivalent
                        .addSender(operatorId, amount.longValue())
                        .addRecipient(recipientId, amount.longValue())
                        .setMemo(memoString)
                        // As we are sending from the operator we do not need to explicitly sign the
                        // transaction
                        .executeForReceipt();

                System.out.println("transferring " + amount.longValue() + " tinybar...");
                var senderBalanceAfter = client.getAccountBalance(operatorId);
                var receiptBalanceAfter = client.getAccountBalance(recipientId);
                System.out.println("" + operatorId + " balance = " + senderBalanceAfter +
                        "\n" + recipientId + " balance = " + receiptBalanceAfter);

            } else {
                System.out.println("Nope, incorrect, let's make some changes");
            }
        } catch (HederaException e) {
            e.printStackTrace();
        }
    }
}

