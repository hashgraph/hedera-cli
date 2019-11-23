package com.hedera.cli.hedera.crypto;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.bip39.Mnemonic;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicChecksumException;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicLengthException;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicWordException;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.hedera.keygen.CryptoUtils;
import com.hedera.cli.hedera.keygen.EDKeyPair;
import com.hedera.cli.hedera.keygen.EDBip32KeyChain;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.DataDirectory;
import com.hedera.cli.models.RecoveredAccountModel;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.AccountInfo;
import com.hedera.hashgraph.sdk.account.AccountInfoQuery;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@NoArgsConstructor
@Getter
@Setter
@Component
@Command(name = "recovery", description = "@|fg(225) Recovers a Hedera account via the 24 recovery words.|@", helpCommand = true)
public class AccountRecovery implements Runnable, Operation {

    @Autowired
    private Hedera hedera;

    @Autowired
    private DataDirectory dataDirectory;

    @Autowired
    private AccountManager accountManager;

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private InputReader inputReader;

    @Parameters(index = "0", description = "Hedera account in the format shardNum.realmNum.accountNum"
            + "%n@|bold,underline Usage:|@%n" + "@|fg(yellow) account recovery 0.0.1003|@")
    private String accountId;

    private int index = 0;
    private AccountInfo accountInfo;
    private boolean accountRecovered;
    private List<String> phraseList;
    private KeyPair keyPair;

    @NonNull
    private Ed25519PrivateKey ed25519PrivateKey;

    @Override
    public void run() {
        shellHelper.printInfo("Start the recovery process");
        String verifiedAccountId = accountManager.verifyAccountId(accountId);
        if (verifiedAccountId == null)
            return;

        boolean isWords = promptPreview(inputReader);
        if (isWords) {
            phraseList = phraseListFromRecoveryWordsPrompt(inputReader, accountManager);
            if (phraseList.isEmpty()) return;
        } else {
            ed25519PrivateKey = ed25519PrivateKeyFromKeysPrompt(inputReader, accountId, shellHelper);
        }

        String method = methodFromMethodPrompt(inputReader, accountManager);
        if (StringUtil.isNullOrEmpty(method)) return;
        if (isBip(method)) {
            keyPair = recoverEDKeypairPostBipMigration(phraseList);
        } else {
            keyPair = recoverEd25519AccountKeypair(phraseList);
        }
        recoverWithMethod(ed25519PrivateKey, accountId, isWords, keyPair);
    }

    public boolean isBip(String method) {
        return method.equalsIgnoreCase("bip");
    }

    public void recoverWithMethod(Ed25519PrivateKey ed25519PrivateKey, String accountId,
                                  boolean isWords, KeyPair keyPair) {
        if (isWords) {
            recoverUsingKeyPair(keyPair, accountId);
        } else {
            recoverUsingPrivKey(ed25519PrivateKey, accountId);
        }
    }

    public void recoverUsingKeyPair(KeyPair keyPair, String accountId) {
        if(verifyAndSaveWithKeyPair(keyPair, accountId)) {
            printKeyPair(keyPair, accountId);
        }
    }

    public void recoverUsingPrivKey(Ed25519PrivateKey ed25519PrivateKey, String accountId) {
        if (verifyAndSaveWithPrivKey(ed25519PrivateKey, accountId)) {
            printKeyPairWithPrivKey(ed25519PrivateKey, accountId);
        }
    }

    public List<String> phraseListFromRecoveryWordsPrompt(InputReader inputReader, AccountManager accountManager) {
        String phrase = inputReader.prompt("24 words phrase", "secret", false);
        phraseList = accountManager.verifyPhraseList(Arrays.asList(phrase.split(" ")));
        return phraseList;
    }

    public Ed25519PrivateKey ed25519PrivateKeyFromKeysPrompt(InputReader inputReader, String accountId, ShellHelper shellHelper) {
        String privateKeyStr = inputReader.prompt("Enter the private key of account " + accountId, "secret", false);
        try {
            ed25519PrivateKey = Ed25519PrivateKey.fromString(privateKeyStr);
        } catch (Exception e) {
            shellHelper.printError("Private key is not in the right ED25519 string format");
        }
        return ed25519PrivateKey;
    }

    public String methodFromMethodPrompt(InputReader inputReader, AccountManager accountManager) {
        String method = inputReader
                .prompt("Have you migrated your account on Hedera wallet? If migrated, enter `bip`, else enter `hgc`");
        return accountManager.verifyMethod(method);
    }

    public boolean verifyAndSaveWithKeyPair(KeyPair keypair, String accountId) {
        Ed25519PrivateKey ed25519PrivateKey = Ed25519PrivateKey.fromString(keypair.getPrivateKeyHex());
        return verifyAndSaveWithPrivKey(ed25519PrivateKey, accountId);
    }

    public boolean verifyAndSaveWithPrivKey(Ed25519PrivateKey ed25519PrivateKey, String accountId) {
        accountRecovered = verifyAccountExistsInHedera(accountId, ed25519PrivateKey.toString());
        if (!accountRecovered) {
            shellHelper.printError("Error in recovering account");
            return false;
        }
        hedera.accountManager.setDefaultAccountId(AccountId.fromString(accountId), ed25519PrivateKey);
        return true;
    }

    public boolean promptPreview(InputReader inputReader) {
        String choice = inputReader.prompt("Recover account using 24 words or keys? Enter words/keys");
        return choice.equalsIgnoreCase("words");
    }

    public boolean verifyAccountExistsLocally(AccountInfo accountInfo, String accountId) {
        boolean accountIdMatches = accountInfo.getAccountId().equals(AccountId.fromString(accountId));
        if (accountIdMatches) {
            if (!retrieveIndex()) {
                // Check if account already exists in index.txt
                shellHelper.printSuccess("Account recovered and verified with Hedera");
                accountRecovered = true;
            } else {
                shellHelper.printError("This account already exists!");
                accountRecovered = false;
            }
        }
        return accountRecovered;
    }

    public boolean verifyAccountExistsInHedera(String accountId, String privateKeyString) {
        boolean accountExistsInHedera;
        try {
            accountInfo = getAccountInfoWithPrivKey(hedera, accountId,
                    Ed25519PrivateKey.fromString(privateKeyString));
            if (accountInfo == null) return false;
            accountExistsInHedera = verifyAccountExistsLocally(accountInfo, accountId);
        } catch (Exception e) {
            shellHelper.printError("Error in verifying accountID and recovery words");
            accountExistsInHedera = false;
        }
        return accountExistsInHedera;
    }

    public AccountInfo getAccountInfoWithPrivKey(Hedera hedera, String accountId, Ed25519PrivateKey accPrivKey) {
        try (Client client = hedera.createHederaClientWithoutSettingOperator()) {
            client.setOperator(AccountId.fromString(accountId), accPrivKey);
            AccountInfoQuery q;
            q = new AccountInfoQuery(client).setAccountId(AccountId.fromString(accountId));
            accountInfo = q.execute();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            // do nothing
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
            return null;
        }
        return accountInfo;
    }

    public boolean retrieveIndex() {
        String pathToIndexTxt = accountManager.pathToIndexTxt();
        boolean accountExistsInIndex = false;
        Map<String, String> readingIndexAccount = dataDirectory.readIndexToHashmap(pathToIndexTxt);
        if (readingIndexAccount == null) {
            return false;
        }
        for (Map.Entry<String, String> entry : readingIndexAccount.entrySet()) {
            if (entry.getKey().equals(accountId)) {
                accountExistsInIndex = true;
            }
        }
        return accountExistsInIndex;
    }

    public KeyPair recoverEd25519AccountKeypair(List<String> phraseList) {
        KeyPair keypair = null;
        Mnemonic mnemonic = new Mnemonic();
        try {
            byte[] entropy = mnemonic.toEntropy(phraseList);
            byte[] seed = CryptoUtils.deriveKey(entropy, index, 32);
            keypair = new EDKeyPair(seed);
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

    public void printKeyPairWithPrivKey(Ed25519PrivateKey ed25519PrivateKey, String accountId) {
        RecoveredAccountModel recoveredAccountModel = new RecoveredAccountModel();
        recoveredAccountModel.setAccountId(accountId);
        recoveredAccountModel.setPrivateKey(ed25519PrivateKey.toString().substring(32));
        recoveredAccountModel.setPublicKey(ed25519PrivateKey.getPublicKey().toString().substring(24));
        recoveredAccountModel.setPrivateKeyEncoded(ed25519PrivateKey.toString());
        recoveredAccountModel.setPublicKeyEncoded(ed25519PrivateKey.getPublicKey().toString());
        printRecoveredAccount(recoveredAccountModel);
    }

    public void printKeyPair(KeyPair keypair, String accountId) {
        RecoveredAccountModel recoveredAccountModel = new RecoveredAccountModel();
        recoveredAccountModel.setAccountId(accountId);
        recoveredAccountModel.setPrivateKey(keypair.getPrivateKeyHex());
        recoveredAccountModel.setPublicKey(keypair.getPublicKeyHex());
        recoveredAccountModel.setPrivateKeyEncoded(keypair.getPrivateKeyEncodedHex());
        recoveredAccountModel.setPublicKeyEncoded(keypair.getPublicKeyEncodedHex());
        recoveredAccountModel.setPrivateKeyBrowserCompatible(keypair.getSeedAndPublicKeyHex());
        printRecoveredAccount(recoveredAccountModel);
    }

    private void printRecoveredAccount(RecoveredAccountModel recoveredAccountModel) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            shellHelper.printSuccess(ow.writeValueAsString(recoveredAccountModel));
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }
}