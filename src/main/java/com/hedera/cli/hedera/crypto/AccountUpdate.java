package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
import com.hedera.hashgraph.sdk.HederaException;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.account.AccountUpdateTransaction;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(name="update",
        description = "@|fg(225) Updates the account public key|@",
        subcommands = {})
public class AccountUpdate implements Runnable {

    @Autowired
    ApplicationContext context;

    @Autowired
    Hedera hedera;

    @Option(names = {"-a", "--account"}, description = "AccountId of public key to be updated")
    private String accountId;
//
//    @Option(names = {"-origk", "--origKey"}, description = "Original public key to be updated")
//    private String originalKey;

    @Override
    public void run() {
        try {
            // Hedera hedera = new Hedera(context);
            var client = hedera.createHederaClient();

            boolean update = false;
            if (update) {
                // parse in keys
//                var originalKeyFromLocal = DataDirectory.readFile("network.txt", );
                // update keys
            }
            // First, we create a new account so we don't affect our account
            var originalKey = Ed25519PrivateKey.generate();
            var accountId = client.createAccount(originalKey.getPublicKey(), 0);

            // Next, we update the key
            var newKey = Ed25519PrivateKey.generate();
            System.out.println(" :: update public key of account " + accountId);
            System.out.println("set key = " + newKey.getPublicKey());
            TransactionId transactionId = new TransactionId(hedera.getOperatorId());
            new AccountUpdateTransaction(client).setAccountForUpdate(accountId)
                    .setTransactionId(transactionId)
                    .setKey(newKey.getPublicKey())
                    // Sign with the previous key and the new key
                    .sign(originalKey)
                    .sign(newKey)
                    .executeForReceipt();
            // Now we fetch the account information to check if the key was changed
            System.out.println(" :: getAccount and check our current key");

            var info = client.getAccount(accountId);

            System.out.println("key = " + info.getKey());
        } catch (HederaException e) {
            e.printStackTrace();
        }
    }
}
