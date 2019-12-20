package com.hedera.cli.hedera.file;

import com.google.protobuf.ByteString;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.proto.FileGetContentsResponse;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.file.FileContentsQuery;
import com.hedera.hashgraph.sdk.file.FileId;
import com.hedera.hashgraph.sdk.file.FileInfoQuery;
import org.hjson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

@Component
@Command(name = "info", description = "|@fg(225) Queries the info of a file|@")
public class FileInfo implements Runnable {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    @Parameters(index = "0", description = "@|fg(225) File Id in the format of shardNum.realmNum.fileNum"
            + "%n@|bold,underline Usage:|@%n"
            + "@|fg(yellow) file info 0.0.1003|@")
    private String fileNumInString;

    @Override
    public void run() {
        try (Client client = hedera.createHederaClient()) {
            FileId fileId = FileId.fromString(fileNumInString);

            com.hedera.hashgraph.sdk.file.FileInfo fileInfo = new FileInfoQuery()
                    .setFileId(fileId)
                    .execute(client);

            shellHelper.printInfo("File info : " + fileInfo);
            shellHelper.printInfo("File info : " + fileInfo.fileId);
            shellHelper.printInfo("File expiry time : " + fileInfo.expirationTime);
            shellHelper.printInfo("File public key : " + fileInfo.keys);
            shellHelper.printInfo("File size : " + fileInfo.size);

            FileGetContentsResponse fileContents = new FileContentsQuery()
                    .setFileId(fileId)
                    .execute(client);

            boolean fileVerified = verifyFileContentAValidUTF8ByteSequence(fileContents.getFileContents().getContents());
            String decodedText = decodeText(fileContents.getFileContents().getContents().toStringUtf8(), "UTF-8");
            System.out.println("decoded text: " + fileVerified);
            System.out.println(decodedText);
            shellHelper.printSuccess("File content : " + fileContents);
            shellHelper.printSuccess("File has file contents : " + fileContents.hasFileContents());
            shellHelper.printSuccess("File get file contents file contents to string: " + fileContents.getFileContents().getContents());
            shellHelper.printSuccess("File get file contents utf 8 : " + fileContents.getFileContents().getContents().toStringUtf8());
            shellHelper.printSuccess("File content hashcode : " + fileContents.hashCode());

            JsonObject objJsonObject = new JsonObject();
            objJsonObject.get(fileContents.getFileContents().getContents().toString());
            System.out.println(objJsonObject);
            System.out.println("2");

            JsonObject objJsonObject1 = new JsonObject();
            objJsonObject1.get(fileContents.getFileContents().getContents().toStringUtf8());
            System.out.println(objJsonObject1);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            // do nothing here
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    public String decodeText(String input, String encoding) throws IOException {
        return new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(input.getBytes())))
                .readLine();
    }

    public boolean verifyFileContentAValidUTF8ByteSequence(ByteString byteString) throws UnsupportedEncodingException {
        return Arrays.equals(byteString.toByteArray(),
                new String(byteString.toByteArray(), "UTF-8").getBytes("UTF-8"));
    }
}
