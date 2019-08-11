package com.hedera.cli.hedera;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.models.Network;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.models.AddressBook;
import com.hedera.cli.models.HederaNode;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Hedera {

    private HederaNode node;

    public Hedera() {
        boolean dev = true;
        if (dev) {
            System.out.println("devv");
            InputStream addressBookInputStream = getClass().getResourceAsStream("/addressbook.json");
            this.node = this.getSingleNode(addressBookInputStream);
        }
        this.node = this.getRandomNode();
    }

    private HederaNode getRandomNode() {
        String addressBookJson = File.separator + "addressbook.json";
        InputStream addressBookInputStream = getClass().getClassLoader().getResourceAsStream(addressBookJson);
        ObjectMapper objectMapper = new ObjectMapper();
        HederaNode node = null;
        try {
            AddressBook addressBook = objectMapper.readValue(addressBookInputStream, AddressBook.class);
            List<Network> networks = addressBook.getNetworks();
            DataDirectory dataDirectory = new DataDirectory();
            String currentNetwork = dataDirectory.readFile("network.txt", "aspen");
            for (Network network: networks) {
                if (network.getName().equals(currentNetwork)) {
                    node = network.getRandomNode();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return node;
    }

    public HederaNode getSingleNode(InputStream addressBookInputStream) {
        HederaNode node = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AddressBook addressBook = objectMapper.readValue(addressBookInputStream, AddressBook.class);
            List<Network> networks = addressBook.getNetworks();
            DataDirectory dataDirectory = new DataDirectory();
            String currentNetwork = dataDirectory.readFile("network.txt", "external");
            for (Network network: networks) {
                if (network.getName().equals(currentNetwork)) {
                    node = network.getSingleNode();
                    System.out.println("here");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return node;
    }

    private List<Network> getNetworks() {
        List<Network> networks = null;
        String addressBookJson = File.separator + "addressbook.json";
        InputStream addressBookInputStream = getClass().getClassLoader().getResourceAsStream(addressBookJson);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            AddressBook addressBook = objectMapper.readValue(addressBookInputStream, AddressBook.class);
            networks = addressBook.getNetworks();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return networks;
    }

    public List<String> getNetworksStrings() {
        List<String> list = new ArrayList<String>();
        List<Network> networks = this.getNetworks();
        for (Network network: networks) {
            list.add(network.getName());
        }
        return list;
    }

    public static Dotenv getEnv() {
        // Load configuration from the environment or a $projectRoot/.env file, if present
        // See .env.sample for an example of what it is looking for
        return Dotenv.load();
    }

    public AccountId getNodeId() {
        return AccountId.fromString(this.node.getAccount());
    }

    public static AccountId getOperatorId() {
        return AccountId.fromString(Objects.requireNonNull(getEnv().get("OPERATOR_ID")));
    }

    public static Ed25519PrivateKey getOperatorKey() {
        return Ed25519PrivateKey.fromString(Objects.requireNonNull(getEnv().get("OPERATOR_KEY")));
    }

    public Client createHederaClient() {
        // To connect to a network with more nodes, add additional entries to the network map
        var nodeAddress = this.node.getAddress();
        var client = new Client(Map.of(this.getNodeId(), nodeAddress));

        // Defaults the operator account ID and key such that all generated transactions will be paid for
        // by this account and be signed by this key
        client.setOperator(getOperatorId(), getOperatorKey());

        return client;
    }

    public static byte[] parseHex(String hex) {
        var len = hex.length();
        var data = new byte[len / 2];

        var i = 0;

        //noinspection NullableProblems
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
