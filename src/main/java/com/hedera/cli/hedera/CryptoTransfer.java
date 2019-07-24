package com.hedera.cli.hedera;

import com.hedera.hashgraph.sdk.HederaException;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;
import com.hedera.cli.ExampleHelper;
import com.hedera.cli.shell.ProgressCounter;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import java.math.BigInteger;

@ShellComponent
public class CryptoTransfer {

    @Autowired
    @Lazy
    private Terminal terminal;


    @Autowired
    ProgressCounter progressCounter;

    @ShellMethod("Cryptotransfer")
    public String cryptotransfer(String recipient, String recipientAmt) throws HederaException, InterruptedException {
        var operatorId = ExampleHelper.getOperatorId();
        var client = ExampleHelper.createHederaClient();

        var recipientId = AccountId.fromString("0.0." + recipient);
        var amount = new BigInteger(recipientAmt);

        var senderBalanceBefore = client.getAccountBalance(operatorId);
        var receiptBalanceBefore = client.getAccountBalance(recipientId);
        System.out.println("" + operatorId + " balance = " + senderBalanceBefore);
        System.out.println("" + recipientId + " balance = " + receiptBalanceBefore);

        new CryptoTransferTransaction(client)
                // .addSender and .addRecipient can be called as many times as you want as long
                // as the total sum from
                // both sides is equivalent
                .addSender(operatorId, amount.longValue()).addRecipient(recipientId, amount.longValue())
                .setMemo("transfer test")
                // As we are sending from the operator we do not need to explicitly sign the
                // transaction
                .executeForRecord();

        System.out.println("transferring " + amount.longValue() + " tinybar...");

        var senderBalanceAfter = client.getAccountBalance(operatorId);
        var receiptBalanceAfter = client.getAccountBalance(recipientId);

        return "" + operatorId + " balance = " + senderBalanceAfter +
                "\n" + recipientId + " balance = " + receiptBalanceAfter;
    }
}

