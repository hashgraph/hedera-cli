package com.hedera.cli.hedera.utils;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.hedera.setup.Setup;
import com.hedera.cli.models.TransactionObj;
import com.hedera.hashgraph.sdk.account.AccountId;

import org.hjson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class Utils {

    @Autowired
    private DataDirectory dataDirectory;

    @Autowired
    private Setup setup;

    public ZonedDateTime dateToMilliseconds(String[] dateInString) {
        System.out.println("The date from cli is: ");
        StringBuilder sb = new StringBuilder();
        for (String s: dateInString) {
            sb.append(s).append(" ");
        }
        TimeZone tz = Calendar.getInstance().getTimeZone();
        sb.append(tz.getID());
        String dateString = sb.toString();
        System.out.println(dateString);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss z");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter);
        System.out.println("Date of File Expiry is: " + zonedDateTime.toString());
        return zonedDateTime; 
    }

    public void saveTransactionsToJson(String txID, TransactionObj obj) {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonString;
        String filename;
        try {
            jsonString = ow.writeValueAsString(obj);
            String networkName = dataDirectory.readFile("network.txt");
            String pathToTransactionFolder = networkName + File.separator + "transactions" + File.separator;
            filename = txID + ".json";
            String pathToTransactionFile = pathToTransactionFolder + filename;
            dataDirectory.mkHederaSubDir(pathToTransactionFolder);
            dataDirectory.writeFile(pathToTransactionFile, jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveAccountsToJson(KeyPair keyPair, AccountId accountId) {
        JsonObject account = new JsonObject();
        account.add("accountId", accountId.toString());
        account.add("privateKey", keyPair.getPrivateKeyHex());
        account.add("publicKey", keyPair.getPublicKeyHex());
        setup.saveToJson(accountId.toString(), account);
    }
}
