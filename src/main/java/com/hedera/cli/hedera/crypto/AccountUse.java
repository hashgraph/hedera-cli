package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.utils.DataDirectory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

import java.io.File;

@Command(name = "use", description = "@|fg(225) Allows to toggle between multiple Hedera Accounts|@", helpCommand = true)
public class AccountUse implements Runnable {

    @Spec
    CommandSpec spec;

    @Option(names = { "-a", "--account-id" }, description = "Account ID in %nshardNum.realmNum.accountNum format")
    private String accountId;


    @Override
    public void run() {
        switchAccounts();
    }

    public void switchAccounts() {
        System.out.println("To be implemented");
        DataDirectory dataDirectory = new DataDirectory();
        String networkName = dataDirectory.readFile("network.txt");
        String pathToAccountsFolder = networkName + File.separator + "accounts" + File.separator;
        String pathToDefaultTxt = pathToAccountsFolder +  "default.txt";


    }
}
