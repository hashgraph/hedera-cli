
package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;

import com.hedera.cli.hedera.bip39.Mnemonic;
import com.hedera.cli.hedera.keygen.*;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.account.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;
import com.hedera.hashgraph.sdk.proto.AccountID;
import io.github.cdimascio.dotenv.Dotenv;
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
                + "default is false"
                + "%n@|bold,underline Usage:|@"
                + "%n@|fg(yellow) account create -k=true,-b=100000|@")
        private boolean keyGen;

        @Override
        public void run() {

                keyGen = false;
                if (keyGen) {
                        // If keyGen via args is set to true, generate new keys
                        KeyGeneration keyGeneration = new KeyGeneration();
                        HGCSeed hgcSeed = new HGCSeed((CryptoUtils.getSecureRandomData(32)));
                        List<String> mnemonic = keyGeneration.generateMnemonic(hgcSeed);
                        KeyPair keypair = keyGeneration.generateKeysAndWords(hgcSeed);
                        System.out.println("AccountCreate subcommand");
                        var newKey = Ed25519PrivateKey.fromString(keypair.getPrivateKeyEncodedHex());
                        var newPublicKey = Ed25519PublicKey.fromString(keypair.getPublicKeyEncodedHex());
                        AccountId accountID = createNewAccount(newKey, newPublicKey);
                        System.out.println("AccountID = " + accountID);
                }
                // Else keyGen always set to false and read from Dotenv
                var newKey = Ed25519PrivateKey.fromString(Dotenv.load().get("KEYGEN_MOBILE_PRIVATE_KEY"));
                var newPublicKey = Ed25519PublicKey.fromString(Dotenv.load().get("KEYGEN_MOBILE_PUBLIC_KEY"));
                AccountId accountID = createNewAccount(newKey, newPublicKey);
                System.out.println("AccountID = " + accountID);
        }

        public AccountId createNewAccount(Ed25519PrivateKey privateKey, Ed25519PublicKey publicKey) {
                System.out.println("private key = " + privateKey);
                System.out.println("public key = " + publicKey);
                AccountId accountId = null;
                Hedera hedera = new Hedera();
                var client = hedera.createHederaClient().setMaxTransactionFee(100000000);
                var tx = new AccountCreateTransaction(client)
                        // The only _required_ property here is `key`
                        .setKey(privateKey.getPublicKey()).setInitialBalance(initBal);

                // This will wait for the receipt to become available
                TransactionReceipt receipt = null;
                try {
                        receipt = tx.executeForReceipt();
                        if (receipt != null) {
                                accountId = receipt.getAccountId();
                        } else {
                                throw new Exception("Receipt is null");
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return accountId;
        }
}