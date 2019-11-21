package com.hedera.cli.hedera.crypto;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Option;

@Getter
@Setter
@Component
public class CryptoTransferOptions {

    // Mutually exclusive group, ie, either input tinybars or hbars as arguments
    @ArgGroup(exclusive = true, multiplicity = "1")
    public Exclusive exclusive;

    // Dependent group, ie, all arguments must exist
    @ArgGroup(exclusive = false, multiplicity = "0..1")
    public Dependent dependent;

    @Getter
    @Setter
    public static class Exclusive {
        @Option(names = {"-tb", "--recipientAmtTinyBars"}, description = "Amount to transfer in tinybars"
                + "%n@|bold,underline Usage:|@%n"
                + "@|fg(yellow) transfer -s 0.0.1001 -r 0.0.1002 -tb 100|@"
                + "%n@|fg(yellow) transfer -s 0.0.1001 -r 0.0.1002 -hb 0.1|@")
        public String transferListAmtTinyBars;

        @Option(names = {"-hb", "--recipientAmtHBars"}, arity = "1..*", description = "Amount to transfer in hbars")
        public String transferListAmtHBars;
    }

    @Getter
    @Setter
    public static class Dependent {

        @Option(names = { "-s", "--sender" },  required = true,
                description = "PreviewTransferList accountID in the format shardNum.realmNum.accountNum")
        public String senderList;

        @Option(names = { "-r", "--recipient" },  required = true,
                description = "Recipient accountID in the format shardNum.realmNum.accountNum")
        public String recipientList;

        @Option(names = {"-y", "--yes"}, description = "Yes, skip preview")
        public boolean skipPreview;
    }
}
