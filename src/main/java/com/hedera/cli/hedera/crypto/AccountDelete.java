package com.hedera.cli.hedera.crypto;

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
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Getter
@Setter
@Component
@Command(name = "delete",
        description = "@|fg(225) Gets the information of a specific account." +
                "%nRequires key for account modification" +
                "%nreturns a stateproof if requested|@")
public class AccountDelete implements Runnable {

    @Spec
    CommandSpec spec;

    @Autowired
    ApplicationContext context;

    @Autowired
    ShellHelper shellHelper;

    @Option(names = {"-o", "--oldAcc"}, description = "Old account ID to be deleted."
            + "%n@|bold,underline Usage:|@%n"
            + "@|fg(yellow) account delete -o=1001,-n=1002|@")
    private String oldAccountInString;

    @Option(names = {"-n", "--newAcc"}, description = "Account ID where funds from old account" +
            "%nare to be transferred over to")
    private String newAccountInString;

    private InputReader inputReader;
    private Ed25519PrivateKey oldAccountPrivKey;
    private String isInfoCorrect;

    @Override
    public void run() {

        Hedera hedera = new Hedera(context);
        var client = hedera.createHederaClient();
        var oldAccount = AccountId.fromString("0.0." + oldAccountInString);
        var newAccount = AccountId.fromString("0.0." + newAccountInString);

        String privKeyOfAccountToBeDeleted = inputReader.prompt("Enter the private key of the account to be deleted", "secret", false);
        oldAccountPrivKey = Ed25519PrivateKey.fromString(privKeyOfAccountToBeDeleted);

        isInfoCorrect = promptPreview(oldAccount, newAccount);
        if (isInfoCorrect.equals("yes")) {
            shellHelper.print("Info is correct, let's go!");
            boolean accountDeleted = executeAccountDelete(client, oldAccount, oldAccountPrivKey, newAccount);
            if (accountDeleted) {
                getReceiptWithOperator(hedera, newAccount);
                shellHelper.print("after get receipt : ");
                boolean fileDeleted = deleteJsonAccountFromDisk(oldAccount);
                shellHelper.printSuccess("File deleted: " + fileDeleted);
            } else {
                shellHelper.printError("Some error, account not deleted");
            }
        } else if (isInfoCorrect.equals("no")) {
            shellHelper.print("Nope, incorrect, let's make some changes");
        } else {
            shellHelper.printError("Input must either been yes or no");
        }
    }

    private String promptPreview(AccountId oldAccountId, AccountId newAccount) {
        return inputReader.prompt("\nAccount to be deleted: " + oldAccountId
                + "\nAccount for deleted account's funds to be transferred to: " + newAccount
                + "\n\nIs this correct?"
                + "\nyes/no");
    }

    public boolean executeAccountDelete(Client client, AccountId oldAccount, Ed25519PrivateKey oldAccountPrivKey, AccountId newAccount) {
        boolean accountDeleted = false;
        try {
            new AccountDeleteTransaction(client.setOperator(oldAccount, oldAccountPrivKey))
                    .setDeleteAccountId(oldAccount)
                    .setTransferAccountId(newAccount)
                    .execute();
            shellHelper.printInfo("deleting old account... ");
            accountDeleted = true;
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
        return accountDeleted;
    }

    public void getReceiptWithOperator(Hedera hedera, AccountId newAccount) {
        try {
            // Create a new client that is not associated with the old account
            Thread.sleep(5000);
            Client client = hedera.createHederaClient();
            client.setOperator(hedera.getOperatorId(), hedera.getOperatorKey());
            shellHelper.print(hedera.getOperatorId().toString());
            var newAccountBalance = client.getAccountBalance(newAccount);
            shellHelper.printSuccess("Account " + newAccount + " new balance is " + newAccountBalance);
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    public boolean deleteJsonAccountFromDisk(AccountId oldAccount) {
        DataDirectory dataDirectory = new DataDirectory();
        AccountUtils accountUtils = new AccountUtils();
        String pathToIndexTxt = accountUtils.pathToAccountsFolder() + "index.txt";
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
