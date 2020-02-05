package com.hedera.cli.hedera;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.google.protobuf.ByteString;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.AddressBookManager;
import com.hedera.cli.models.DataDirectory;
import com.hedera.cli.models.HederaNode;
import com.hedera.cli.models.Network;
import com.hedera.cli.services.CurrentAccountService;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.proto.ResponseCodeEnum;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Transaction;
import com.hedera.hashgraph.sdk.TransactionBuilder;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.PrivateKey;
import com.hedera.hashgraph.sdk.crypto.PublicKey;
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

    @Autowired
    public ShellHelper shellHelper;

    private HederaNode node;

    enum KeyType {
        PRIVATE, PUBLIC
    }

    // Our operator account is returned, in order of priority,
    // current account, default account, no account (empty string)
    public String getOperatorAccount() {

        // is there an in-memory current account?
        String currentAccount = currentAccountId();
        if (!StringUtil.isNullOrEmpty(currentAccount)) {
            return currentAccount;
        }
        // is there an on-disk default account?
        String defaultAccountNameAndId = getDefaultAccount();
        if (!defaultAccountNameAndId.isEmpty()) {
            String[] defaultArray = defaultAccountNameAndId.split(":");
            return defaultArray[1];
        }
        // no account, so empty string
        return "";
    }

    public String getDefaultAccount() {
        String defaultAccount = "";
        String currentNetwork = addressBookManager.getCurrentNetworkAsString();
        String pathToDefaultAccount = currentNetwork + File.separator + "accounts" + File.separator
                + AddressBookManager.ACCOUNT_DEFAULT_FILE;
        try {
            defaultAccount = dataDirectory.readFile(pathToDefaultAccount);
        } catch (Exception e) {
            // no default account
            return "";
        }
        return defaultAccount;
    }

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
        CurrentAccountService currentAccountService = context.getBean("currentAccount", CurrentAccountService.class);
        String network = currentAccountService.getNetwork();
        String currentNetwork = addressBookManager.getCurrentNetworkAsString();
        if (currentNetwork.equals(network)) {
            return currentAccountService.getAccountNumber();
        }
        return "";
    }

    public String retrieveIndexAccountKeyInHexString(KeyType keyType) {
        String pathToIndexTxt = accountManager.pathToIndexTxt();

        String accountId;
        String value;
        String key = "";

        Map<String, String> readingIndexAccount = dataDirectory.readIndexToHashmap(pathToIndexTxt);
        for (Map.Entry<String, String> entry : readingIndexAccount.entrySet()) {
            accountId = entry.getKey(); // key refers to the account id
            value = entry.getValue(); // value refers to the filename json
            String currentAccountId = currentAccountId();
            if (accountId.equals(currentAccountId)) {
                String pathToCurrentJsonAccount = accountManager.pathToAccountsFolder() + value + ".json";
                Map<String, String> currentJsonAccount = dataDirectory.readJsonToHashmap(pathToCurrentJsonAccount);
                if (keyType == KeyType.PRIVATE) {
                    key = currentJsonAccount.get("privateKey").toString();
                } else {
                    key = currentJsonAccount.get("publicKey").toString();
                }

            }
        }
        return key;
    }

    public Ed25519PrivateKey getOperatorKey() {
        String privateKeyInHexString;
        boolean currentAccountExist = currentAccountExist();
        if (currentAccountExist) {
            privateKeyInHexString = retrieveIndexAccountKeyInHexString(KeyType.PRIVATE);
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

    /**
     * Executes the given transaction after signing it using the given keys and current operator account.
     */
    public TransactionReceipt executeTransaction(TransactionBuilder transactionBuilder, List<PrivateKey> signingKeys) {
        TransactionReceipt receipt = null;
        try (Client client = createHederaClient()) {
            Transaction transaction = transactionBuilder.build(client);
            ByteString operatorKey = ByteString.copyFrom(getOperatorKey().publicKey.toBytes());
            for (PrivateKey privateKey : signingKeys) {
                if (!privateKey.publicKey.hasPrefix(operatorKey)) {  // double signing with same key is not allowed
                    transaction.sign(privateKey);
                }
            }
            receipt = transaction
                    .execute(client)
                    .getReceipt(client);
            shellHelper.print(receipt.status.toString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            // do nothing
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
        return receipt;
    }
}
