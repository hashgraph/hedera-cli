package com.hedera.cli.hedera.crypto;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.utils.AccountManager;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.account.AccountDeleteTransaction;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@Getter
@Setter
@Component
@Command(name = "delete", separator = " ", description = "@|fg(225) Deletes the given old account and transfers any balance to the given new account.|@")
public class AccountDelete implements Runnable, Operation {

    @Spec
    private CommandSpec spec;

    @Autowired
    private Hedera hedera;

    @Autowired
    private DataDirectory dataDirectory;

    @Autowired
    private AccountManager accountManager;

    @Autowired
    private ShellHelper shellHelper;

    @Option(names = {"-o",
            "--oldAccount"}, required = true, description = "Old account ID in %nshardNum.realmNum.accountNum format to be deleted."
            + "%n@|bold,underline Usage:|@%n" + "@|fg(yellow) account delete -o 0.0.1001 -n 0.0.1002|@")
    private String oldAccountInString;

    @Option(names = {"-n",
            "--newAccount"}, required = true, description = "Account ID in %nshardNum.realmNum.accountNum format,"
            + "%nwhere funds from old account are transferred to")
    private String newAccountInString;

    @Option(names = {"-y", "--yes"}, description = "Yes, skip preview")
    private boolean yes;

    private InputReader inputReader;
    private Ed25519PrivateKey oldAccountPrivKey;

    @Override
    public void run() {
        try {
            // Hedera hedera = new Hedera(context);
            var oldAccount = AccountId.fromString(oldAccountInString);
            var newAccount = AccountId.fromString(newAccountInString);

            String privKeyOfAccountToBeDeleted = inputReader
                    .prompt("Enter the private key of the account to be deleted", "secret", false);
            oldAccountPrivKey = Ed25519PrivateKey.fromString(privKeyOfAccountToBeDeleted);

            if (!yes) {
                boolean correctInfo = promptPreview(oldAccount, newAccount);
                if (correctInfo) {
                    shellHelper.print("Info is correct, let's go!");
                    executeAccountDelete(hedera, oldAccount, oldAccountPrivKey, newAccount);
                } else {
                    shellHelper.print("Nope, incorrect, let's make some changes");
                }
            } else {
                executeAccountDelete(hedera, oldAccount, oldAccountPrivKey, newAccount);
            }
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    private boolean promptPreview(AccountId oldAccountId, AccountId newAccount) {
        String choice = inputReader.prompt("\nAccount to be deleted: " + oldAccountId
                + "\nFunds from deleted account to be transferred to: " + newAccount + "\n\nIs this correct?"
                + "\nyes/no");
        return choice.equalsIgnoreCase("yes") || choice.equalsIgnoreCase("y");
    }

    private boolean checkIfOperatorKeyIsTheSameAsAccountToBeDeleted(Hedera hedera, Ed25519PrivateKey oldAccountPrivKey) {
        return hedera.getOperatorKey().toString().equals(oldAccountPrivKey.toString());
    }

    public void executeAccountDelete(Hedera hedera, AccountId oldAccount, Ed25519PrivateKey oldAccountPrivKey,
                                     AccountId newAccount) {
        var client = hedera.createHederaClient();
        TransactionId transactionId = new TransactionId(hedera.getOperatorId());
        try {

            boolean privateKeyDuplicate = checkIfOperatorKeyIsTheSameAsAccountToBeDeleted(hedera, oldAccountPrivKey);
            // account that is to be deleted must sign its own transaction
            if (privateKeyDuplicate) {
                // operator already sign transaction
                TransactionReceipt receipt = new AccountDeleteTransaction(client)
                        .setTransactionId(transactionId)
                        .setDeleteAccountId(oldAccount)
                        .setTransferAccountId(newAccount)
                        .executeForReceipt();
                receiptStatus(receipt, hedera, newAccount, oldAccount);
            } else {
                // sign the transaction
                TransactionReceipt receipt = new AccountDeleteTransaction(client)
                        .setTransactionId(transactionId)
                        .setDeleteAccountId(oldAccount)
                        .setTransferAccountId(newAccount)
                        .sign(oldAccountPrivKey)
                        .executeForReceipt();
                receiptStatus(receipt, hedera, newAccount, oldAccount);
            }

        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    private void receiptStatus(TransactionReceipt receipt, Hedera hedera, AccountId newAccount, AccountId oldAccount) {
        if (receipt.getStatus().toString().equals("SUCCESS")) {
            shellHelper.printSuccess(receipt.getStatus().toString());
            getBalance(hedera, newAccount);
            boolean fileDeleted = deleteJsonAccountFromDisk(oldAccount);
            shellHelper.printSuccess("File deleted from disk " + fileDeleted);
        } else {
            shellHelper.printError("Some error, account not deleted");
        }
    }

    public void getBalance(Hedera hedera, AccountId newAccount) {
        try {
            // Set a sleep to wait for hedera to come to consensus for the funds of deleted account
            // to be transferred to the new account
            Thread.sleep(4000);
            Client client = hedera.createHederaClient();
            client.setOperator(hedera.getOperatorId(), hedera.getOperatorKey());
            var newAccountBalance = client.getAccountBalance(newAccount);
            shellHelper.printSuccess("Account " + newAccount + " new balance is " + newAccountBalance);
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    public boolean deleteJsonAccountFromDisk(AccountId oldAccount) {
        String pathToIndexTxt = accountManager.pathToIndexTxt();
        boolean fileDeleted = false;

        String pathToCurrentJsonAccount;
        Map<String, String> updatedMap;

        Map<String, String> readingIndexAccount = dataDirectory.readIndexToHashmap(pathToIndexTxt);

        Set<Map.Entry<String, String>> setOfEntries = readingIndexAccount.entrySet();
        Iterator<Map.Entry<String, String>> iterator = setOfEntries.iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String accountId = entry.getKey(); // key refers to the account id
            String value = entry.getValue(); // value refers to the filename json

            if (accountId.equals(oldAccount.toString())) {
                // delete the associated json file in disk
                pathToCurrentJsonAccount = accountManager.pathToAccountsFolder() + value + ".json";
                Path filePathToJson = Paths.get(dataDirectory.getDataDir().toString(), pathToCurrentJsonAccount);
                File file = new File(filePathToJson.toString());
                fileDeleted = file.delete();
                iterator.remove();
            }
        }
        // write to file
        updatedMap = readingIndexAccount;
        dataDirectory.writeFile(pathToIndexTxt, dataDirectory.formatMapToIndex(updatedMap));
        return fileDeleted;
    }

    @Override
    public void executeSubCommand(InputReader inputReader, String... args) {
        this.inputReader = inputReader;
        if (args.length == 0) {
            CommandLine.usage(this, System.out);
        } else {
            try {
                new CommandLine(this).execute(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
