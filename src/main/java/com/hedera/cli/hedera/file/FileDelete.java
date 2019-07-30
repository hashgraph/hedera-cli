package com.hedera.cli.hedera.file;

import com.hedera.cli.ExampleHelper;
import com.hedera.cli.hedera.utils.Utils;
import com.hedera.hashgraph.sdk.file.FileCreateTransaction;
import com.hedera.hashgraph.sdk.file.FileDeleteTransaction;
import com.hedera.hashgraph.sdk.file.FileInfoQuery;
import com.hedera.hashgraph.sdk.proto.ResponseCodeEnum;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Command(name = "delete", header = "Delete specified file from the Hedera network",
description = "Deletes the file with FileID in the format of" +
"shardNum.realmNum.fileNum")
public class FileDelete implements Runnable {

    @Option(names = {"-d", "--date"},
            description = "Enter date of file expiration in the format of"
    + "dd-MM-yyyy hh:mm:ss for example 11-01-2019 11:11:59")
    private Date date;

    @Override
    public void run() {
        try {
            var operatorKey = ExampleHelper.getOperatorKey();
            var client = ExampleHelper.createHederaClient();
            var fileContents = "This is the file content for FileDelete.class".getBytes();
            Utils utils = new Utils();
            var tx = new FileCreateTransaction(client).setExpirationTime(
                    Instant.now()
                            .plus(Duration.ofSeconds(utils.DateToMilliSeconds(date))))
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
