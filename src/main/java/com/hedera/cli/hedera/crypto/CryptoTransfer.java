package com.hedera.cli.hedera.crypto;

import java.math.BigInteger;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

@NoArgsConstructor
@Setter
@Component
@Command(name = "single",
        description = "@|fg(225) Transfer hbars to a single account|@%n",
        helpCommand = true)
public class CryptoTransfer implements Runnable {

    @Autowired
    ApplicationContext context;

    @Spec
    CommandSpec spec;

    @Option(names = {"-a", "--accountId"}, arity = "1", description = "Recipient's accountID to transfer to, shardNum and realmNum not needed"
            + "%n@|bold,underline Usage:|@%n"
            + "@|fg(yellow) transfer single -a=1234,-r=100|@")
    private String recipient;

    @Option(names = {"-r", "--recipientAmt"}, arity = "1", description = "Amount to transfer in tinybars")
    private String recipientAmt;

    @Option(names = {"-n", "--noPreview"}, arity = "0..1",
            defaultValue = "yes",
            fallbackValue = "no",
            description = "Cryptotransfer preview" +
                    "\noption with optional parameter. Default: ${DEFAULT-VALUE},\n" +
            "if specified without parameter: ${FALLBACK-VALUE}")
    private String mPreview = "no";

    private String noPreview(String preview) {
        if (preview.equals("no")) {
            mPreview = preview;
        } else if (preview.equals("yes")) {
            mPreview = preview;
        } else {
            throw new CommandLine.ParameterException(spec.commandLine(), "Option -y removes preview");
        }
        return mPreview;
    }

    private String memoString;
    private InputReader inputReader;
    private String isInfoCorrect;

    @Override
    public void run() {
        memoString = inputReader.prompt("Memo field");
        Hedera hedera = new Hedera(context);
        var operatorId = hedera.getOperatorId();
        var client = hedera.createHederaClient();
        var recipientId = AccountId.fromString("0.0." + recipient);
        var amount = new BigInteger(recipientAmt);

        if (noPreview(mPreview).equals("no")) {
            executeCryptoTransfer(client, operatorId, recipientId, amount);
        } else if (noPreview(mPreview).equals("yes")) {
            isInfoCorrect = promptPreview(operatorId, recipientId, amount);
            if (isInfoCorrect.equals("yes")) {
                System.out.println("Info is correct, let's go!");
                executeCryptoTransfer(client, operatorId, recipientId, amount);
            } else if (isInfoCorrect.equals("no")) {
                System.out.println("Nope, incorrect, let's make some changes");
            } else {
                throw new ParameterException(spec.commandLine(), "Input must either been yes or no");
            }
        } else {
            throw new CommandLine.ParameterException(spec.commandLine(), "Error in commandline");
        }
    }

    private String promptPreview(AccountId operatorId, AccountId recipientId, BigInteger amount) {
        return inputReader.prompt("\nOperator: " + operatorId
                + "\nRecipient: " + recipientId + "\nAmount: " + amount
                + "\n\nIs this correct?"
                + " \nyes/no");
    }

    public void executeCryptoTransfer(Client client, AccountId operatorId, AccountId recipientId, BigInteger amount) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

