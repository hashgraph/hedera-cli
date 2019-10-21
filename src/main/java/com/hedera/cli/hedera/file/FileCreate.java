package com.hedera.cli.hedera.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.utils.Utils;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.file.FileCreateTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(name = "create", description = "@|fg(225) Creates a new File and returns a FileID in the format of%n"
        + "shardNum.realmNum.fileNum|@")
public class FileCreate implements Runnable {

    @Autowired
    private Hedera hedera;

    @Autowired
    private Utils utils;

    @Autowired
    ShellHelper shellHelper;

    @Option(names = { "-d", "--date" }, arity = "0..2", description = "Enter file expiry date in the format of%n"
            + "dd-MM-yyyy hh:mm:ss%n" + "%n@|bold,underline Usage:|@%n"
            + "@|fg(yellow) file create -d=22-02-2019,21:30:58|@")
    private String[] date;

    @Option(names = { "-t", "--maxTransactionFee" }, description = "Enter the maximum fee in tinybars%n"
            + "%n@|bold,underline Usage:|@%n" + "@|fg(yellow) file create -f=200000|@")
    private int maxTransactionFee;

    @Option(names = {"-c", "--contentsString"}, split = " ", arity = "0..*",
            description = "File contents in string"
                    + "%n@|bold,underline Usage:|@%n"
                    + "@|fg(yellow) file create -d=22-11-2019,21:21:21 -t=200000 -c=\"winter is coming!\"|@")
    private String[] fileContentsInString;

    @Option(names = {"-s", "--fileSizeByte"},
            description = "Test file size"
                    + "%n@|bold,underline Usage:|@%n"
                    + "@|fg(yellow) file create -d=22-11-2019,21:21:21 -t=200000 -s=10000|@")
    private int fileSizeByte;

    // @ArgGroup(exclusive = false)
    // Dependent dependent;
    //
    // private class Dependent {
    // @Option(names = {"-d", "--date"}, required = true, arity = "0..2",
    // description = "Enter file expiry date in the format of%n"
    // + "dd-MM-yyyy hh:mm:ss%n"
    // + "%n@|bold,underline Usage:|@%n"
    // + "@|fg(yellow) file create -d=22-02-2019,21:30:58|@")
    // private String[] date;
    //
    // @Option(names = {"-t", "--maxTransactionFee"}, required = true,
    // description = "Enter the maximum fee in tinybars%n"
    // + "%n@|bold,underline Usage:|@%n"
    // + "@|fg(yellow) file create -f=200000|@")
    // private int maxTransactionFee;
    // }
    //
    // @ArgGroup(exclusive = true, multiplicity = "1")
    // Exclusive exclusive;
    //
    // private class Exclusive {
    // @Option(names = {"-s", "--contentsString"}, required = true, split = " ",
    // arity = "0..*",
    // description = "File contents in string"
    // + "%n@|bold,underline Usage:|@%n"
    // + "@|fg(yellow) file create -s=\"hello world again\"|@")
    // private String[] fileContentsInString;
    //
    // @Option(names = {"-b", "--fileSizeByte"}, required = true,
    // description = "Test file size")
    // private int fileSizeByte;
    // }

    // @Option(names = {"-p", "--contentsPath"}, split = " ", arity = "0..*",
    // description = "Path to file"
    // + "%n@|bold,underline Usage:|@%n"
    // + "@|fg(yellow) file create -p=file/to/path.txt|@")
    // private String[] pathToFile;

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
        try {
            var operatorKey = hedera.getOperatorKey();
            var client = hedera.createHederaClient().setMaxTransactionFee(maxTransactionFee);
            shellHelper.print(String.valueOf(maxTransactionFee));
            shellHelper.print(String.valueOf(Arrays.asList(date)));

            FileCreateTransaction tx = null;
            Instant instant = utils.dateToMilliseconds(date);
            TransactionId transactionId = new TransactionId(hedera.getOperatorId());

            boolean testSize = false;
            if (testSize) {
                // This is to test the file size, by parsing in -b=100, it creates file contents on 100bytes
                var fileContentsTestSize = stringOfNBytes(fileSizeByte).getBytes();
                tx = new FileCreateTransaction(client)
                        .setTransactionId(transactionId)
                        .setExpirationTime(instant)
                        // Use the same key as the operator to "own" this file
                        .addKey(operatorKey.getPublicKey())
                        .setContents(fileContentsTestSize)
                        .setTransactionFee(maxTransactionFee);
            } else {
                // The file is required to be a byte array,
                // you can easily use the bytes of a file instead.
                var fileContents = stringArrayToString(fileContentsInString).getBytes();
                tx = new FileCreateTransaction(client)
                        .setTransactionId(transactionId)
                        .setExpirationTime(instant)
                        // Use the same key as the operator to "own" this file
                        .addKey(operatorKey.getPublicKey())
                        .setContents(fileContents)
                        .setTransactionFee(maxTransactionFee);
            }
            // This will wait for the receipt to become available
            TransactionReceipt receipt = tx.executeForReceipt();
            var newFileId = receipt.getFileId();
            shellHelper.print("file: " + newFileId);
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    public byte[] readBytesFromFilePath(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {

            File javaFile = new File(filePath);
            bytesArray = new byte[(int) javaFile.length()];

            // read file into bytes[]
            fileInputStream = new FileInputStream(javaFile);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            shellHelper.printError(e.getMessage());
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    shellHelper.printError(e.getMessage());
                }
            }
        }
        return bytesArray;
    }

    public String stringArrayToString(String[] fileContentsInString) {
        String str = Arrays.toString(fileContentsInString);
        str = str.substring(1, str.length() - 1).replace(",", "");
        return str;
    }

    public String stringOfNBytes(int fileSizeByte) {
        String result = String.join("", Collections.nCopies(fileSizeByte, "A"));
        return result;
    }
}
