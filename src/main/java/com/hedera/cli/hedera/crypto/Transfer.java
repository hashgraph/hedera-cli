package com.hedera.cli.hedera.crypto;

import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name= "transfer",
        description = "@|fg(magenta) Crypto transfer to single or multiple accounts|@ %n" +
        "@|fg(yellow) transfer single <args> OR transfer multiple <args>|@",
        subcommands = {CryptoTransfer.class, CryptoTransferMultiple.class})
public class Transfer implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(String subCommand, String... args) {
        switch (subCommand) {
            case "single":
                if (args.length == 0) {
                    CommandLine.usage(new CryptoTransfer(), System.out);
                } else {
                    new CommandLine(new CryptoTransfer()).execute(args);
                }
                break;
            case "multiple":
                if (args.length == 0) {
                    CommandLine.usage(new CryptoTransferMultiple(), System.out);
                } else {
                    new CommandLine(new CryptoTransferMultiple()).execute(args);
                }
                break;
            default:
                this.run();
                break;
        }
    }
}
