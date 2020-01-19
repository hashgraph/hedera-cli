package com.hedera.cli.models;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import javax.annotation.PostConstruct;

import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionRecord;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class TransactionManager {

    @Autowired
    private DataDirectory dataDirectory;

    @Autowired
    private ShellHelper shellHelper;

    private ObjectWriter objectWriter;

    @PostConstruct
    public void init() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }
    public Instant dateToMilliseconds(String[] dateInString) throws ParseException {
        System.out.println("The date from cli is: ");
        StringBuilder sb = new StringBuilder();
        for (String s : dateInString) {
            sb.append(s).append(" ");
        }
        String dateString = sb.toString().trim();
        System.out.println(dateString);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = formatter.parse(dateString);
        System.out.println("Date of File Expiry is: " + date.toInstant().toString());
        return date.toInstant();
    }

    public void saveTransactionRecord(TransactionRecord record) throws JsonProcessingException {
        shellHelper.printSuccess("Transfer receipt status: " + record.receipt.status.toString());
        shellHelper.printSuccess("Transfer transaction fee: " + record.transactionFee);
        shellHelper.printSuccess("Transfer consensus timestamp: " + record.consensusTimestamp.toString());
        shellHelper.printSuccess("Transfer memo: " + record.transactionMemo);

        String txID = this.printTransactionId(record.transactionId);

        TransactionObj txObj = new TransactionObj();
        txObj.setTxID(txID);
        txObj.setTxMemo(record.transactionMemo);
        txObj.setTxFee(record.transactionFee);
        txObj.setTxConsensusTimestamp(record.consensusTimestamp);
        txObj.setTxValidStart(record.transactionId.validStart.getEpochSecond() + "-"
                + record.transactionId.validStart.getNano());

        this.saveTransactionsToJson(txID, txObj);
    }

    private String printTransactionId(TransactionId transactionId) {
        String txTimestamp = transactionId.validStart.getEpochSecond() + "-"
                + transactionId.validStart.getNano();
        String txID = transactionId.accountId.toString() + "-" + txTimestamp;
        shellHelper.printSuccess("TransactionID : " + txID);
        return txID;
    }

    public void saveTransactionsToJson(String txID, TransactionObj obj) throws JsonProcessingException {
        String jsonString = objectWriter.writeValueAsString(obj);
        String networkName = dataDirectory.readFile("network.txt");
        String pathToTransactionFolder = networkName + File.separator + "transactions" + File.separator;
        String filename = txID + ".json";
        String pathToTransactionFile = pathToTransactionFolder + filename;

        dataDirectory.mkHederaSubDir(pathToTransactionFolder);
        dataDirectory.writeFile(pathToTransactionFile, jsonString);
    }
}