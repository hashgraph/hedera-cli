package com.hedera.cli.hedera.crypto;

import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name = "account", description = "@|fg(magenta) Create, update, delete or querying an account by providing the <args>|@"
        + "%n@|fg(yellow) <command> <subcommand> <args>" + "%neg. account create <args>|@", subcommands = {
                AccountCreate.class, AccountUpdate.class, AccountInfo.class, AccountDefault.class, AccountDelete.class,
                AccountRecovery.class })
public class Account implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(String subCommand, String... args) {
        // Check subcommand before parsing args
        switch (subCommand) {
        case "create":
            if (args.length == 0) {
                CommandLine.usage(new AccountCreate(), System.out);
            } else {
                new CommandLine(new AccountCreate()).execute(args);
            }
            break;
        case "update":
            if (args.length == 0) {
                CommandLine.usage(new AccountUpdate(), System.out);
            } else {
                new CommandLine(new AccountUpdate()).execute(args);
            }
            break;
        case "info":
            if (args.length == 0) {
                CommandLine.usage(new AccountInfo(), System.out);
            } else {
                new CommandLine(new AccountInfo()).execute(args);
            }
            break;
        case "delete":
            if (args.length == 0) {
                CommandLine.usage(new AccountDelete(), System.out);
            } else {
                new CommandLine(new AccountDelete()).execute(args);
            }
            break;
        case "recover":
            if (args.length == 0) {
                CommandLine.usage(new AccountRecovery(), System.out);
            } else {
                new CommandLine(new AccountRecovery()).execute(args);
            }
            break;
        default:
            this.run();
            break;
        }
    }
}