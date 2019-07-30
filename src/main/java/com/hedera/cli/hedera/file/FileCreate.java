package com.hedera.cli.hedera.file;

import com.hedera.cli.ExampleHelper;
import com.hedera.cli.hedera.utils.Utils;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.file.FileCreateTransaction;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Command(name = "create",
        header = "Creates a new file",
        description = "Creates a new File and returns a FileID in the format of" +
                "shardNum.realmNum.fileNum")
public class FileCreate implements Runnable {

    @Option(names = {"-d", "--date"}, description = "Enter file expiry date in the format of"
            + "dd-MM-yyyy hh:mm:ss for example 22-02-2019 21:30:58")
    private Date date;

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
        try {
            var operatorKey = ExampleHelper.getOperatorKey();
            var client = ExampleHelper.createHederaClient();
            // The file is required to be a byte array,
            // you can easily use the bytes of a file instead.
            System.out.println(date);
            var fileContents = "Hedera hashgraph is great!".getBytes();
            FileCreateTransaction tx = null;
            System.out.println("Date parsed in ");
            Utils utils = new Utils();
            System.out.println(utils.DateToMilliSeconds(date));
            tx = new FileCreateTransaction(client).setExpirationTime(
                    Instant.now()
                            .plus(Duration.ofSeconds(utils.DateToMilliSeconds(date))))
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
