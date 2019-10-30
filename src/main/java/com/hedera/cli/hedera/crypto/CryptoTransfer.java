package com.hedera.cli.hedera.crypto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.utils.AccountManager;
import com.hedera.cli.hedera.utils.Composite;
import com.hedera.cli.hedera.utils.CryptoTransferUtils;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;
import lombok.Setter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;
import picocli.CommandLine.ArgGroup;

@NoArgsConstructor
@Setter
@Getter
@Component
@Command(name = "single", description = "@|fg(225) Transfer hbars to a single account|@%n", helpCommand = true)
public class CryptoTransfer implements Runnable {

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private Hedera hedera;

    @Autowired
    private AccountManager accountManager;

    @Autowired
    private CryptoTransferUtils cryptoTransferUtils;

    @Autowired
    private Composite composite;

    @Spec
    private CommandSpec spec;

    @ArgGroup(exclusive = false, multiplicity = "1")
    private List<Composite> composites;

    private String mPreview;
    private String memoString;
    private InputReader inputReader;
    private String isInfoCorrect;
    private long amountInTiny;

    @Override
    public void run() {
        try {
            // size 2
            for (int i = 0; i < composites.size(); i++) {
                // get the exclusive arg by user ie tinybars or hbars
                composite = composites.get(i);
            }
            String hBars = composite.exclusive.recipientAmtHBars;
            String tinyBars = composite.exclusive.recipientAmtTinyBars;
            String recipient = composite.dependent.recipient;
            mPreview = composite.dependent.mPreview;

            var operatorId = hedera.getOperatorId();
            var client = hedera.createHederaClient();
            var recipientId = AccountId.fromString(recipient);

            if (!StringUtil.isNullOrEmpty(tinyBars) && StringUtil.isNullOrEmpty(hBars)) {
                memoString = accountManager.promptMemoString(inputReader);
                amountInTiny = cryptoTransferUtils.verifyTransferInTinyBars(tinyBars);
                if (amountInTiny == 0L) {
                    shellHelper.printError("Tinybars must be whole numbers");
                    return;
                }
                reviewAndExecute(client, operatorId, recipientId, amountInTiny);
            } else if (StringUtil.isNullOrEmpty(tinyBars) && !StringUtil.isNullOrEmpty(hBars)) {
                memoString = accountManager.promptMemoString(inputReader);
                amountInTiny = cryptoTransferUtils.verifyTransferInHbars(hBars);
                if (amountInTiny == 0L) {
                    shellHelper.printError("Hbar must be > 0");
                    return;
                }
                reviewAndExecute(client, operatorId, recipientId, amountInTiny);
            } else if (StringUtil.isNullOrEmpty(tinyBars) && StringUtil.isNullOrEmpty(hBars)) {
                shellHelper.printError("You have to provide a transaction amount in hbars or tinybars");
            } else {
                shellHelper.printError("Error in commandline");
            }
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    public void reviewAndExecute(Client client, AccountId operatorId, AccountId recipientId, long amountInTiny) {
        if ("no".equals(mPreview)) {
            executeCryptoTransfer(client, operatorId, recipientId, amountInTiny);
        } else if ("yes".equals(mPreview)) {
            isInfoCorrect = promptPreview(operatorId, recipientId, amountInTiny);
            if ("yes".equals(isInfoCorrect)) {
                shellHelper.print("Info is correct, let's go!");
                executeCryptoTransfer(client, operatorId, recipientId, amountInTiny);
            } else if ("no".equals(isInfoCorrect)) {
                shellHelper.print("Nope, incorrect, let's make some changes");
            } else {
                shellHelper.printError("Input must be either yes or no");
            }
        } else {
            shellHelper.printError("Error in commandline");
        }
    }

    private String promptPreview(AccountId operatorId, AccountId recipientId, long amount) {
        BigDecimal amountInHbar = new BigDecimal(amount);
        BigDecimal promptAmount = amountInHbar.divide(new BigDecimal("100000000")
                .setScale(12, RoundingMode.HALF_EVEN));
        return inputReader.prompt("\nOperator: " + operatorId + "\nRecipient: " + recipientId
                + "\nAmount in hbars: " + promptAmount.toPlainString()
                + "\nAmount in tinybars: " + amount
                + "\n\nIs this correct?" + "\nyes/no");
    }

    public void executeCryptoTransfer(Client client, AccountId operatorId, AccountId recipientId, long amount) {
        try {
            var senderBalanceBefore = client.getAccountBalance(operatorId);
            var receiptBalanceBefore = client.getAccountBalance(recipientId);
            shellHelper.print("" + operatorId + " balance = " + senderBalanceBefore);
            shellHelper.print("" + recipientId + " balance = " + receiptBalanceBefore);
            TransactionId transactionId = new TransactionId(operatorId);
            new CryptoTransferTransaction(client)
                    // .addSender and .addRecipient can be called as many times as you want as long
                    // as the total sum from
                    // both sides is equivalent
                    .addSender(operatorId, amount).addRecipient(recipientId, amount)
                    .setMemo(memoString)
                    .setTransactionId(transactionId)
                    // As we are sending from the operator we do not need to explicitly sign the
                    // transaction
                    .executeForReceipt();

            shellHelper.printInfo("transferring " + amount + " tinybar...");
            var senderBalanceAfter = client.getAccountBalance(operatorId);
            var receiptBalanceAfter = client.getAccountBalance(recipientId);
            shellHelper.printSuccess("" + operatorId + " balance = " + senderBalanceAfter + "\n" + recipientId
                    + " balance = " + receiptBalanceAfter);
            shellHelper.printSuccess("Success!");
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }
}
