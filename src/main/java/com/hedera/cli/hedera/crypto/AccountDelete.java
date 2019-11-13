package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.DataDirectory;
import com.hedera.cli.services.HederaGrpc;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Getter
@Setter
@Component
@Command(name = "delete", separator = " ", description = "@|fg(225) Deletes the given old account and transfers any balance to the given new account.|@")
public class AccountDelete implements Runnable, Operation {

    @Autowired
    private Hedera hedera;

    @Autowired
    private DataDirectory dataDirectory;

    @Autowired
    private AccountManager accountManager;

    @Autowired
    private InputReader inputReader;

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private HederaGrpc hederaGrpc;

    @Option(names = { "-o",
            "--oldAccount" }, required = true, description = "Old account ID in %nshardNum.realmNum.accountNum format to be deleted."
                    + "%n@|bold,underline Usage:|@%n" + "@|fg(yellow) account delete -o 0.0.1001 -n 0.0.1002|@")
    private String oldAccountInString;

    @Option(names = { "-n",
            "--newAccount" }, required = true, description = "Account ID in %nshardNum.realmNum.accountNum format,"
                    + "%nwhere funds from old account are transferred to")
    private String newAccountInString;

    @Option(names = { "-y", "--yes" }, description = "Yes, skip preview")
    private boolean skipPreview;

    private Ed25519PrivateKey oldAccountPrivKey;

    @Override
    public void run() {
        AccountId oldAccount;
        AccountId newAccount;
        try {
            oldAccount = AccountId.fromString(oldAccountInString);
            newAccount = AccountId.fromString(newAccountInString);
        } catch (Exception e) {
            shellHelper.printError("Invalid account id provided");
            return;
        }

        String oldAccountPrivateKey = inputReader.prompt("Enter the private key of the account to be deleted", "secret",
                false);
        try {
            oldAccountPrivKey = Ed25519PrivateKey.fromString(oldAccountPrivateKey);
        } catch (Exception e) {
            shellHelper.printError("Private key is not in the right ED25519 string format");
            return;
        }

        if (skipPreview) {
            hederaGrpc.executeAccountDelete(oldAccount, oldAccountPrivKey, newAccount);
            return;
        }

        boolean correctInfo = promptPreview(oldAccount, newAccount);
        if (correctInfo) {
            shellHelper.print("Info is correct, let's go!");
            hederaGrpc.executeAccountDelete(oldAccount, oldAccountPrivKey, newAccount);
            return;
        } else {
            shellHelper.printError("Nope, incorrect, let's make some changes");
        }
    }

    private boolean promptPreview(AccountId oldAccountId, AccountId newAccount) {
        String choice = inputReader.prompt(
                "\nAccount to be deleted: " + oldAccountId + "\nFunds from deleted account to be transferred to: "
                        + newAccount + "\n\nIs this correct?" + "\nyes/no");
        return choice.equalsIgnoreCase("yes") || choice.equalsIgnoreCase("y");
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
