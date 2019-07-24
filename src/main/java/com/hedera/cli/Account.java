package com.hedera.cli;

import com.hedera.cli.hedera.CreateAccount;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import com.hedera.cli.hedera.CryptoCreate;


@Component
@Command(name = "account", subcommands = {CryptoCreate.class, CreateAccount.class})
public class Account implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void subCommand(String... args) {
        CommandLine commandLine = new CommandLine(new Account());
        commandLine.execute(args);
    }
}