package com.hedera.cli.hedera;

import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.models.Network;
import com.hedera.cli.models.AddressBook;
import com.hedera.cli.models.HederaNode;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Hedera {

    private AddressBook addressBook;
    private HederaNode node;

    public Hedera() {
        addressBook = AddressBook.init();
        this.node = this.getRandomNode();
    }

    private HederaNode getRandomNode() {
        return addressBook.getCurrentNetwork().getRandomNode();
    }

    public List<Network> getNetworks() {
        return addressBook.getNetworks();
    }

    public List<String> getNetworksStrings() {
        return addressBook.getNetworksAsStrings();
    }

    public static Dotenv getEnv() {
        // Load configuration from the environment or a $projectRoot/.env file, if
        // present
        // See .env.sample for an example of what it is looking for
        return Dotenv.load();
    }

    public AccountId getNodeId() {
        return AccountId.fromString(this.node.getAccount());
    }

    public AccountId getOperatorId() {
       return retrieveDefaultAccountID();
    }

    public Ed25519PrivateKey getOperatorKey() {
        return Ed25519PrivateKey.fromString(retrieveDefaultAccountKeyInHexString());
    }

    private String pathToAccountsFolder() {
        DataDirectory dataDirectory = new DataDirectory();
        String networkName = dataDirectory.readFile("network.txt");
        return networkName + File.separator + "accounts" + File.separator;
    }

    private String[] defaultAccountString() {
        String pathToAccountsFolder = pathToAccountsFolder();
        String pathToDefaultTxt = pathToAccountsFolder +  "default.txt";

        // read the key value, the associated file in the list
        DataDirectory dataDirectory = new DataDirectory();
        String fileString = dataDirectory.readFile(pathToDefaultTxt);
        return fileString.split(":");
    }

    public AccountId retrieveDefaultAccountID() {
        String pathToAccountsFolder = pathToAccountsFolder();
        String pathToDefaultTxt = pathToAccountsFolder +  "default.txt";

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
        return defaultJsonAccount.get("privateKey").toString();
    }

    public String retrieveDefaultAccountPublicKeyInHexString() {
        DataDirectory dataDirectory = new DataDirectory();
        String pathToDefaultJsonAccount = pathToAccountsFolder() + defaultAccountString()[0] + ".json";
        HashMap defaultJsonAccount = dataDirectory.jsonToHashmap(pathToDefaultJsonAccount);
        return defaultJsonAccount.get("publicKey").toString();
    }


    public Client createHederaClient() {
        // To connect to a network with more nodes, add additional entries to the
        // network map
        var nodeAddress = this.node.getAddress();
        var client = new Client(Map.of(this.getNodeId(), nodeAddress));

        // Defaults the operator account ID and key such that all generated transactions
        // will be paid for
        // by this account and be signed by this key
        client.setOperator(getOperatorId(), getOperatorKey());

        return client;
    }

    public static byte[] parseHex(String hex) {
        var len = hex.length();
        var data = new byte[len / 2];

        var i = 0;

        // noinspection NullableProblems
        for (var c : (Iterable<Integer>) hex.chars()::iterator) {
            if ((i % 2) == 0) {
                // high nibble
                data[i / 2] = (byte) (Character.digit(c, 16) << 4);
            } else {
                // low nibble
                data[i / 2] &= (byte) Character.digit(c, 16);
            }

            i++;
        }

        return data;
    }
}
