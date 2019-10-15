package com.hedera.cli.hedera.crypto;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.utils.AccountUtils;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.*;
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

    @Option(names = {"-o", "--oldAcc"}, required = true, description = "Old account ID in %nshardNum.realmNum.accountNum format to be deleted."
            + "%n@|bold,underline Usage:|@%n"
            + "@|fg(yellow) account delete -o=0.0.1001,-n=0.0.1002|@")
    private String oldAccountInString;

    @Option(names = {"-n", "--newAcc"}, required = true, description = "Account ID in %nshardNum.realmNum.accountNum format," +
            "%nwhere funds from old account are transferred to")
    private String newAccountInString;

    private InputReader inputReader;
    private Ed25519PrivateKey oldAccountPrivKey;
    private String isInfoCorrect;

    @Override
    public void run() {
        try {
            Hedera hedera = new Hedera(context);
            var oldAccount = AccountId.fromString(oldAccountInString);
            var newAccount = AccountId.fromString(newAccountInString);

            String privKeyOfAccountToBeDeleted = inputReader.prompt("Enter the private key of the account to be deleted", "secret", false);
            oldAccountPrivKey = Ed25519PrivateKey.fromString(privKeyOfAccountToBeDeleted);

            isInfoCorrect = promptPreview(oldAccount, newAccount);
            if (isInfoCorrect.equals("yes")) {
                shellHelper.print("Info is correct, let's go!");
                boolean accountDeleted = executeAccountDelete(hedera, oldAccount, oldAccountPrivKey, newAccount);
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
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    private String promptPreview(AccountId oldAccountId, AccountId newAccount) {
        return inputReader.prompt("\nAccount to be deleted: " + oldAccountId
                + "\nAccount for deleted account's funds to be transferred to: " + newAccount
                + "\n\nIs this correct?"
                + "\nyes/no");
    }

    public boolean executeAccountDelete(Hedera hedera, AccountId oldAccount, Ed25519PrivateKey oldAccountPrivKey, AccountId newAccount) {
        boolean accountDeleted = false;
        var client = hedera.createHederaClient();
        var operatorId = hedera.getOperatorId();
        var operatorKey = hedera.getOperatorKey();
        client.setOperator(operatorId, operatorKey);
        // account that is to be deleted must sign the transaction
        try {
            // if accountId for funds to be transferred is the same as the operatorId, only sign once
            if (newAccount.toString().equals(hedera.getOperatorId().toString())) {
                TransactionId txId = new AccountDeleteTransaction(client)
                        .setDeleteAccountId(oldAccount)
                        .setTransferAccountId(newAccount)
                        .execute();
                printTxId(txId);
                accountDeleted = true;
            } else {
                // if accountId for funds to be transferred is different from the the operatorId,
                // both accounts must sign
                AccountDeleteTransaction accountDeleteTransaction = new AccountDeleteTransaction(client)
                        .setDeleteAccountId(oldAccount)
                        .setTransferAccountId(newAccount);
                var signedTxnBytes = oldAccountSignsTransaction(client, oldAccountPrivKey, accountDeleteTransaction.toBytes());
                TransactionId txId = Transaction.fromBytes(client, signedTxnBytes)
                        .execute();
                printTxId(txId);
                accountDeleted = true;
            }
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
        return accountDeleted;
    }

    private void printTxId(TransactionId txId) {
        shellHelper.printInfo("Deleting old account... ");
        String txTimestamp = txId.getValidStart().getEpochSecond() + "-"
                + txId.getValidStart().getNano();
        shellHelper.printSuccess("TransactionId: " + txId.getAccountId().toString() + "-" + txTimestamp);
    }

    private byte[] oldAccountSignsTransaction(Client client, Ed25519PrivateKey oldAccountPrivKey, byte[] transactionData) throws InvalidProtocolBufferException {
        return Transaction.fromBytes(client, transactionData)
                .sign(oldAccountPrivKey)
                .toBytes();
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
