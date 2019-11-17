package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;

@Component
@Command(name = "transfer", description = "@|fg(225) Crypto transfer to single or multiple accounts|@"
        + "%n@|fg(yellow) transfer -s 0.0.1001,0.0.1002 -r 0.0.1003,0.0.1004 -tb -1000,-1000,1000,1000|@"
        + "%ntransfer -s 0.0.1001,0.0.1002 -r 0.0.1003,0.0.1004 -hb -0.1,-100,1.20,100.999978|@")
public class Transfer implements Runnable {

    @Autowired
    private InputReader inputReader;

    @ArgGroup(exclusive = false, multiplicity = "1")
    private CryptoTransferOptions o;

    @Autowired
    private KryptoKransfer kryptoKransfer;

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(String... args) {
        if (args.length == 0) {
            CommandLine.usage(this, System.out);
        } else {
            try {
                kryptoKransfer.handle(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
