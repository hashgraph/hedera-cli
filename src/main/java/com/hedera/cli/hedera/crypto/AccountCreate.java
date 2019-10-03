
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
                + "%ntogether with 24 recovery words (bip39 compatible),"
                + "%nCreates a new Hedera account and "
                + "%nReturns an accountID in the form of shardNum.realmNum.accountNum.|@", helpCommand = true)
public class AccountCreate implements Runnable {

    @Autowired
    ApplicationContext context;

    @Spec
    CommandSpec spec;

    @Option(names = {"-r", "--record"}, description = "Generates a record that lasts 25hrs")
    private boolean generateRecord = false;

    @Option(names = {"-b", "--balance"}, description = "Initial balance of new account created in hbars "
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

    @Option(names = {"-m", "--method"}, description = "Input -m=hgc if passphrases have not been migrated on wallet "
            + "%nor account creations are before 13 September 2019. Input -m=bip if passphrases have been migrated on the wallet,"
            + "%nor account creations are after 13 September 2019")
    private String strMethod = "bip";

    private String setMethod(String method) {
        if (method.equals("bip")) {
            strMethod = method;
        } else if (method.equals("hgc")) {
            strMethod = method;
        } else {
            throw new ParameterException(spec.commandLine(), "Method must either been hgc or bip");
        }
        return strMethod;
    }

    private AccountId accountID;
    private Hedera hedera;
    private AccountUtils accountUtils = new AccountUtils();

    @Override
    public void run() {

        Hedera hedera = new Hedera(context);

        setMinimum(initBal);

        if (keyGen) {
            // If keyGen via args is set to true, generate new keys
            KeyGeneration keyGeneration = new KeyGeneration();
            HGCSeed hgcSeed = new HGCSeed((CryptoUtils.getSecureRandomData(32)));
            List<String> mnemonic = keyGeneration.generateMnemonic(hgcSeed);
            KeyPair keypair = keyGeneration.generateKeysAndWords(hgcSeed, setMethod(strMethod), mnemonic);
            System.out.println("AccountCreate subcommand");
            var newKey = Ed25519PrivateKey.fromString(keypair.getPrivateKeyEncodedHex());
            var newPublicKey = Ed25519PublicKey.fromString(keypair.getPublicKeyEncodedHex());
            accountID = createNewAccount(newKey, newPublicKey);
            System.out.println("AccountID = " + accountID);
            System.out.println("mnemonic = " + mnemonic);
            // save to local disk
            JsonObject account = new JsonObject();
            account.add("accountId", accountID.toString());
            account.add("privateKey", keypair.getPrivateKeyHex());
            account.add("publicKey", keypair.getPublicKeyHex());
            System.out.println(account);
            Setup setup = new Setup();
            setup.saveToJson(accountID.toString(), account);
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
//                account.add("privateKey_ASN1", origKey.toString());
//                account.add("publicKey_ASN1", origPublicKey.toString());
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