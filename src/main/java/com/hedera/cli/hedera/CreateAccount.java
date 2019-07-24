package com.hedera.cli.hedera;

import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.HederaException;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.account.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.cli.ExampleHelper;
import io.github.cdimascio.dotenv.Dotenv;
import picocli.CommandLine.Command;

import java.util.Map;

@Command(name = "blah")
public class CreateAccount implements Runnable {

    public static Client hederaClient() {

        // Grab configuration variables from the .env file

        var operatorId = AccountId.fromString(Dotenv.load().get("OPERATOR_ID"));
        var operatorKey = Ed25519PrivateKey.fromString(Dotenv.load().get("OPERATOR_KEY"));
        var nodeId = AccountId.fromString(Dotenv.load().get("NODE_ID"));
        var nodeAddress = Dotenv.load().get("NODE_ADDRESS");

        // Build client

        var hederaClient = new Client(Map.of(nodeId, nodeAddress));

        // Set the the account ID and private key of the operator

        hederaClient.setOperator(operatorId, operatorKey);

        return hederaClient;
    }

    @Override
    public void run() {

        // Generate a Ed25519 private, public key pair
        var newKey = Ed25519PrivateKey.generate();
        var newPublicKey = newKey.getPublicKey();

        System.out.println("private key = " + newKey);
        System.out.println("public key = " + newPublicKey);

        var client = ExampleHelper.createHederaClient();

        var tx = new AccountCreateTransaction(client)
                // The only _required_ property here is `key`
                .setKey(newKey.getPublicKey())
                .setInitialBalance(1000);

        // This will wait for the receipt to become available
        TransactionReceipt receipt = null;
        try {
            receipt = tx.executeForReceipt();
        } catch (HederaException e) {
            e.printStackTrace();
        }

        var newAccountId = receipt.getAccountId();
        System.out.println("account = " + newAccountId);

    }
}