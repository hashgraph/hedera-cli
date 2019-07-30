package com.hedera.cli.hedera.file;

import com.hedera.cli.ExampleHelper;
import com.hedera.cli.hedera.utils.Utils;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.file.FileCreateTransaction;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.time.Instant;
import java.util.Arrays;

@Command(name = "create",
        header = "Creates a new file",
        description = "@|fg(magenta) Creates a new File and returns a FileID in the format of%n" +
                "shardNum.realmNum.fileNum|@")
public class FileCreate implements Runnable {

    @Option(names = {"-d", "--date"}, arity = "0..2",
            description = "Enter file expiry date in the format of%n"
                    + "dd-MM-yyyy hh:mm:ss%n"
                    + "%n@|bold,underline Usage:|@%n"
                    + "@|fg(yellow) -d=22-02-2019,21:30:58|@")
    private String[] date;

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
        System.out.println("File create " + Arrays.asList(date));
        try {
            var operatorKey = ExampleHelper.getOperatorKey();
            var client = ExampleHelper.createHederaClient();
            // The file is required to be a byte array,
            // you can easily use the bytes of a file instead.
            var fileContents = "Hedera hashgraph is great!".getBytes();
            FileCreateTransaction tx = null;
            System.out.println("Date parsed in ");
            Utils utils = new Utils();
            System.out.println(utils.dateToMilliseconds(date));
            Instant instant = utils.dateToMilliseconds(date);
            tx = new FileCreateTransaction(client)
                    .setExpirationTime(instant)
                    // Use the same key as the operator to "own" this file
                    .addKey(operatorKey.getPublicKey())
                    .setContents(fileContents);
            // This will wait for the receipt to become available
            TransactionReceipt receipt = tx.executeForReceipt();
            var newFileId = receipt.getFileId();
            System.out.println("file: " + newFileId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
