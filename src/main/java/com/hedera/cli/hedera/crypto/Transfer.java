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

    // @Autowired
    // private CryptoTransfer cryptoTransfer;

    @ArgGroup(exclusive = false, multiplicity = "1")
    private CryptoTransferOptions o;

    @Autowired
    private KryptoKransfer kryptoKransfer;

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(String... args) {
        System.out.println(this);
        System.out.println(kryptoKransfer);
        for (String a : args) {
            System.out.println(a);
        }

        if (args.length == 0) {
            CommandLine.usage(this, System.out);
        } else {
            try {
                System.out.println(args);
                System.out.println(o);
                kryptoKransfer.handle(args);

                // System.out.println(o);
                // System.out.println("Dependent:");
                // System.out.println(o.dependent.senderList);
                // System.out.println(o.dependent.recipientList);
                // System.out.println(o.dependent.skipPreview);
        
                // System.out.println("Exclusive:");
                // System.out.println(o.exclusive.transferListAmtTinyBars);
                // System.out.println(o.exclusive.transferListAmtHBars);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
