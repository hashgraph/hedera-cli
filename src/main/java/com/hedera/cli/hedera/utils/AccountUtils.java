package com.hedera.cli.hedera.utils;

import com.hedera.hashgraph.sdk.account.AccountId;

import java.io.File;
import java.util.HashMap;

public class AccountUtils {

    private static final String CURRENT = "current.txt";
    private static final String DEFAULT = "default.txt";
    private static final String PRIVATEKEY = "privateKey";
    private static final String PUBLICKEY = "publicKey";


    public String pathToAccountsFolder() {
        DataDirectory dataDirectory = new DataDirectory();
        String networkName = dataDirectory.readFile("network.txt");
        return networkName + File.separator + "accounts" + File.separator;
    }

    /**
     * Default Account ID found in default.txt
     * @return
     */
    public String[] defaultAccountString() {
        String pathToAccountsFolder = pathToAccountsFolder();
        String pathToDefaultTxt = pathToAccountsFolder + DEFAULT;

        // read the key value, the associated file in the list
        DataDirectory dataDirectory = new DataDirectory();
        String fileString = dataDirectory.readFile(pathToDefaultTxt);
        return fileString.split(":");
    }

    public AccountId retrieveDefaultAccountID() {
        String pathToAccountsFolder = pathToAccountsFolder();
        String pathToDefaultTxt = pathToAccountsFolder + DEFAULT;

        // read the key value, the associated file in the list
        DataDirectory dataDirectory = new DataDirectory();
        String fileString = dataDirectory.readFile(pathToDefaultTxt);
        String[] accountString = fileString.split(":");
        return AccountId.fromString(accountString[1]);
    }

    public String retrieveDefaultAccountKeyInHexString() {
        DataDirectory dataDirectory = new DataDirectory();
        String pathToDefaultJsonAccount = pathToAccountsFolder() + defaultAccountString()[0] + ".json";
        HashMap defaultJsonAccount = dataDirectory.jsonToHashmap(pathToDefaultJsonAccount);
        return defaultJsonAccount.get(PRIVATEKEY).toString();
    }

    public String retrieveDefaultAccountPublicKeyInHexString() {
        DataDirectory dataDirectory = new DataDirectory();
        String pathToDefaultJsonAccount = pathToAccountsFolder() + defaultAccountString()[0] + ".json";
        HashMap defaultJsonAccount = dataDirectory.jsonToHashmap(pathToDefaultJsonAccount);
        return defaultJsonAccount.get(PUBLICKEY).toString();
    }

    /**
     * Current Account ID found in current.txt
     * @return
     */
    public String[] currentAccountString() {
        String pathToAccountsFolder = pathToAccountsFolder();
        String pathToCurrentTxt = pathToAccountsFolder +  CURRENT;

        // read the key value, the associated file in the list
        DataDirectory dataDirectory = new DataDirectory();
        String fileString = dataDirectory.readFile(pathToCurrentTxt);
        return fileString.split(":");
    }

    public AccountId retrieveCurrentAccountID() {
        String pathToAccountsFolder = pathToAccountsFolder();
        String pathToCurrentTxt = pathToAccountsFolder +  CURRENT;

        // read the key value, the associated file in the list
        DataDirectory dataDirectory = new DataDirectory();
        String fileString = dataDirectory.readFile(pathToCurrentTxt);
        String[] accountString = fileString.split(":");
        return AccountId.fromString(accountString[1]);
    }

    public String retrieveCurrentAccountKeyInHexString() {
        DataDirectory dataDirectory = new DataDirectory();
        String pathToCurrentJsonAccount = pathToAccountsFolder() + currentAccountString()[0] + ".json";
        HashMap currentJsonAccount = dataDirectory.jsonToHashmap(pathToCurrentJsonAccount);
        return currentJsonAccount.get(PRIVATEKEY).toString();
    }

    public String retrieveCurrentAccountPublicKeyInHexString() {
        DataDirectory dataDirectory = new DataDirectory();
        String pathToCurrentJsonAccount = pathToAccountsFolder() + currentAccountString()[0] + ".json";
        HashMap currentJsonAccount = dataDirectory.jsonToHashmap(pathToCurrentJsonAccount);
        return currentJsonAccount.get(PUBLICKEY).toString();
    }
}
