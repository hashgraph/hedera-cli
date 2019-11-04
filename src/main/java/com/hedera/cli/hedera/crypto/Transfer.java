package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name = "transfer", description = "@|fg(225) Crypto transfer to single or multiple accounts|@"
        + "%n@|fg(yellow) transfer -s 0.0.1001,0.0.1002 -r 0.0.1003,0.0.1004 -tb -1000,-1000,1000,1000|@"
        + "%ntransfer -s 0.0.1001,0.0.1002 -r 0.0.1003,0.0.1004 -hb -0.1,-100,1.20,100.999978|@")
public class Transfer implements Runnable {

    @Autowired
    private CryptoTransfer cryptoTransfer;

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(InputReader inputReader, String... args) {
        cryptoTransfer.setInputReader(inputReader);
        if (args.length == 0) {
            CommandLine.usage(cryptoTransfer, System.out);
        } else {
            try {
                new CommandLine(cryptoTransfer).execute(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
