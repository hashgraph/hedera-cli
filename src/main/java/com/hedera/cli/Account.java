package com.hedera.cli;

import com.hedera.cli.hedera.CreateAccount;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParseResult;

import com.hedera.cli.hedera.CryptoCreate;


@Component
@Command(name = "account", subcommands = {CryptoCreate.class, CreateAccount.class})
public class Account implements Runnable {

    private static void handleParseResult(ParseResult parsed) {
        assert parsed.subcommand() != null : "at least 1 command and 1 subcommand found";
    
        ParseResult sub = parsed.subcommand();
        assert parsed.commandSpec().userObject().getClass() == Account.class       : "main command";
        assert    sub.commandSpec().userObject().getClass() == CryptoCreate.class : "subcommand";
    }

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void subCommand(String... args) {
        CommandLine commandLine = new CommandLine(new Account());
        commandLine.execute(args);
    }
}