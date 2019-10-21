package com.hedera.cli.hedera.crypto;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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
import com.hedera.cli.models.RecoveredAccountModel;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.AccountInfoQuery;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@NoArgsConstructor
@Getter
@Setter
@Component
@Command(name = "recovery", description = "@|fg(225) Recovers a Hedera account via the 24 recovery words.|@", helpCommand = true)
public class AccountRecovery implements Runnable {

    @Spec
    private CommandSpec spec;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private Hedera hedera;

    @Autowired
    private DataDirectory dataDirectory;

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private Utils utils;

    @Autowired
    private ShellHelper shellHelper;

    @Parameters(index = "0", description = "Hedera account in the format shardNum.realmNum.accountNum"
            + "%n@|bold,underline Usage:|@%n"
            + "@|fg(yellow) account recovery 0.0.1003|@")
    private String accountId;

    private String strMethod = "bip";
    private int index = 0;
    private InputReader inputReader;
    private AccountGetInfo accountInfo;
    private com.hedera.hashgraph.sdk.account.AccountInfo accountRes;
    private KeyPair keyPair;

    @Override
    public void run() {
        accountInfo = new AccountGetInfo();
        // hedera = new Hedera(context);
        shellHelper.print("Recovering accountID in the format of 0.0.xxxx" + accountId);
        strMethod = inputReader.prompt("Have you migrated your account on Hedera wallet? If migrated, enter `bip`, else enter `hgc`");
        String phrase = inputReader.prompt("24 words phrase", "secret", false);
        List<String> phraseList = Arrays.asList(phrase.split(" "));
        if (phraseList.size() == 24) {
            // recover key from phrase
            if ("bip".equals(strMethod)) {
                keyPair = recoverEDKeypairPostBipMigration(phraseList);
                verifyAndSaveAccount();
            } else if ("hgc".equals(strMethod)) {
                keyPair = recoverEd25519AccountKeypair(phraseList);
                verifyAndSaveAccount();
            } else {
                shellHelper.printError("Method must either been hgc or bip");
            }
        } else {
            shellHelper.printError("Recovery words must contain 24 words");
        }
    }

    public void verifyAndSaveAccount() {
        try {
            accountRes = getAccountInfoWithPrivKey(hedera, accountId, Ed25519PrivateKey.fromString(keyPair.getPrivateKeyHex()));
            if (accountRes.getAccountId().equals(AccountId.fromString(accountId))) {
                // Check if account already exists in index.txt
                if (!retrieveIndex()) {
                    printKeyPair(keyPair);
                    utils.saveAccountsToJson(keyPair, AccountId.fromString(accountId));
                    shellHelper.printSuccess("Account recovered and saved in ~/.hedera");
                } else {
                    shellHelper.printWarning("This account already exists!");
                }
            }
        } catch (Exception e) {
            shellHelper.printError("AccountId and Recovery words do not match");
        }
    }

    public com.hedera.hashgraph.sdk.account.AccountInfo getAccountInfoWithPrivKey(Hedera hedera, String accountId, Ed25519PrivateKey accPrivKey) {
        try {
            var client = hedera.createHederaClient();
            AccountInfoQuery q;
            q = new AccountInfoQuery(client)
                    .setAccountId(AccountId.fromString(accountId));
            accountRes = q.execute();
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
        return accountRes;
    }

    public boolean retrieveIndex() {
        String pathToIndexTxt = accountUtils.pathToIndexTxt();
        boolean accountExists = false;
        Map<String, String> readingIndexAccount = dataDirectory.readIndexToHashmap(pathToIndexTxt);
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
            shellHelper.printError(e.getMessage());
        }
        return keyPair;
    }

    public KeyPair recoverEDKeypairPostBipMigration(List<String> phraseList) {
        EDBip32KeyChain edBip32KeyChain = new EDBip32KeyChain();
        return edBip32KeyChain.keyPairFromWordList(0, phraseList);
    }

    public void printKeyPair(KeyPair keyPair) {
        RecoveredAccountModel recoveredAccountModel = new RecoveredAccountModel();
        recoveredAccountModel.setAccountId(accountId);
        recoveredAccountModel.setPrivateKey(keyPair.getPrivateKeyHex());
        recoveredAccountModel.setPublicKey(keyPair.getPublicKeyHex());
        recoveredAccountModel.setPrivateKeyEncoded(keyPair.getPrivateKeyEncodedHex());
        recoveredAccountModel.setPublicKeyEncoded(keyPair.getPublicKeyEncodedHex());
        recoveredAccountModel.setPrivateKeyBrowserCompatible(keyPair.getSeedAndPublicKeyHex());
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            shellHelper.print(ow.writeValueAsString(recoveredAccountModel));
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }
}