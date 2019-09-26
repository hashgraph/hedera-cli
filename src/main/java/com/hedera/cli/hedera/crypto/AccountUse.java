package com.hedera.cli.hedera.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.models.AddressBook;
import com.hedera.hashgraph.sdk.account.AccountId;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Command(name = "use", description = "@|fg(225) Allows to toggle between multiple Hedera Accounts|@", helpCommand = true)
public class AccountUse implements Runnable {

    @Spec
    CommandSpec spec;

    @Option(names = {"-a", "--account-id"}, description = "Account ID in %nshardNum.realmNum.accountNum format")
    private String accountId;


    @Override
    public void run() {
        switchAccount();
    }

    public void switchAccount() {
        DataDirectory dataDirectory = new DataDirectory();
        String networkName = dataDirectory.readFile("network.txt");
        String pathToAccountsFolder = networkName + File.separator + "accounts" + File.separator;
        String pathToDefaultTxt = pathToAccountsFolder + "default.txt";
        String pathToIndexTxt = pathToAccountsFolder + "index.txt";

        String readingDefaultAccount = dataDirectory.readFile(pathToDefaultTxt);
        System.out.println(readingDefaultAccount);
        String defaultAccountFilename = readingDefaultAccount.split(":")[0];
        String defaultAccountID = readingDefaultAccount.split(":")[1];
        HashMap readingIndexAccount = dataDirectory.readFileHashmap(pathToIndexTxt);

        for (Object key : readingIndexAccount.keySet()) {
            System.out.println(key.toString());
            if (key.toString().equals(defaultAccountID)) {
                System.out.println("Here is where we want to make the changes");
            }
        }

    }
}
