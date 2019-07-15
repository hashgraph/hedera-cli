package hedera.cli;

import com.hedera.hashgraph.sdk.HederaException;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;

import java.math.BigInteger;

public final class CryptoTransfer {
    private CryptoTransfer() { }

    public static void main(String[] args) throws HederaException {
        var operatorId = ExampleHelper.getOperatorId();
        var client = ExampleHelper.createHederaClient();

        var recipientId = AccountId.fromString("0.0.1004");
        var amount = new BigInteger("500000000"); //5hbars

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

        System.out.println("" + operatorId + " balance = " + senderBalanceAfter);
        System.out.println("" + recipientId + " balance = " + receiptBalanceAfter);
//        System.out.println("Transfer memo: " + record.getMemo());
    }
}

