package com.hedera.cli.hedera.crypto;

import java.util.Arrays;
import java.util.HashMap;
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
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@NoArgsConstructor
@Getter
@Setter
@Component
@Command(name = "recovery", description = "@|fg(225) Recovers a Hedera account via the 24 recovery words.|@", helpCommand = true)
public class AccountRecovery implements Runnable {

    @Spec
    CommandSpec spec;

    @Autowired
    ApplicationContext context;

    @Autowired
    ShellHelper shellHelper;

    @Option(names = {"-a", "--accountId"}, description = "Account ID in %nshardNum.realmNum.accountNum format")
    private String accountId;

    @Option(names = {"-m", "--method"}, arity = "1", defaultValue = "bip", description = "Recovers keypair from recovery phrase")
    private String strMethod = "bip";

    private int index = 0;
    private InputReader inputReader;
    private Utils utils;
    private AccountGetInfo accountInfo;
    private Hedera hedera;
    private com.hedera.hashgraph.sdk.account.AccountInfo accountRes;
    private KeyPair keyPair;

    @Override
    public void run() {
        utils = new Utils();
        accountInfo = new AccountGetInfo();
        hedera = new Hedera(context);
        shellHelper.print("Recovering accountID in the format of 0.0.xxxx" + accountId);
        strMethod = inputReader.prompt("Have you updated your account on Hedera wallet? If updated, enter `bip`, else enter `hgc`");
        String phrase = inputReader.prompt("24 words phrase", "secret", false);
        List<String> phraseList = Arrays.asList(phrase.split(" "));
        // recover key from phrase

        if (strMethod.equals("bip")) {
            keyPair = recoverEDKeypairPostBipMigration(phraseList);
            verifyAndSaveAccount();
        } else if (strMethod.equals("hgc")) {
            keyPair = recoverEd25519AccountKeypair(phraseList);
            verifyAndSaveAccount();
        } else {
            shellHelper.printError("Method must either been hgc or bip");
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
            var client = hedera.createHederaClient()
                    .setOperator(hedera.getOperatorId(), hedera.getOperatorKey());
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