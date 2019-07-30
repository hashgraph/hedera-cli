package com.hedera.cli.hedera.crypto;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.Arrays;

@Command(name= "multiple",
        description = "@|fg(magenta) Transfer hbars to multiple accounts|@",
        helpCommand = true)
public class CryptoTransferMultiple implements Runnable {

    @Option(names = {"-r", "--recipient"}, split = " ", arity = "0..*",
            description = "Recipient to transfer to"+
            "%n@|bold,underline Usage:|@%n" +
            "@|fg(yellow) transfer multiple -r=1001,1002,-a=100,100 OR%n" +
            "transfer multiple --recipient=1001,1002,--recipientAmt=100,100|@")
    private String[] recipient;

    @Option(names = {"-a", "--recipientAmt"}, split = " ", arity = "0..*", description = "Amount to transfer")
    private String[] recipientAmt;

    @Override
    public void run() {
        System.out.println("TODO multiple transfer list");
        System.out.println(Arrays.asList(recipient));
        System.out.println(Arrays.asList(recipientAmt));
        System.out.println(recipient.length + " " + recipientAmt.length);
    }
}
