package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Arrays;

@Component
@Command(name = "account", description = "@|fg(225) Create, update, delete or querying an account by providing the <args>|@"
        + "%n@|fg(yellow) <command> <subcommand> <args>" + "%neg. account create <args>|@", subcommands = {
                AccountCreate.class, AccountUpdate.class, AccountGetInfo.class, AccountDefault.class,
                AccountDelete.class, AccountRecovery.class, AccountList.class, AccountUse.class })
public class Account implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(ApplicationContext context, InputReader inputReader, String subCommand, String... args) {
        PicocliSpringFactory factory = new PicocliSpringFactory(context);

        // Check subcommand before parsing args
        switch (subCommand) {
        case "create":
            if (args.length == 0) {
                CommandLine.usage(new AccountCreate(), System.out);
            } else {
                try {
                    AccountCreate accountCreate = factory.create(AccountCreate.class);
                    System.out.println(Arrays.toString(args));
                    new CommandLine(accountCreate).execute(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
        case "update":
            if (args.length == 0) {
                CommandLine.usage(new AccountUpdate(), System.out);
            } else {
                try {
                    AccountUpdate accountUpdate = factory.create(AccountUpdate.class);
                    new CommandLine(accountUpdate).execute(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
        case "info":
            if (args.length == 0) {
                CommandLine.usage(new AccountGetInfo(), System.out);
            } else {
                try {
                    AccountGetInfo accountInfo = factory.create(AccountGetInfo.class);
                    new CommandLine(accountInfo).execute(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
        case "delete":
            if (args.length == 0) {
                CommandLine.usage(new AccountDelete(), System.out);
            } else {
                try {
                    AccountDelete accountDelete = factory.create(AccountDelete.class);
                    accountDelete.setInputReader(inputReader);
                    new CommandLine(accountDelete).execute(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
        case "recovery":
            if (args.length == 0) {
                CommandLine.usage(new AccountRecovery(), System.out);
            } else {
                try {
                    AccountRecovery accountRecovery = factory.create(AccountRecovery.class);
                    accountRecovery.setInputReader(inputReader);
                    new CommandLine(accountRecovery).execute(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
        case "ls":
            try {
                AccountList accountList = factory.create(AccountList.class);
                if (args.length == 0) {
                    new CommandLine(accountList).execute(args);
                } else {
                    new CommandLine(accountList).execute(args);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        case "use":
            if (args.length == 0) {
                CommandLine.usage(new AccountUse(), System.out);
            } else {
                try {
                    AccountUse accountUse = factory.create(AccountUse.class);
                    new CommandLine(accountUse).execute(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
        default:
            this.run();
            break;
        }
    }
}