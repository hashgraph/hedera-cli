package com.hedera.cli.hedera.file;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.utils.Utils;
import com.hedera.hashgraph.sdk.file.FileCreateTransaction;
import com.hedera.hashgraph.sdk.file.FileDeleteTransaction;
import com.hedera.hashgraph.sdk.file.FileId;
import com.hedera.hashgraph.sdk.file.FileInfoQuery;
import com.hedera.hashgraph.sdk.proto.ResponseCodeEnum;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.time.Instant;

@Command(name = "delete",
        description = "@|fg(225) Deletes specified file from the Hedera network|@")
public class FileDelete implements Runnable {

    @Option(names = {"-f", "--fileID"},
            description = "@|fg(225) Enter the file ID of the file to be deleted,in the format of"
                    + "%nshardNum.realmNum.fileNum|@")
    private String fileNumInString;

    @Override
    public void run() {
        try {
            Hedera hedera = new Hedera();
            var client = hedera.createHederaClient();
            FileId fileId = FileId.fromString(fileNumInString);
            System.out.println("file: " + fileId);

            // now to delete the file
            var txDeleteReceipt = new FileDeleteTransaction(client)
                    .setFileId(fileId)
                    .executeForReceipt();

            if(txDeleteReceipt.getStatus() != ResponseCodeEnum.SUCCESS) {
                System.out.println("Error while deleting file");
                System.exit(1);
            }

            System.out.println("File deleted successfully");
            var fileInfo = new FileInfoQuery(client)
                    .setFileId(fileId)
                    .execute();

            System.out.println("File info " + fileInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
