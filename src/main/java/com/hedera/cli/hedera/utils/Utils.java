package com.hedera.cli.hedera.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hedera.cli.models.TransactionObj;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class Utils {

    public Instant dateToMilliseconds(String[] dateInString) throws ParseException {
        StringBuilder appendedString = new StringBuilder();
        System.out.println("The date from cli is: ");
        for (String date : dateInString) {
            appendedString.append(date).append(" ");
        }
        SimpleDateFormat sdfWithTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date dateWithTime;
        dateWithTime = sdfWithTime.parse(appendedString.toString());
        Instant instant = dateWithTime.toInstant();
        System.out.println("Date of File Expiry is: " + instant);
        return instant;
    }

    public void saveTransactionsToJson(String txID, TransactionObj obj) {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonString;
        String filename;
        try {
            jsonString = ow.writeValueAsString(obj);
            DataDirectory dataDirectory = new DataDirectory();
            String networkName = dataDirectory.readFile("network.txt");
            String pathToTransactionFolder = networkName + File.separator + "transaction" + File.separator;
            filename = txID + ".json";
            String pathToTransactionFile = pathToTransactionFolder + filename;
            dataDirectory.mkHederaSubDir(pathToTransactionFolder);
            dataDirectory.writeFile(pathToTransactionFile, jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
