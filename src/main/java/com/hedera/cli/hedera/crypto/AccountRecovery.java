package com.hedera.cli.hedera.crypto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.bip39.Mnemonic;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicChecksumException;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicLengthException;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicWordException;
import com.hedera.cli.hedera.keygen.CryptoUtils;
import com.hedera.cli.hedera.keygen.EDBip32KeyChain;
import com.hedera.cli.hedera.keygen.EDKeyPair;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.hedera.utils.AccountUtils;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.hedera.utils.Utils;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

@NoArgsConstructor
@Setter
@Component
@Command(name = "recovery", description = "@|fg(225) Recovers a Hedera account via the 24 recovery words.|@", helpCommand = true)
public class AccountRecovery implements Runnable {

    @Spec
    CommandSpec spec;

    @Autowired
    ApplicationContext context;

    @Option(names = {"-a", "--account-id"}, description = "Account ID in %nshardNum.realmNum.accountNum format")
    private String accountId;

    @Option(names = {"-m", "--method"}, arity = "1", defaultValue = "bip", description = "Recovers keypair from recovery phrase")
    private String strMethod = "bip";

    private int index = 0;
    private InputReader inputReader;
    private Utils utils;
    private AccountInfo accountInfo;
    private Hedera hedera;
    private com.hedera.hashgraph.sdk.account.AccountInfo accountRes;
    private KeyPair keyPair;

    @Override
    public void run() {
        utils = new Utils();
        accountInfo = new AccountInfo();
        hedera = new Hedera(context);
        System.out.println("Recovering accountID in the format of 0.0.xxxx" + accountId);
        strMethod = inputReader.prompt("Have you updated your account on Hedera wallet? If updated, enter `bip`, else enter `hgc`");
        String phrase = inputReader.prompt("24 words phrase", "secret", false);
        List<String> phraseList = Arrays.asList(phrase.split(" "));
        System.out.println(phraseList);
        // recover key from phrase

        if (strMethod.equals("bip")) {
            keyPair = recoverEDKeypairPostBipMigration(phraseList);
            verifyAndSaveAccount();
        } else if (strMethod.equals("hgc")) {
            keyPair = recoverEd25519AccountKeypair(phraseList);
            verifyAndSaveAccount();
        } else {
            throw new ParameterException(spec.commandLine(), "Method must either been hgc or bip");
        }
    }

    public void verifyAndSaveAccount() {
        try {
            accountRes = accountInfo.getAccountInfo(hedera, accountId, Ed25519PrivateKey.fromString(keyPair.getPrivateKeyHex()));
            if (accountRes.getAccountId().equals(AccountId.fromString(accountId))) {
                // Check if account already exists in index.txt
                if (!retrieveIndex()) {
                    System.out.println("Account recovered and saved in ~/.hedera");
                    printKeyPair(keyPair);
                    utils.saveAccountsToJson(keyPair, AccountId.fromString(accountId));
                }
                System.out.println("This account already exists!");
            }
        } catch (Exception e) {
            System.out.println("***** AccountId and Recovery words do not match *****");
        }
    }

    public boolean retrieveIndex() {
        DataDirectory dataDirectory = new DataDirectory();
        AccountUtils accountUtils = new AccountUtils();
        String pathToIndexTxt = accountUtils.pathToAccountsFolder() + "index.txt";
        boolean accountExists = false;
        HashMap<String, String> readingIndexAccount = dataDirectory.readFileHashmap(pathToIndexTxt);
        for (Map.Entry<String, String> entry : readingIndexAccount.entrySet()) {
            if (entry.getKey().equals(accountId)) {
                accountExists = true;
            }
        }
        return accountExists;
    }

    public KeyPair recoverEd25519AccountKeypair(List<String> phraseList) {
        KeyPair keyPair = null;
        Mnemonic mnemonic = new Mnemonic();
        try {
            byte[] entropy = mnemonic.toEntropy(phraseList);
            byte[] seed = CryptoUtils.deriveKey(entropy, index, 32);
            keyPair = new EDKeyPair(seed);
            printKeyPair(keyPair);
        } catch (MnemonicLengthException | MnemonicWordException | MnemonicChecksumException e) {
            e.printStackTrace();
        }
        return keyPair;
    }

    public KeyPair recoverEDKeypairPostBipMigration(List<String> phraseList) {
        EDBip32KeyChain edBip32KeyChain = new EDBip32KeyChain();
        return edBip32KeyChain.keyPairFromWordList(0, phraseList);
    }

    public void printKeyPair(KeyPair keyPair) {
        System.out.println("priv key encoded: " + keyPair.getPrivateKeyEncodedHex());
        System.out.println("pub key encoded: " + keyPair.getPublicKeyEncodedHex());
        System.out.println("priv key hex: " + keyPair.getPrivateKeyHex());
        System.out.println("pub key hex: " + keyPair.getPublicKeyHex());
        System.out.println("browser compatible priv key " + keyPair.getSeedAndPublicKeyHex());
    }
}