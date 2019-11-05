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
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.DataDirectory;
import com.hedera.cli.models.RecoveredAccountModel;
import com.hedera.cli.models.TransactionManager;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.AccountInfo;
import com.hedera.hashgraph.sdk.account.AccountInfoQuery;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@NoArgsConstructor
@Getter
@Setter
@Component
@Command(name = "recovery", description = "@|fg(225) Recovers a Hedera account via the 24 recovery words.|@", helpCommand = true)
public class AccountRecovery implements Runnable, Operation {

    @Spec
    private CommandSpec spec;

    @Autowired
    private Hedera hedera;

    @Autowired
    private DataDirectory dataDirectory;

    @Autowired
    private AccountManager accountManager;

    @Autowired
    private TransactionManager txManager;

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private AccountGetInfo accountGetInfo;

    @Autowired
    private InputReader inputReader;

    @Parameters(index = "0", description = "Hedera account in the format shardNum.realmNum.accountNum"
            + "%n@|bold,underline Usage:|@%n" + "@|fg(yellow) account recovery 0.0.1003|@")
    private String accountId;

    private String strMethod = "bip";
    private int index = 0;
    private AccountInfo accountInfo;
    private KeyPair keypair;

    @Override
    public void run() {
        shellHelper.printInfo("Start the recovery process");
        String verifiedAccountId = accountManager.verifyAccountId(accountId);
        if (verifiedAccountId == null)
            return;
        String phrase = inputReader.prompt("24 words phrase", "secret", false);
        List<String> phraseList = accountManager.verifyPhraseList(Arrays.asList(phrase.split(" ")));
        if (phraseList == null)
            return;
        String method = inputReader
                .prompt("Have you migrated your account on Hedera wallet? If migrated, enter `bip`, else enter `hgc`");
        String strMethod = accountManager.verifyMethod(method);
        if (strMethod == null)
            return;

        if ("bip".equals(method)) {
            KeyPair keypair = recoverEDKeypairPostBipMigration(phraseList);
            boolean accountRecovered = verifyAndSaveAccount(accountId, keypair);
            if (accountRecovered) {
                printKeyPair(keypair, accountId);
                hedera.accountManager.setDefaultAccountId(AccountId.fromString(accountId), keypair);
            } else {
                shellHelper.printError("Error in recovering account");
            }
        } else {
            KeyPair keypair = recoverEd25519AccountKeypair(phraseList, accountId);
            boolean accountRecovered = verifyAndSaveAccount(accountId, keypair);
            if (accountRecovered) {
                printKeyPair(keypair, accountId);
                hedera.accountManager.setDefaultAccountId(AccountId.fromString(accountId), keypair);
            } else {
                shellHelper.printError("Error in recovering account");
            }
        }

    }

    public boolean verifyAndSaveAccount(String accountId, KeyPair keypair) {
        AccountInfo accountInfo;
        boolean accountRecovered;
        try {
            accountInfo = getAccountInfoWithPrivKey(hedera, accountId,
                    Ed25519PrivateKey.fromString(keypair.getPrivateKeyHex()));
            if (accountInfo.getAccountId().equals(AccountId.fromString(accountId)) && !retrieveIndex()) {
                // Check if account already exists in index.txt
                shellHelper.printSuccess("Account recovered and verified with Hedera");
                accountRecovered = true;
            } else {
                shellHelper.printError("This account already exists!");
                accountRecovered = false;
            }
        } catch (Exception e) {
            shellHelper.printError("Error in verifying accountID and recovery words");
            accountRecovered = false;
        }
        return accountRecovered;
    }

    public AccountInfo getAccountInfoWithPrivKey(Hedera hedera, String accountId, Ed25519PrivateKey accPrivKey) {
        try {
            var client = hedera.createHederaClient();
            AccountInfoQuery q;
            q = new AccountInfoQuery(client).setAccountId(AccountId.fromString(accountId));
            accountInfo = q.execute();
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
        return accountInfo;
    }

    public boolean retrieveIndex() {
        String pathToIndexTxt = accountManager.pathToIndexTxt();
        boolean accountExists = false;
        Map<String, String> readingIndexAccount = dataDirectory.readIndexToHashmap(pathToIndexTxt);
        for (Map.Entry<String, String> entry : readingIndexAccount.entrySet()) {
            if (entry.getKey().equals(accountId)) {
                accountExists = true;
            }
        }
        return accountExists;
    }

    public KeyPair recoverEd25519AccountKeypair(List<String> phraseList, String accountId) {
        KeyPair keypair = null;
        Mnemonic mnemonic = new Mnemonic();
        try {
            byte[] entropy = mnemonic.toEntropy(phraseList);
            byte[] seed = CryptoUtils.deriveKey(entropy, index, 32);
            keypair = new EDKeyPair(seed);
            printKeyPair(keypair, accountId);
        } catch (MnemonicLengthException | MnemonicWordException | MnemonicChecksumException e) {
            shellHelper.printError(e.getMessage());
        }
        return keypair;
    }

    public KeyPair recoverEDKeypairPostBipMigration(List<String> phraseList) {
        EDBip32KeyChain edBip32KeyChain = new EDBip32KeyChain();
        return edBip32KeyChain.keyPairFromWordList(0, phraseList);
    }

    @Override
    public void executeSubCommand(InputReader inputReader, String... args) {
        if (args.length == 0) {
            CommandLine.usage(this, System.out);
        } else {
            try {
                this.setInputReader(inputReader);
                new CommandLine(this).execute(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void printKeyPair(KeyPair keypair, String accountId) {
        RecoveredAccountModel recoveredAccountModel = new RecoveredAccountModel();
        recoveredAccountModel.setAccountId(accountId);
        recoveredAccountModel.setPrivateKey(keypair.getPrivateKeyHex());
        recoveredAccountModel.setPublicKey(keypair.getPublicKeyHex());
        recoveredAccountModel.setPrivateKeyEncoded(keypair.getPrivateKeyEncodedHex());
        recoveredAccountModel.setPublicKeyEncoded(keypair.getPublicKeyEncodedHex());
        recoveredAccountModel.setPrivateKeyBrowserCompatible(keypair.getSeedAndPublicKeyHex());
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            shellHelper.printSuccess(ow.writeValueAsString(recoveredAccountModel));
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }
}