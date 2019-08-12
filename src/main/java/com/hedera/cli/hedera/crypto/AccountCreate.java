
package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;

import com.hedera.cli.hedera.bip39.Mnemonic;
import com.hedera.cli.hedera.keygen.*;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.account.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.util.Arrays;
import java.util.List;

@Command(name = "create",
        description = "@|fg(magenta) Generates a new Ed25519 Keypair compatible with java and wallet,"
                + "%ntogether with 24 recovery words (bip39 compatible),"
                + "%nCreates a new Hedera account and "
                + "%nReturns an accountID in the form of shardNum.realmNum.accountNum.|@", helpCommand = true)
public class AccountCreate implements Runnable {

        @Option(names = { "-r", "--record" }, description = "Generates a record that lasts 25hrs")
        private boolean generateRecord;

        @Option(names = { "-b", "--balance" }, description = "Initial balance of new account created in hbars "
                        + "%n@|bold,underline Usage:|@%n" + "@|fg(yellow) account create -b=100 OR%n"
                        + "account create --balance=100|@")
        private int initBal;

        @Option(names = {"-k", "--keygen"}, description = "Creates a brand new key associated with account creation"
                + "%n@|bold,underline Usage:|@"
                + "%n@|fg(yellow) account create -k=yes,-b=100000|@")
        private boolean keyGen;

        @Override
        public void run() {

                // Generate new keys
                KeyGeneration keyGeneration = new KeyGeneration();
                HGCSeed hgcSeed = new HGCSeed((CryptoUtils.getSecureRandomData(32)));
                List<String> mnemonic = keyGeneration.generateMnemonic(hgcSeed);
                KeyPair keypair = keyGeneration.generateKeysAndWords(hgcSeed);


                System.out.println("AccountCreate subcommand");
                System.out.println(generateRecord);
                System.out.println(initBal);


                // ****************** CREATE ACCOUNT ******************************

                // Use the generated keypair from above
                // Using the encoded keypair
                var newKey = Ed25519PrivateKey.fromString(keypair.getPrivateKeyEncodedHex());
                var newPublicKey = Ed25519PublicKey.fromString(keypair.getPublicKeyEncodedHex());

                // Use the generated keypair from above
                // *** Using the hex keypair (note that the priv key come from the seedAndPublicKey method
                // ** not directly from keypair due to legacy
                // var newKey = Ed25519PrivateKey.fromString(keyPair.getSeedAndPublicKeyHex().substring(0, 64));
                // var newPublicKey = Ed25519PublicKey.fromString(keyPair.getPublicKeyHex());

                // Reading from Dotenv
                // var newKey = Ed25519PrivateKey.fromString(Dotenv.load().get("KEYGEN_MOBILE_PRIVATE_KEY"));
                // var newPublicKey = Ed25519PublicKey.fromString(Dotenv.load().get("KEYGEN_MOBILE_PUBLIC_KEY"));

                System.out.println("private key = " + newKey);
                System.out.println("public key = " + newPublicKey);

                Hedera hedera = new Hedera();
                var client = hedera.createHederaClient().setMaxTransactionFee(100000000);

                var tx = new AccountCreateTransaction(client)
                                // The only _required_ property here is `key`
                                .setKey(newKey.getPublicKey()).setInitialBalance(initBal);

                // This will wait for the receipt to become available
                TransactionReceipt receipt = null;
                try {
                        receipt = tx.executeForReceipt();
                        if (receipt != null) {
                                var newAccountId = receipt.getAccountId();
                                System.out.println("account = " + newAccountId);
                        } else {
                                throw new Exception("Receipt is null");
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }
}