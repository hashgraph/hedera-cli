package com.hedera.cli.hedera.crypto;

import java.util.Arrays;

import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name = "account",
//        synopsisHeading = "%n",
//        headerHeading = "@|bold,underline Usage:|@%n%n",
//        header = "Crypto API",
//        descriptionHeading = "%n@|bold,underline Description:|@%n%n",
        description = "@|fg(magenta) Create, update, delete or querying an account by providing the <args>|@"
                + "%n@|fg(yellow) account create <args> OR"
                + "%naccount update <args> OR"
                + "%naccount delete <args> OR|@",
//        parameterListHeading = "%n@|bold,underline Parameters:|@%n",
//        optionListHeading = "%n@|bold,underline Options:|@%n",
        subcommands = {AccountCreate.class,
                AccountUpdate.class,
                AccountInfo.class})
public class Account implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(String subCommand, String... args) {
        System.out.println("args parsed in account");
        System.out.println(subCommand + " " + Arrays.asList(args));
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
            default:
                this.run();
                break;
        }
    }
}