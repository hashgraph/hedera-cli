package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name = "transfer",
        description = "@|fg(225) Crypto transfer to single or multiple accounts|@"
                + "%n@|fg(yellow) transfer single <args> OR"
                + "%ntransfer multiple <args>|@",
        subcommands = {CryptoTransfer.class, CryptoTransferMultiple.class})
public class Transfer implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(InputReader inputReader, String subCommand, String... args) {
        switch (subCommand) {
            case "single":
                if (args.length == 0) {
                    CommandLine.usage(new CryptoTransfer(inputReader), System.out);
                } else {
                    new CommandLine(new CryptoTransfer(inputReader)).execute(args);
                }
                break;
            case "multiple":
                if (args.length == 0) {
                    CommandLine.usage(new CryptoTransferMultiple(inputReader), System.out);
                } else {
                    new CommandLine(new CryptoTransferMultiple(inputReader)).execute(args);
                }
                break;
            default:
                this.run();
                break;
        }
    }
}
