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
        description = "Create, update, delete an account by providing the <args>",
//        parameterListHeading = "%n@|bold,underline Parameters:|@%n",
//        optionListHeading = "%n@|bold,underline Options:|@%n",
        subcommands = {CryptoCreate.class, CryptoTransfer.class})
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
                    CommandLine.usage(new CryptoCreate(), System.out);
                } else {
                    new CommandLine(new CryptoCreate()).execute(args);
                }
                break;
            default:
                this.run();
                break;
        }
    }
}