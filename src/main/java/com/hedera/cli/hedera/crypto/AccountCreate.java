
package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;

import com.hedera.cli.hedera.bip39.Mnemonic;
import com.hedera.cli.hedera.keygen.*;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.account.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;
import io.github.cdimascio.dotenv.Dotenv;
import org.bouncycastle.util.encoders.Hex;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.util.Arrays;
import java.util.List;

@Command(name = "create", description = "@|fg(magenta) Creates a new Hedera account and returns an accountID in the form of "
                + "%nshardNum.realmNum.accountNum.|@", helpCommand = true)
public class AccountCreate implements Runnable {

        @Option(names = { "-r", "--record" }, description = "Generates a record that lasts 25hrs")
        private boolean generateRecord;

        @Option(names = { "-b", "--balance" }, description = "Initial balance of new account created "
                        + "%n@|bold,underline Usage:|@%n" + "@|fg(yellow) account create -b=100 OR%n"
                        + "account create --balance=100|@")
        private int initBal;

        @Override
        public void run() {

                // ****************** KEYGEN index 0 (as wellet) ******************************
                KeyPair keyPair;
                int index = 0;
                List<String> mnemonic;

                // This is the starting point
                // get random data and create an HGCSeed
                HGCSeed hgcSeed = new HGCSeed(CryptoUtils.getSecureRandomData(32));
                // Seed becomes a wordlist ie mnemonic
                System.out.println("seed to wordlist: " + hgcSeed.toWordsList());
                mnemonic = hgcSeed.toWordsList();

                byte[] entropy = null;
                byte[] seed;
                try {
                        entropy = new Mnemonic().toEntropy(mnemonic);
                        List<String> compareMnemonic = new Mnemonic().toMnemonic(entropy);
                        System.out.println(compareMnemonic);
                        System.out.println(mnemonic);

                } catch (Exception e) {
                        e.printStackTrace();
                }

                // keys from seed from entropy
                seed = CryptoUtils.deriveKey(entropy, index, 32);
                EDKeyPair keyPair1 = new EDKeyPair(seed);
                System.out.println("seed entropy from mnemonic: " + Arrays.toString(entropy));
                System.out.println("priv key encoded: " + keyPair1.getPrivateKeyEncodedHex());
                System.out.println("pub key encoded: " + keyPair1.getPublicKeyEncodedHex());
                System.out.println("priv key hex: " + keyPair1.getPrivateKeyHex());
                System.out.println("pub key hex: " + keyPair1.getPublicKeyHex());
                System.out.println("seed and pub key hex: " + keyPair1.getSeedAndPublicKeyHex());


                // key from hgc seed
                KeyChain keyChain = new EDKeyChain(hgcSeed);
                keyPair = keyChain.keyAtIndex(index);
                System.out.println("******* COMPARE KEYPAIR WITH KEYGEN ****** ");
                System.out.println("seed entropy from HGC Seed: " + Arrays.toString(hgcSeed.getEntropy()));
                System.out.println("priv key encoded: " + keyPair.getPrivateKeyEncodedHex()); // encoded works with index 0
                System.out.println("pub key encoded: " + keyPair.getPublicKeyEncodedHex()); // encoded works with index 0
                System.out.println("priv key hex: " + keyPair.getPrivateKeyHex());
                System.out.println("pub key hex: " + keyPair.getPublicKeyHex());
                System.out.println("seed and pub key: " + keyPair.getSeedAndPublicKeyHex());
                System.out.println("********* ********* ********* KEYPAIR WITH KEYGEN ********* ********* *********");

                System.out.println("AccountCreate subcommand");
                System.out.println(this.generateRecord);
                System.out.println(this.initBal);


                // ****************** CREATE ACCOUNT ******************************

//                // Generate a Ed25519 private, public key pair
////                var newKey = Ed25519PrivateKey.generate();
////                var newPublicKey = newKey.getPublicKey();
//
//                var newKey = Ed25519PrivateKey.fromString(Dotenv.load().get("KEYGEN_MOBILE_PRIVATE_KEY"));
//                var newPublicKey = Ed25519PublicKey.fromString(Dotenv.load().get("KEYGEN_MOBILE_PUBLIC_KEY"));
//
//                System.out.println("private key = " + newKey);
//                System.out.println("public key = " + newPublicKey);
//
//                Hedera hedera = new Hedera();
//                var client = hedera.createHederaClient().setMaxTransactionFee(100000000);
//
//                var tx = new AccountCreateTransaction(client)
//                                // The only _required_ property here is `key`
//                                .setKey(newKey.getPublicKey()).setInitialBalance(this.initBal);
//
//                // This will wait for the receipt to become available
//                TransactionReceipt receipt = null;
//                try {
//                        receipt = tx.executeForReceipt();
//                        if (receipt != null) {
//                                var newAccountId = receipt.getAccountId();
//                                System.out.println("account = " + newAccountId);
//                        } else {
//                                throw new Exception("Receipt is null");
//                        }
//                } catch (Exception e) {
//                        e.printStackTrace();
//                }
        }
}