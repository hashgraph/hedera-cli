package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name = "transfer", description = "@|fg(225) Crypto transfer to single or multiple accounts|@"
        + "%n@|fg(yellow) transfer single <args> OR"
        + "%ntransfer multiple <args>|@", subcommands = { CryptoTransfer.class, CryptoTransferMultiple.class })
public class Transfer implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(ApplicationContext context, InputReader inputReader, String subCommand, String... args)
            throws Exception {
        
        // use factory to inject context and manufacture our specific sub command classes
        PicocliSpringFactory factory = new PicocliSpringFactory(context);
        CryptoTransfer cryptoTransfer = factory.create(CryptoTransfer.class);
        cryptoTransfer.setInputReader(inputReader);
        CryptoTransferMultiple cryptoTransferMultiple = factory.create(CryptoTransferMultiple.class);
        cryptoTransferMultiple.setInputReader(inputReader);

        switch (subCommand) {
        case "single":
            if (args.length == 0) {
                CommandLine.usage(cryptoTransfer, System.out);
            } else {
                try {
                    new CommandLine(cryptoTransfer).execute(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
        case "multiple":
            if (args.length == 0) {
                CommandLine.usage(cryptoTransferMultiple, System.out);
            } else {
                try {
                    new CommandLine(cryptoTransferMultiple).execute(args);
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
