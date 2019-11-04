package com.hedera.cli.models;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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
        String dateString = sb.toString().stripTrailing();
        System.out.println(dateString);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = formatter.parse(dateString);
        System.out.println("Date of File Expiry is: " + date.toInstant().toString());
        return date.toInstant();
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