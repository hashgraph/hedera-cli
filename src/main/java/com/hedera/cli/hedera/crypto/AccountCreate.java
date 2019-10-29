
package com.hedera.cli.hedera.crypto;

import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.keygen.CryptoUtils;
import com.hedera.cli.hedera.keygen.HGCSeed;
import com.hedera.cli.hedera.keygen.KeyGeneration;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.hedera.setup.Setup;
import com.hedera.cli.hedera.utils.Utils;
import com.hedera.cli.models.HederaAccount;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.account.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;

import org.hjson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

@Getter
@Setter
@Component
@Command(name = "create", separator = " ", description = "@|fg(225) Generates a new Ed25519 Keypair compatible with java and wallet,"
        + "%ntogether with 24 recovery words (bip compatible)," + "%nCreates a new Hedera account and "
        + "%nReturns an accountID in the form of shardNum.realmNum.accountNum.|@", helpCommand = true)
public class AccountCreate implements Runnable, Operation {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private Hedera hedera;

    @Autowired
    private Setup setup;

    @Autowired
    private Utils utils;

    @Spec
    private CommandSpec spec;

    // @Option(names = {"-r", "--record"}, description = "Generates a record that
    // lasts 25hrs")
    // private boolean generateRecord = false;

    @Option(names = { "-b",
            "--balance" }, required = true, description = "Initial balance of new account created in hbars")
    private int initBal = 0;

    @Option(names = { "-k",
            "--keygen" }, description = "Default generates a brand new key pair associated with account creation"
                    + "%n@|bold,underline Usage:|@%n" + "@|fg(yellow) account create -b 100000000|@")
    private boolean keyGen;

    private String strMethod = "bip";

    private AccountId accountId;

    private JsonObject account;

    @Override
    public void run() {

        setMinimum(initBal);
        if (keyGen) {
            // If keyGen via args is set to true, generate new keys
            KeyGeneration keyGeneration = new KeyGeneration(strMethod);
            HGCSeed hgcSeed = new HGCSeed((CryptoUtils.getSecureRandomData(32)));
            List<String> mnemonic = keyGeneration.generateMnemonic(hgcSeed);
            KeyPair keypair = keyGeneration.generateKeysAndWords(hgcSeed, mnemonic);
            var newPublicKey = Ed25519PublicKey.fromString(keypair.getPublicKeyEncodedHex());
            accountId = createNewAccount(newPublicKey, hedera.getOperatorId());
            account = printAccount(accountId.toString(), keypair.getPrivateKeyHex(), keypair.getPublicKeyHex());
            hedera.accountManager.setDefaultAccountId(accountId, keypair);
        } else {
            // Else keyGen always set to false and read from default.txt which contains
            // operator keys
            var operatorPrivateKey = hedera.getOperatorKey();
            var operatorPublicKey = operatorPrivateKey.getPublicKey();
            accountId = createNewAccount(operatorPublicKey, hedera.getOperatorId());
            // save to local disk
            String privateKey = operatorPrivateKey.toString();
            String publicKey = operatorPublicKey.toString();
            account = printAccount(accountId.toString(), privateKey, publicKey);
            hedera.accountManager.setDefaultAccountId(accountId, Ed25519PrivateKey.fromString(privateKey));
        }
    }

    public JsonObject printAccount(String accountId, String privateKey, String publicKey) {
        JsonObject account1 = new JsonObject();
        account1.add("accountId", accountId);
        account1.add("privateKey", privateKey);
        account1.add("publicKey", publicKey);
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object jsonObject = mapper.readValue(account1.toString(), HederaAccount.class);
            String accountValue = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
            shellHelper.printSuccess(accountValue);
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
        return account1;
    }

    public AccountId createNewAccount(Ed25519PublicKey publicKey, AccountId operatorId) {
        AccountId accountId = null;
        var client = hedera.createHederaClient();
        TransactionId transactionId = new TransactionId(operatorId);
        var tx = new AccountCreateTransaction(client)
                // The only _required_ property here is `key`
                .setTransactionId(transactionId).setKey(publicKey).setInitialBalance(initBal)
                .setAutoRenewPeriod(Duration.ofSeconds(7890000));

        // This will wait for the receipt to become available
        TransactionReceipt receipt;
        try {
            receipt = tx.executeForReceipt();
            if (receipt != null) {
                accountId = receipt.getAccountId();
            } else {
                shellHelper.printError("Receipt is null");
            }
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
        return accountId;
    }

    private void setMinimum(int min) {
        if (min < 0) {
            throw new ParameterException(spec.commandLine(), "Minimum must be a positive integer");
        }
        initBal = min;
    }

    @Override
    public void executeSubCommand(InputReader inputReader, String... args) {
        if (args.length == 0) {
            CommandLine.usage(this, System.out);
        } else {
            try {
                new CommandLine(this).execute(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}