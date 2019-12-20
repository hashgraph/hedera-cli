package com.hedera.cli.hedera.file;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.proto.ResponseCodeEnum;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.file.FileDeleteTransaction;
import com.hedera.hashgraph.sdk.file.FileId;
import com.hedera.hashgraph.sdk.file.FileInfoQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.TimeoutException;

@Component
@Command(name = "delete",
        description = "@|fg(225) Deletes specified file from the Hedera network|@")
public class FileDelete implements Runnable {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    @Parameters(index = "0", description = "@|fg(225) File Id of file for deletion in the format of shardNum.realmNum.fileNum"
            + "%n@|bold,underline Usage:|@%n"
            + "@|fg(yellow) file delete 0.0.1003|@")
    private String fileNumInString;

    @Override
    public void run() {
        try (Client client = hedera.createHederaClient()) {
            FileId fileId = FileId.fromString(fileNumInString);
            shellHelper.printInfo("file: " + fileId);
            TransactionId transactionId = new TransactionId(hedera.getOperatorId());

            // now to delete the file
            var txDeleteReceipt = new FileDeleteTransaction(client)
                    .setTransactionId(transactionId)
                    .setFileId(fileId)
                    .executeForReceipt();

            if(txDeleteReceipt.getStatus() != ResponseCodeEnum.SUCCESS) {
                shellHelper.printError("Error while deleting file");
            }

            shellHelper.printInfo("File deleted successfully");
            var fileInfo = new FileInfoQuery(client)
                    .setFileId(fileId)
                    .execute();
          
            shellHelper.printInfo("File info : " + fileInfo);
            shellHelper.printInfo("File expiry time : " + fileInfo.getExpirationTime());
            shellHelper.printInfo("File public key : " + fileInfo.getKeys());
            shellHelper.printInfo("File size : " + fileInfo.getSize());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            // do nothing
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }
}
