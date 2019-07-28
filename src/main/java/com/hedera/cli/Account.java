package com.hedera.cli;

import java.util.Arrays;

import com.hedera.cli.hedera.CryptoTransfer;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import com.hedera.cli.hedera.CryptoCreate;

@Component
@Command(name = "account",
        sortOptions = false,
        headerHeading = "@|bold,underline Usage:|@%n%n",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description:|@%n%n",
        parameterListHeading = "%n@|bold,underline Parameters:|@%n",
        optionListHeading = "%n@|bold,underline Options:|@%n",
        header = "Create, update, delete account.",
        description = "Crypto APIs",
        subcommands = {CryptoCreate.class, CryptoTransfer.class})
public class Account implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

//    private CommandLine addSubCommands() {
//        // programmatically managing our sub-commands
//        CommandLine cmd = new CommandLine(new Account());
//        cmd.addSubcommand("create", new CryptoCreate());
//        cmd.addSubcommand("transfer", new CryptoTransfer());
//        return cmd;
//    }

    public void handle(String subCommand, String... args) {

        System.out.println("handle");
        System.out.println(subCommand);
        System.out.println(Arrays.asList(args));
//        CommandLine cmd = this.addSubCommands();
        // ParseResult parsed = cmd.parseArgs(args);

        // Check subcommand before parsing args
        switch (subCommand) {
            case "create":
                new CommandLine(new CryptoCreate()).execute(args);
                break;
            case "transfer":
                new CommandLine(new CryptoTransfer()).execute(args);
                break;
            default:
                this.run();
                break;
        }
        // scenario 1: if no args are provided
        // scenario 2: if 1 arg is provided
        // scenario 3: if 2 args are provided and the first arg is a subcommand
        // scenario 4: if 2 args are provided and the first arg is NOT a subcommand

        // // 3 and 4
        // if (args.length == 2) {
        //     System.out.println("2 args. Let's check to see if it is a valid subcommand");
        //     System.out.println(args[0]);
        //     System.out.println(args[1]);
        //     ParseResult parsed = cmd.parseArgs(args);
        //     System.out.println(parsed);
        // }

    }
}