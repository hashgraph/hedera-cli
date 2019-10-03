package com.hedera.cli.hedera;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hedera.cli.hedera.utils.AccountUtils;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.models.AddressBook;
import com.hedera.cli.models.HederaNode;
import com.hedera.cli.models.Network;
import com.hedera.cli.services.CurrentAccountService;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.springframework.context.ApplicationContext;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;

public class Hedera {

    ApplicationContext context;

    private AddressBook addressBook;
    private HederaNode node;

    public Hedera(ApplicationContext context) {
        addressBook = AddressBook.init();
        this.node = this.getRandomNode();
        this.context = context;
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

    public AccountId getNodeId() {
        return AccountId.fromString(this.node.getAccount());
    }

    public AccountId getOperatorId() {
        AccountId operatorId;
        boolean currentAccountExist = currentAccountExist();
        if (currentAccountExist) {
            String accountNumber = currentAccountId();
            operatorId = AccountId.fromString(accountNumber);
        } else {
            AccountUtils accountUtils = new AccountUtils();
            operatorId = accountUtils.retrieveDefaultAccountID();
        }
        return operatorId;
    }

    public boolean currentAccountExist() {
        String accountNumber = currentAccountId();
        if (!StringUtil.isNullOrEmpty(accountNumber)) {
            // current account exists
            return true;
        }
        return false;
    }

    public String currentAccountId() {
        CurrentAccountService currentAccountService = (CurrentAccountService) context.getBean("currentAccount");
        return currentAccountService.getAccountNumber();
    }

    public String retrieveIndexAccountKeyInHexString() {
        DataDirectory dataDirectory = new DataDirectory();
        AccountUtils accountUtils = new AccountUtils();
        String pathToIndexTxt = accountUtils.pathToAccountsFolder() + "index.txt";

        String accountId;
        String value;
        String privateKey = "";

        HashMap<String, String> readingIndexAccount = dataDirectory.readFileHashmap(pathToIndexTxt);
        for(Map.Entry<String, String> entry : readingIndexAccount.entrySet()) {
            accountId = entry.getKey(); // key refers to the account id
            value = entry.getValue(); // value refers to the filename json
            String currentAccountId = currentAccountId();
            if (accountId.equals(currentAccountId)) {
                String pathToCurrentJsonAccount = accountUtils.pathToAccountsFolder() + value + ".json";
                HashMap currentJsonAccount = dataDirectory.jsonToHashmap(pathToCurrentJsonAccount);
                privateKey = currentJsonAccount.get("privateKey").toString();
            }
        }
        return privateKey;
    }

    public String retrieveIndexAccountPublicKeyInHexString() {
        DataDirectory dataDirectory = new DataDirectory();
        AccountUtils accountUtils = new AccountUtils();
        String pathToIndexTxt = accountUtils.pathToAccountsFolder() + "index.txt";

        String publicKey = "";
        String accountId;
        String value;

        HashMap<String, String> readingIndexAccount = dataDirectory.readFileHashmap(pathToIndexTxt);
        for(Map.Entry<String, String> entry : readingIndexAccount.entrySet()) {
            accountId = entry.getKey(); // key refers to the account id
            value = entry.getValue(); // value refers to the filename json
            String currentAccountId = currentAccountId();
            if (accountId.equals(currentAccountId)) {
                String pathToCurrentJsonAccount = accountUtils.pathToAccountsFolder() + value + ".json";
                HashMap currentJsonAccount = dataDirectory.jsonToHashmap(pathToCurrentJsonAccount);
                publicKey = currentJsonAccount.get("publicKey").toString();
            }
        }
        return publicKey;
    }

    public Ed25519PrivateKey getOperatorKey() {
        AccountUtils accountUtils = new AccountUtils();
        String privateKeyInHexString;
        boolean currentAccountExist = currentAccountExist();
        if (currentAccountExist) {
            privateKeyInHexString = retrieveIndexAccountKeyInHexString();
        } else {
            privateKeyInHexString = accountUtils.retrieveDefaultAccountKeyInHexString();
        }
        return Ed25519PrivateKey.fromString(privateKeyInHexString);
    }

    public Client createHederaClient() {
        // To connect to a network with more nodes, add additional entries to the
        // network map
        var nodeAddress = this.node.getAddress();
        var client = new Client(Map.of(this.getNodeId(), nodeAddress));

        // Defaults the operator account ID and key such that all generated transactions
        // will be paid for
        // by this account and be signed by this key
        client.setOperator(getOperatorId(), getOperatorKey()).setMaxTransactionFee(100000000);
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
