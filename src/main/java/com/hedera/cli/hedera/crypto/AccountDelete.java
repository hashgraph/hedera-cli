package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
// import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.account.AccountDeleteTransaction;
import com.hedera.hashgraph.sdk.account.AccountId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(name = "delete",
        description = "@|fg(225) Gets the information of a specific account." +
                "%nRequires key for account modification" +
                "%nreturns a stateproof if requested|@")
public class AccountDelete implements Runnable {

    @Option(names={"-o", "--oldAcc"}, description = "Old account ID to be deleted."
            + "%n@|bold,underline Usage:|@%n"
            + "@|fg(yellow) account delete -o=1001,-n=1002|@")
    private String oldAccountInString;

    @Option(names={"-n", "--newAcc"}, description = "Account ID where funds from old account"+
            "%nare to be transferred over to")
    private String newAccountInString;

    @Autowired
    ApplicationContext context;

    @Override
    public void run() {
        try {
            System.out.println("AccountDelete commands");
            Hedera hedera = new Hedera(context);
            var client = hedera.createHederaClient();
            var oldAccount = AccountId.fromString("0.0." + oldAccountInString);
            var newAccount = AccountId.fromString("0.0." + newAccountInString);
            AccountDeleteTransaction tx = null;
            tx = new AccountDeleteTransaction(client)
                    .setDeleteAccountId(oldAccount)
                    .setTransferAccountId(newAccount);

            System.out.println("deleting old account... ");
            System.out.println("transferring to new account... ");
            // TransactionReceipt receipt = tx.executeForReceipt();
            var record = tx.executeForRecord();
            System.out.println(record.getTransfers());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
