package com.hedera.cli.hedera.crypto;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.utils.AccountUtils;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.account.AccountDeleteTransaction;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@Getter
@Setter
@Component
@Command(name = "delete", separator = " ", description = "@|fg(225) Deletes the given old account and transfers any balance to the given new account.|@")
public class AccountDelete implements Runnable {

    @Spec
    CommandSpec spec;

    @Autowired
    ApplicationContext context;

    @Autowired
    ShellHelper shellHelper;

    @Option(names = { "-o",
            "--oldAccount" }, required = true, description = "Old account ID in %nshardNum.realmNum.accountNum format to be deleted."
                    + "%n@|bold,underline Usage:|@%n" + "@|fg(yellow) account delete -o 0.0.1001 -n 0.0.1002|@")
    private String oldAccountInString;

    @Option(names = { "-n",
            "--newAccount" }, required = true, description = "Account ID in %nshardNum.realmNum.accountNum format,"
                    + "%nwhere funds from old account are transferred to")
    private String newAccountInString;

    @Option(names = { "-y", "--yes" }, description = "Yes, skip preview")
    private boolean yes = false;

    private InputReader inputReader;
    private Ed25519PrivateKey oldAccountPrivKey;

    @Override
    public void run() {
        try {
            Hedera hedera = new Hedera(context);
            var oldAccount = AccountId.fromString(oldAccountInString);
            var newAccount = AccountId.fromString(newAccountInString);

            String privKeyOfAccountToBeDeleted = inputReader
                    .prompt("Enter the private key of the account to be deleted", "secret", false);
            oldAccountPrivKey = Ed25519PrivateKey.fromString(privKeyOfAccountToBeDeleted);

            if (yes == false) {
                yes = promptPreview(oldAccount, newAccount);
            }

            if (yes) {
                shellHelper.print("Info is correct, let's go!");
                boolean accountDeleted = executeAccountDelete(hedera, oldAccount, oldAccountPrivKey, newAccount);
                if (accountDeleted) {
                    getBalance(hedera, newAccount);
                    boolean fileDeleted = deleteJsonAccountFromDisk(oldAccount);
                    shellHelper.printSuccess("File deleted: " + fileDeleted);
                } else {
                    shellHelper.printError("Some error, account not deleted");
                }
            } else {
                shellHelper.print("Nope, incorrect, let's make some changes");
            }
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    private boolean promptPreview(AccountId oldAccountId, AccountId newAccount) {
        String choice = inputReader.prompt("\nAccount to be deleted: " + oldAccountId
                + "\nAccount for deleted account's funds to be transferred to: " + newAccount + "\n\nIs this correct?"
                + "\nyes/no");
        if (choice.toLowerCase().equals("yes") || choice.toLowerCase().equals("y")) {
            return true;
        }
        return false;
    }

    public boolean executeAccountDelete(Hedera hedera, AccountId oldAccount, Ed25519PrivateKey oldAccountPrivKey,
            AccountId newAccount) {
        boolean accountDeleted = false;
        var client = hedera.createHederaClient();
        // account that is to be deleted must sign its own transaction
        try {
            TransactionReceipt receipt = new AccountDeleteTransaction(client).setDeleteAccountId(oldAccount)
                    .setTransferAccountId(newAccount).sign(oldAccountPrivKey).executeForReceipt();
            shellHelper.printSuccess(receipt.getStatus().toString());
            accountDeleted = true;
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
        return accountDeleted;
    }

    public void getBalance(Hedera hedera, AccountId newAccount) {
        try {
            // Create a new client that is not associated with the old account
            Thread.sleep(5000);
            Client client = hedera.createHederaClient();
            client.setOperator(hedera.getOperatorId(), hedera.getOperatorKey());
            var newAccountBalance = client.getAccountBalance(newAccount);
            shellHelper.printSuccess("Account " + newAccount + " new balance is " + newAccountBalance);
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    public boolean deleteJsonAccountFromDisk(AccountId oldAccount) {
        DataDirectory dataDirectory = new DataDirectory();
        AccountUtils accountUtils = new AccountUtils();
        String pathToIndexTxt = accountUtils.pathToIndexTxt();
        boolean fileDeleted = false;

        String userHome = dataDirectory.getUserHome();
        String directoryName = dataDirectory.getDirectoryName();
        String pathToCurrentJsonAccount;
        Map<String, String> updatedMap;

        Map<String, String> readingIndexAccount = dataDirectory.readFileHashmap(pathToIndexTxt);

        Set<Map.Entry<String, String>> setOfEntries = readingIndexAccount.entrySet();
        Iterator<Map.Entry<String, String>> iterator = setOfEntries.iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String accountId = entry.getKey(); // key refers to the account id
            String value = entry.getValue(); // value refers to the filename json

            if (accountId.equals(oldAccount.toString())) {
                // delete the associated json file in disk
                pathToCurrentJsonAccount = accountUtils.pathToAccountsFolder() + value + ".json";
                Path filePathToJson = Paths.get(userHome, directoryName, pathToCurrentJsonAccount);
                File file = new File(filePathToJson.toString());
                fileDeleted = file.delete();
                iterator.remove();
            }
        }
        // write to file
        updatedMap = readingIndexAccount;
        dataDirectory.writeFile(pathToIndexTxt, updatedMap.toString());
        return fileDeleted;
    }
}
