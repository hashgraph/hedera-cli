
package com.hedera.cli.hedera.crypto;

import java.time.Duration;
import java.util.List;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.keygen.CryptoUtils;
import com.hedera.cli.hedera.keygen.HGCSeed;
import com.hedera.cli.hedera.keygen.KeyGeneration;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.hedera.setup.Setup;
import com.hedera.cli.hedera.utils.AccountUtils;
import com.hedera.cli.hedera.utils.Utils;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.account.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;

import org.hjson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

@Component
@Command(name = "create",
        description = "@|fg(225) Generates a new Ed25519 Keypair compatible with java and wallet,"
                + "%ntogether with 24 recovery words (bip compatible),"
                + "%nCreates a new Hedera account and "
                + "%nReturns an accountID in the form of shardNum.realmNum.accountNum.|@", helpCommand = true)
public class AccountCreate implements Runnable {

    @Autowired
    ApplicationContext context;

    @Spec
    CommandSpec spec;

    @Option(names = {"-r", "--record"}, description = "Generates a record that lasts 25hrs")
    private boolean generateRecord = false;

    @Option(names = {"-b", "--balance"}, description = "Initial balance of new account created in hbars")
    private int initBal = 0;

    private void setMinimum(int min) {
        if (min < 0) {
            throw new ParameterException(spec.commandLine(), "Minimum must be a positive integer");
        }
        initBal = min;
    }

    @Option(names = {"-k", "--keygen"}, description = "Default generates a brand new key pair associated with account creation"
            + "%n@|bold,underline Usage:|@")
    private boolean keyGen = true;

    @Option(names = {"-m", "--method"}, defaultValue = "bip", description = "Default set for passphrases and keypairs that are bip compatible"
            + "%n@|bold,underline Usage:|@"
            + "%n@|fg(yellow) account create -b=100000000|@")
    private String strMethod = "bip";

    private AccountId accountID;

    private AccountUtils accountUtils = new AccountUtils();

    @Override
    public void run() {

        Hedera hedera = new Hedera(context);

        setMinimum(initBal);

        if (keyGen) {
            // If keyGen via args is set to true, generate new keys
            KeyGeneration keyGeneration = new KeyGeneration(strMethod);
            HGCSeed hgcSeed = new HGCSeed((CryptoUtils.getSecureRandomData(32)));
            List<String> mnemonic = keyGeneration.generateMnemonic(hgcSeed);

            KeyPair keypair = keyGeneration.generateKeysAndWords(hgcSeed, mnemonic);
            System.out.println("AccountCreate subcommand");
            var newKey = Ed25519PrivateKey.fromString(keypair.getPrivateKeyEncodedHex());
            var newPublicKey = Ed25519PublicKey.fromString(keypair.getPublicKeyEncodedHex());
            accountID = createNewAccount(newKey, newPublicKey);
            System.out.println("AccountID = " + accountID);
            System.out.println("mnemonic = " + mnemonic);
            // save to local disk
            Utils utils = new Utils();
            utils.saveAccountsToJson(keypair, AccountId.fromString(accountID.toString()));
        } else {
            // Else keyGen always set to false and read from default.txt which contains operator keys
            var origKey = hedera.getOperatorKey();
            var origPublicKey = origKey.getPublicKey();
            accountID = createNewAccount(origKey, origPublicKey);
            // save to local disk
            System.out.println("AccountID = " + accountID);
            JsonObject account = new JsonObject();
            account.add("accountId", accountID.toString());
            account.add("privateKey", accountUtils.retrieveDefaultAccountKeyInHexString());
            account.add("publicKey", accountUtils.retrieveDefaultAccountPublicKeyInHexString());

            System.out.println(account);
            Setup setup = new Setup();
            setup.saveToJson(accountID.toString(), account);
        }
    }

    public AccountId createNewAccount(Ed25519PrivateKey privateKey, Ed25519PublicKey publicKey) {
        System.out.println("private key = " + privateKey);
        System.out.println("public key = " + publicKey);
        AccountId accountId = null;
        Hedera hedera = new Hedera(context);
        var client = hedera.createHederaClient().setMaxTransactionFee(100000000);
        var tx = new AccountCreateTransaction(client)
                // The only _required_ property here is `key`
                .setKey(privateKey.getPublicKey()).setInitialBalance(initBal)
                .setAutoRenewPeriod(Duration.ofSeconds(7890000));

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