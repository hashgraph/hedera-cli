package com.hedera.cli.hedera.crypto;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

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
    }
}
