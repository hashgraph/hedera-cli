
package com.hedera.cli.hedera.crypto;

import java.util.List;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.keygen.*;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.account.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;
import io.github.cdimascio.dotenv.Dotenv;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Model.*;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "create",
        description = "@|fg(225) Generates a new Ed25519 Keypair compatible with java and wallet,"
                + "%ntogether with 24 recovery words (bip39 compatible),"
                + "%nCreates a new Hedera account and "
                + "%nReturns an accountID in the form of shardNum.realmNum.accountNum.|@", helpCommand = true)
public class AccountCreate implements Runnable {

        @Spec
        CommandSpec spec;

        @Option(names = { "-r", "--record" }, description = "Generates a record that lasts 25hrs")
        private boolean generateRecord = false;

        @Option(names = { "-b", "--balance" }, description = "Initial balance of new account created in hbars "
                        + "%n@|bold,underline Usage:|@%n" + "@|fg(yellow) account create -b=100 OR%n"
                        + "account create --balance=100|@")
        private int initBal = 0;
        private void setMinimum(int min) {
                if (min < 0) {
                        throw new ParameterException(spec.commandLine(), "Minimum must be a positive integer");
                }
                initBal = min;
        }

        @Option(names = {"-k", "--keygen"}, description = "Creates a brand new key associated with account creation"
                + "default is false"
                + "%n@|bold,underline Usage:|@"
                + "%n@|fg(yellow) account create -k=true,-b=100000|@")
        private boolean keyGen = false;

        @Override
        public void run() {

                setMinimum(initBal);

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
                        System.out.println("mnemonic = " + mnemonic);
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