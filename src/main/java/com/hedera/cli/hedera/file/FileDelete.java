package com.hedera.cli.hedera.file;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.file.FileDeleteTransaction;
import com.hedera.hashgraph.sdk.file.FileId;
import com.hedera.hashgraph.sdk.file.FileInfo;
import com.hedera.hashgraph.sdk.file.FileInfoQuery;
import com.hedera.hashgraph.proto.ResponseCodeEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.TimeoutException;

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
        try (Client client = hedera.createHederaClient()) {
            FileId fileId = FileId.fromString(fileNumInString);
            shellHelper.printInfo("file: " + fileId);

            // now to delete the file
            TransactionId txId = new FileDeleteTransaction()
                    .setFileId(fileId)
                    .execute(client);

            TransactionReceipt receipt = txId.getReceipt(client);

            if(receipt.status.code != ResponseCodeEnum.SUCCESS_VALUE) {
                shellHelper.printError("Error while deleting file");
            }

            shellHelper.printInfo("File deleted successfully");
            FileInfo fileInfo = new FileInfoQuery()
                    .setFileId(fileId)
                    .execute(client);
          
            shellHelper.printInfo("File info " + fileInfo);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            // do nothing
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }
}
