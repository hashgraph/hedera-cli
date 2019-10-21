package com.hedera.cli.hedera.crypto;

import java.math.BigInteger;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@NoArgsConstructor
@Setter
@Component
@Command(name = "single", description = "@|fg(225) Transfer hbars to a single account|@%n", helpCommand = true)
public class CryptoTransfer implements Runnable {

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private Hedera hedera;

    @Spec
    private CommandSpec spec;

    @Option(names = { "-a",
            "--accountId" }, arity = "1", required = true, description = "Recipient's accountID to transfer to, shardNum and realmNum NOT NEEDED")
    private String recipient;

    @Option(names = { "-r",
            "--recipientAmt" }, arity = "1", required = true, description = "Amount to transfer in tinybars")
    private String recipientAmt;

    @Option(names = { "-n",
            "--noPreview" }, arity = "0..1", defaultValue = "yes", fallbackValue = "no", description = "Cryptotransfer preview"
                    + "\noption with optional parameter. Default: ${DEFAULT-VALUE},\n"
                    + "if specified without parameter: ${FALLBACK-VALUE}" + "%n@|bold,underline Usage:|@%n"
                    + "@|fg(yellow) transfer single -a 1234 -r 100|@")
    private String mPreview = "no";

    private String memoString;
    private InputReader inputReader;
    private String isInfoCorrect;

    @Override
    public void run() {
        memoString = inputReader.prompt("Memo field");
        // Hedera hedera = new Hedera(context);
        var operatorId = hedera.getOperatorId();
        var client = hedera.createHederaClient();
        var recipientId = AccountId.fromString("0.0." + recipient);
        var amount = new BigInteger(recipientAmt);

        if ("no".equals(noPreview(mPreview))) {
            executeCryptoTransfer(client, operatorId, recipientId, amount);
        } else if ("yes".equals(noPreview(mPreview))) {
            isInfoCorrect = promptPreview(operatorId, recipientId, amount);
            if ("yes".equals(isInfoCorrect)) {
                shellHelper.print("Info is correct, let's go!");
                executeCryptoTransfer(client, operatorId, recipientId, amount);
            } else if ("no".equals(isInfoCorrect)) {
                shellHelper.print("Nope, incorrect, let's make some changes");
            } else {
                shellHelper.printError("Input must either been yes or no");
            }
        } else {
            shellHelper.printError("Error in commandline");
        }
    }

    private String promptPreview(AccountId operatorId, AccountId recipientId, BigInteger amount) {
        return inputReader.prompt("\nOperator: " + operatorId + "\nRecipient: " + recipientId + "\nAmount: " + amount
                + "\n\nIs this correct?" + "\nyes/no");
    }

    public void executeCryptoTransfer(Client client, AccountId operatorId, AccountId recipientId, BigInteger amount) {
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
                    .addSender(operatorId, amount.longValue()).addRecipient(recipientId, amount.longValue())
                    .setMemo(memoString)
                    .setTransactionId(transactionId)
                    // As we are sending from the operator we do not need to explicitly sign the
                    // transaction
                    .executeForReceipt();

            shellHelper.printInfo("transferring " + amount.longValue() + " tinybar...");
            var senderBalanceAfter = client.getAccountBalance(operatorId);
            var receiptBalanceAfter = client.getAccountBalance(recipientId);
            shellHelper.printSuccess("" + operatorId + " balance = " + senderBalanceAfter + "\n" + recipientId
                    + " balance = " + receiptBalanceAfter);
            shellHelper.printSuccess("Success!");
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    private String noPreview(String preview) {
        if ("no".equals(preview)) {
            mPreview = preview;
        } else if ("yes".equals(preview)) {
            mPreview = preview;
        } else {
            throw new CommandLine.ParameterException(spec.commandLine(), "Option -n removes preview");
        }
        return mPreview;
    }

}
