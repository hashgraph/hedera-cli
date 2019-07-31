package com.hedera.cli.hedera.file;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.utils.Utils;
import com.hedera.hashgraph.sdk.file.FileCreateTransaction;
import com.hedera.hashgraph.sdk.file.FileDeleteTransaction;
import com.hedera.hashgraph.sdk.file.FileInfoQuery;
import com.hedera.hashgraph.sdk.proto.ResponseCodeEnum;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.time.Instant;

@Command(name = "delete",
        description = "@|fg(magenta) Deletes specified file from the Hedera network|@")
public class FileDelete implements Runnable {

    @Option(names = {"-d", "--date"}, arity = "0..2",
            description = "Enter date of file expiration in the format of%n" +
                    "dd-MM-yyyy hh:mm:ss%n" +
                    "%n@|bold,underline Usage:|@%n" +
                    "@|fg(yellow) file delete -d=11-01-2019,11:11:59|@")
    private String[] date;

    @Override
    public void run() {
        try {
            var operatorKey = Hedera.getOperatorKey();
            var client = Hedera.createHederaClient();
            var fileContents = "This is the file content for FileDelete.class".getBytes();
            Utils utils = new Utils();
            Instant instant = utils.dateToMilliseconds(date);
            var tx = new FileCreateTransaction(client)
                    .setExpirationTime(instant)
                    .addKey(operatorKey.getPublicKey())
                    .setContents(fileContents);
            var receipt = tx.executeForReceipt();
            var newFileId = receipt.getFileId();

            System.out.println("file: " + newFileId);

            // now to delete the file
            var txDeleteReceipt = new FileDeleteTransaction(client)
                    .setFileId(newFileId)
                    .executeForReceipt();

            if(txDeleteReceipt.getStatus() != ResponseCodeEnum.SUCCESS) {
                System.out.println("Error while deleting file");
                System.exit(1);
            }

            System.out.println("File deleted successfully");
            var fileInfo = new FileInfoQuery(client)
                    .setFileId(newFileId)
                    .execute();

            System.out.println("File info " + fileInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
