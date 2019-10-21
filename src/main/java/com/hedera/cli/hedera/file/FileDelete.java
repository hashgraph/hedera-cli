package com.hedera.cli.hedera.file;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.file.FileDeleteTransaction;
import com.hedera.hashgraph.sdk.file.FileId;
import com.hedera.hashgraph.sdk.file.FileInfoQuery;
import com.hederahashgraph.api.proto.java.ResponseCodeEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(name = "delete",
        description = "@|fg(225) Deletes specified file from the Hedera network|@")
public class FileDelete implements Runnable {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    @Option(names = {"-f", "--fileID"},
            description = "@|fg(225) Enter the file ID of the file to be deleted,in the format of"
                    + "%nshardNum.realmNum.fileNum|@")
    private String fileNumInString;

    @Override
    public void run() {
        try {
            // Hedera hedera = new Hedera(context);
            var client = hedera.createHederaClient();
            FileId fileId = FileId.fromString(fileNumInString);
            shellHelper.printInfo("file: " + fileId);

            // now to delete the file
            var txDeleteReceipt = new FileDeleteTransaction(client)
                    .setFileId(fileId)
                    .executeForReceipt();

            if(txDeleteReceipt.getStatus() != ResponseCodeEnum.SUCCESS) {
                shellHelper.printError("Error while deleting file");
            }

            System.out.println("File deleted successfully");
            var fileInfo = new FileInfoQuery(client)
                    .setFileId(fileId)
                    .execute();

            System.out.println("File info " + fileInfo);
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }
}
