package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name = "account", description = "@|fg(225) Create, update, delete or querying an account by providing the <args>|@"
        + "%n@|fg(yellow) <command> <subcommand> <args>" + "%neg. account create <args>|@", subcommands = {
                AccountCreate.class, AccountUpdate.class, AccountGetInfo.class, AccountDefault.class,
                AccountDelete.class, AccountRecovery.class, AccountList.class, AccountUse.class, AccountBalance.class })
public class Account implements Runnable { 
    
    @Autowired
    private AccountOperationFactory accountOperationFactory;

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(InputReader inputReader, String subCommand, String... args) {
        if (!subCommand.isEmpty()) {
            Operation o = accountOperationFactory.getOperation(subCommand).orElseThrow();
            o.executeSubCommand(inputReader, args);
        } else {
            this.run();
        }

    }
}