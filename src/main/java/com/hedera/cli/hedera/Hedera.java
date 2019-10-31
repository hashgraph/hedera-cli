package com.hedera.cli.hedera;

import java.util.List;
import java.util.Map;

import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.AddressBookManager;
import com.hedera.cli.models.DataDirectory;
import com.hedera.cli.models.HederaNode;
import com.hedera.cli.models.Network;
import com.hedera.cli.services.CurrentAccountService;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
public class Hedera {

    @Autowired
    private DataDirectory dataDirectory;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private AddressBookManager addressBookManager;

    @Autowired
    public AccountManager accountManager;

    private HederaNode node;

    private HederaNode getRandomNode() {
        return addressBookManager.getCurrentNetwork().getRandomNode();
    }

    public List<Network> getNetworks() {
        return addressBookManager.getNetworks();
    }

    public List<String> getNetworksStrings() {
        return addressBookManager.getNetworksAsStrings();
    }

    public AccountId getNodeId() {
        return AccountId.fromString(node.getAccount());
    }

    public AccountId getOperatorId() {
        AccountId operatorId;
        boolean currentAccountExist = currentAccountExist();
        if (currentAccountExist) {
            String accountNumber = currentAccountId();
            operatorId = AccountId.fromString(accountNumber);
        } else {
            operatorId = accountManager.getDefaultAccountId();
        }
        return operatorId;
    }

    public boolean currentAccountExist() {
        return !StringUtil.isNullOrEmpty(currentAccountId());
    }

    public String currentAccountId() {
        CurrentAccountService currentAccountService = (CurrentAccountService) context.getBean("currentAccount");
        return currentAccountService.getAccountNumber();
    }

    public String retrieveIndexAccountKeyInHexString() {
        String pathToIndexTxt = accountManager.pathToIndexTxt();

        String accountId;
        String value;
        String privateKey = "";

        Map<String, String> readingIndexAccount = dataDirectory.readIndexToHashmap(pathToIndexTxt);
        for (Map.Entry<String, String> entry : readingIndexAccount.entrySet()) {
            accountId = entry.getKey(); // key refers to the account id
            value = entry.getValue(); // value refers to the filename json
            String currentAccountId = currentAccountId();
            if (accountId.equals(currentAccountId)) {
                String pathToCurrentJsonAccount = accountManager.pathToAccountsFolder() + value + ".json";
                Map<String, String> currentJsonAccount = dataDirectory.jsonToHashmap(pathToCurrentJsonAccount);
                privateKey = currentJsonAccount.get("privateKey").toString();
            }
        }
        return privateKey;
    }

    public String retrieveIndexAccountPublicKeyInHexString() {
        String pathToIndexTxt = accountManager.pathToIndexTxt();

        String publicKey = "";
        String accountId;
        String value;

        Map<String, String> readingIndexAccount = dataDirectory.readIndexToHashmap(pathToIndexTxt);
        for (Map.Entry<String, String> entry : readingIndexAccount.entrySet()) {
            accountId = entry.getKey(); // key refers to the account id
            value = entry.getValue(); // value refers to the filename json
            String currentAccountId = currentAccountId();
            if (accountId.equals(currentAccountId)) {
                String pathToCurrentJsonAccount = accountManager.pathToAccountsFolder() + value + ".json";
                Map<String, String> currentJsonAccount = dataDirectory.jsonToHashmap(pathToCurrentJsonAccount);
                publicKey = currentJsonAccount.get("publicKey").toString();
            }
        }
        return publicKey;
    }

    public Ed25519PrivateKey getOperatorKey() {
        String privateKeyInHexString;
        boolean currentAccountExist = currentAccountExist();
        if (currentAccountExist) {
            privateKeyInHexString = retrieveIndexAccountKeyInHexString();
        } else {
            privateKeyInHexString = accountManager.getDefaultAccountKeyInHexString();
        }
        return Ed25519PrivateKey.fromString(privateKeyInHexString);
    }

    public Client createHederaClient() { // @formatter:off
        // To connect to a network with more nodes, add additional entries to the
        // network map
        node = getRandomNode(); // can only be invoked once
        var nodeAddress = node.getAddress();
        var client = new Client(Map.of(this.getNodeId(), nodeAddress));

        // Defaults the operator account ID and key such that all generated transactions
        // will be paid for
        // by this account and be signed by this key
        client
            .setOperator(getOperatorId(), getOperatorKey())
            .setMaxTransactionFee(100000000);
        return client;
    }  // @formatter:on

    public Client createHederaClientWithoutSettingOperator() { // @formatter:off
        // To connect to a network with more nodes, add additional entries to the
        // network map
        node = getRandomNode(); // can only be invoked once
        var nodeAddress = node.getAddress();
        var client = new Client(Map.of(this.getNodeId(), nodeAddress));
        client.setMaxTransactionFee(100000000);
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
