package com.hedera.cli.hedera.utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.hedera.setup.Setup;
import com.hedera.cli.models.TransactionObj;
import com.hedera.hashgraph.sdk.account.AccountId;

import org.hjson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;

public class Utils {

    @Autowired
    DataDirectory dataDirectory;

    @Autowired
    Setup setup;

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
