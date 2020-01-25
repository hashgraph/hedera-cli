
package com.hedera.cli.hedera.crypto;

import java.util.List;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.keygen.CryptoUtils;
import com.hedera.cli.hedera.keygen.HGCSeed;
import com.hedera.cli.hedera.keygen.KeyGeneration;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.hedera.setup.Setup;
import com.hedera.cli.services.HederaGrpc;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;

import org.hjson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ShellHelper shellHelper;

    @Autowired
    private Hedera hedera;

    @Autowired
    private Setup setup;

    @Autowired
    private HederaGrpc hederaGrpc;

    @Spec
    private CommandSpec spec;

    // @Option(names = {"-r", "--record"}, description = "Generates a record that
    // lasts 25hrs")
    // private boolean generateRecord = false;

    @Option(names = { "-b",
            "--balance" }, required = true, description = "Initial balance of new account created in hbars")
    private long initBal = 0;

    @Option(names = { "-k",
            "--keygen" }, description = "Default generates a brand new key pair associated with account creation"
                    + "%n@|bold,underline Usage:|@%n" + "@|fg(yellow) account create -b 100000000|@")
    private boolean keyGen;

    @Option(names = { "-pk", "--publicKey" }, description = "Associates a public key for an account creation"
            + "%n@|bold,underline Usage:|@%n" + "@|fg(yellow) account create -b 100000000 -pk|@")
    private String privateKeyFromArgs;

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
            Ed25519PublicKey newPublicKey = Ed25519PublicKey.fromString(keypair.getPublicKeyEncodedHex());
            accountId = hederaGrpc.createNewAccount(newPublicKey, hedera.getOperatorId(), initBal);
            if (accountId != null && keypair != null) {
                account = hederaGrpc.printAccount(accountId.toString(), keypair.getPrivateKeyHex(),
                        keypair.getPublicKeyHex());
                hedera.getAccountManager().setDefaultAccountId(accountId, keypair);
            }

        } else {
            // Else keyGen always set to false and read from default.txt which contains
            // operator keys
            Ed25519PrivateKey operatorPrivateKey = hedera.getOperatorKey();
            Ed25519PublicKey operatorPublicKey = operatorPrivateKey.getPublicKey();
            accountId = hederaGrpc.createNewAccount(operatorPublicKey, hedera.getOperatorId(), initBal);
            if (operatorPrivateKey != null && operatorPublicKey != null && accountId != null) {
                // save to local disk
                String privateKey = operatorPrivateKey.toString();
                String publicKey = operatorPublicKey.toString();
                account = hederaGrpc.printAccount(accountId.toString(), privateKey, publicKey);
                hedera.getAccountManager().setDefaultAccountId(accountId, Ed25519PrivateKey.fromString(privateKey));
            }
        }
    }

    private void setMinimum(long min) {
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