package com.hedera.cli.hedera.file;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.crypto.Operation;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.proto.ResponseCodeEnum;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.file.FileDeleteTransaction;
import com.hedera.hashgraph.sdk.file.FileId;
import com.hedera.hashgraph.sdk.file.FileInfoQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.TimeoutException;

@Component
@Command(name = "delete",
        description = "@|fg(225) Deletes specified file from the Hedera network|@")
public class FileDelete implements Runnable, Operation {

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
            var txDeleteReceipt = new FileDeleteTransaction()
                    .setTransactionId(transactionId)
                    .setFileId(fileId)
                    .execute(client)
                    .getReceipt(client);

            if(txDeleteReceipt.status != ResponseCodeEnum.SUCCESS) {
                shellHelper.printError("Error while deleting file");
            }

            shellHelper.printInfo("File deleted successfully");
            var fileInfo = new FileInfoQuery()
                    .setFileId(fileId)
                    .execute(client);
          
            shellHelper.printInfo("File info : " + fileInfo);
            shellHelper.printInfo("File expiry time : " + fileInfo.expirationTime);
            shellHelper.printInfo("File public key : " + fileInfo.keys);
            shellHelper.printInfo("File size : " + fileInfo.size);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            // do nothing
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    @Override
    public void executeSubCommand(InputReader inputReader, String... args) {
        if(args.length == 0) {
            CommandLine.usage(this, System.out);
        } else {
            try {
                new CommandLine(this).execute(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
