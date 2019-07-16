package hedera.cli;

import com.hedera.hashgraph.sdk.HederaException;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.math.BigInteger;

@ShellComponent
public class MyCommand {

    @ShellMethod("Add two integers together.")
    public String add(String a, String b) {
        return a + b;
    }

    @ShellMethod("Display stuff.")
    public String echo(int a, int b, int c) {
        return String.format("You said a=%d, b=%d, c=%d", a, b, c);
    }

    @ShellMethod("Crypto transfer")
    public String cryptotransfer(String recipient, String recipientAmt) throws HederaException {
        var operatorId = ExampleHelper.getOperatorId();
        var client = ExampleHelper.createHederaClient();

        var recipientId = AccountId.fromString("0.0." + recipient);
        var amount = new BigInteger(recipientAmt);

        var senderBalanceBefore = client.getAccountBalance(operatorId);
        var receiptBalanceBefore = client.getAccountBalance(recipientId);
        System.out.println("" + operatorId + " balance = " + senderBalanceBefore);
        System.out.println("" + recipientId + " balance = " + receiptBalanceBefore);

        var record = new CryptoTransferTransaction(client)
                // .addSender and .addRecipient can be called as many times as you want as long as the total sum from
                // both sides is equivalent
                .addSender(operatorId, amount.longValue())
                .addRecipient(recipientId, amount.longValue())
                .setMemo("transfer test")
                // As we are sending from the operator we do not need to explicitly sign the transaction
                .executeForRecord();

        System.out.println("transferred " + amount.longValue() + "...");

        var senderBalanceAfter = client.getAccountBalance(operatorId);
        var receiptBalanceAfter = client.getAccountBalance(recipientId);

        return String.format(
                "" + operatorId + " balance = " + senderBalanceAfter,
                "" + recipientId + " balance = " + receiptBalanceAfter
        );
    }
}