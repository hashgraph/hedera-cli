package com.hedera.cli.hedera.file;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.TransactionManager;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.file.FileContentsQuery;
import com.hedera.hashgraph.sdk.file.FileId;
import com.hedera.hashgraph.sdk.file.FileInfoQuery;
import com.hederahashgraph.api.proto.java.FileGetContentsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Command;

import java.util.concurrent.TimeoutException;

@Component
@Command(name = "info", description = "|@fg(225) Queries the info of a file|@")
public class FileInfo implements Runnable {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private TransactionManager txManager;

    @Parameters(index = "0", description = "@|fg(225) File Id in the format of shardNum.realmNum.fileNum"
            + "%n@|bold,underline Usage:|@%n"
            + "@|fg(yellow) file info 0.0.1003|@")
    private String fileNumInString;

    @Override
    public void run() {
        try (Client client = hedera.createHederaClient()) {
            FileId fileId = FileId.fromString(fileNumInString);

            com.hedera.hashgraph.sdk.file.FileInfo fileInfo = new FileInfoQuery(client)
                    .setFileId(fileId)
                    .execute();

            shellHelper.printInfo("File info : " + fileInfo);
            shellHelper.printInfo("File expiry time : " + fileInfo.getExpirationTime());
            shellHelper.printInfo("File public key : " + fileInfo.getKeys());
            shellHelper.printInfo("File size : " + fileInfo.getSize());

            FileGetContentsResponse fileContents = new FileContentsQuery(client)
                    .setFileId(fileId)
                    .execute();

            shellHelper.printSuccess("File content : " + fileContents);
            shellHelper.printSuccess("File has file contents : " + fileContents.hasFileContents());
            shellHelper.printSuccess("File get file contents file contents to sring: " + fileContents.getFileContents().getContents().toString());
            shellHelper.printSuccess("File get file contents utf 8 : " + fileContents.getFileContents().getContents().toStringUtf8());
//            shellHelper.printSuccess("File get all fields : " + fileContents.getAllFields());
            shellHelper.printSuccess("File content hashcode : " + fileContents.hashCode());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            // do nothing here
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }
}
