package com.hedera.cli.hedera.utils;

import java.io.File;
import java.util.HashMap;

import com.hedera.hashgraph.sdk.account.AccountId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountUtils {

    @Autowired
    private DataDirectory dataDirectory;

    private static final String DEFAULT = "default.txt";
    private static final String PRIVATEKEY = "privateKey";
    private static final String PUBLICKEY = "publicKey";

    public String pathToAccountsFolder() {
        String networkName = dataDirectory.readFile("network.txt");
        return networkName + File.separator + "accounts" + File.separator;
    }

    public String pathToIndexTxt() {
        return pathToAccountsFolder() + "index.txt";
    }

    /**
     * Default Account ID found in default.txt
     * @return
     */
    public String[] defaultAccountString() {
        String pathToAccountsFolder = pathToAccountsFolder();
        String pathToDefaultTxt = pathToAccountsFolder + DEFAULT;
        // read the key value, the associated file in the list
        String fileString = dataDirectory.readFile(pathToDefaultTxt);
        return fileString.split(":");
    }

    public AccountId retrieveDefaultAccountID() {
        String[] accountString = defaultAccountString();
        return AccountId.fromString(accountString[1]);
    }

    public String retrieveDefaultAccountKeyInHexString() {
        String pathToDefaultJsonAccount = pathToAccountsFolder() + defaultAccountString()[0] + ".json";
        HashMap<String, String> defaultJsonAccount = dataDirectory.jsonToHashmap(pathToDefaultJsonAccount);
        return defaultJsonAccount.get(PRIVATEKEY).toString();
    }

    public String retrieveDefaultAccountPublicKeyInHexString() {
        String pathToDefaultJsonAccount = pathToAccountsFolder() + defaultAccountString()[0] + ".json";
        HashMap<String, String> defaultJsonAccount = dataDirectory.jsonToHashmap(pathToDefaultJsonAccount);
        return defaultJsonAccount.get(PUBLICKEY).toString();
    }
}
