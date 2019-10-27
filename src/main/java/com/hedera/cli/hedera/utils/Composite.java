package com.hedera.cli.hedera.utils;

import org.springframework.stereotype.Component;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Option;

@Component
public class Composite {

    // Mutually exclusive group, ie, either input tinybars or hbars as arguments
    @ArgGroup(exclusive = true, multiplicity = "1")
    public Exclusive exclusive;

    // Dependent group, ie, all arguments must exist
    @ArgGroup(exclusive = false, multiplicity = "0..1")
    public Dependent dependent;

    public static class Exclusive {
        @Option(names = {"-tb", "--recipientAmtTinyBars"}, required = true, description = "Amount to transfer in tinybars")
        public String recipientAmtTinyBars;

        @Option(names = {"-hb", "--recipientAmtHBars"}, required = true, description = "Amount to transfer in hbars")
        public String recipientAmtHBars;
    }

    public static class Dependent {

        @Option(names = { "-a",
                "--accountId" }, arity = "1", required = true, description = "Recipient's accountID in the format shardNum.realmNum.accountNum")
        public String recipient;

        @Option(names = { "-n",
                "--noPreview" }, arity = "0..1", defaultValue = "yes", fallbackValue = "no", description = "Cryptotransfer preview"
                + "\noption with optional parameter. Default: ${DEFAULT-VALUE},\n"
                + "if specified without parameter: ${FALLBACK-VALUE}" + "%n@|bold,underline Usage:|@%n"
                + "@|fg(yellow) transfer single -a 0.0.1234 -r 100|@")
        public String mPreview = "no";
    }
}
