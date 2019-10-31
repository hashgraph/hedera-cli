package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.DataDirectory;
import com.hedera.cli.shell.ShellHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name = "ls", description = "@|fg(225) List of all accounts for the current network.|@")
public class AccountList implements Runnable, Operation {

    @Autowired
    private DataDirectory dataDirectory;

    @Autowired
    private AccountManager accountManager;

    @Autowired
    private ShellHelper shellHelper;

    @Override
    public void run() {
        shellHelper.print("List of accounts in the current network");
        String pathToIndexTxt = accountManager.pathToIndexTxt();
        dataDirectory.readIndex(pathToIndexTxt);
    }

    @Override
    public void executeSubCommand(InputReader inputReader, String... args) {
        try {
            new CommandLine(this).execute(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}