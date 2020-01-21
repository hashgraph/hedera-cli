package com.hedera.cli.hedera.crypto;

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
    private InputPrompts inputPrompts;

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

    @NonNull
    private Ed25519PrivateKey ed25519PrivateKey;

    @Override
    public void run() {
        shellHelper.printInfo("Start the recovery process");
        String verifiedAccountId = accountManager.verifyAccountId(accountId);
        if (verifiedAccountId == null) return;

        boolean isWords = inputPrompts.keysOrPassphrasePrompt(inputReader);
        if (!isWords) {
            ed25519PrivateKey = inputPrompts.ed25519PrivKeysPrompt(inputReader, accountId, shellHelper);
            verifyWithPrivKey(ed25519PrivateKey, accountId);
            return;
        }
        List<String> phraseList = inputPrompts.passphrasePrompt(inputReader, accountManager);
        if (phraseList.isEmpty()) return;
        String method = inputPrompts.methodPrompt(inputReader, accountManager);
        if (StringUtil.isNullOrEmpty(method)) return;
        KeyPair keyPairRecovered = recoverKeypairWithPassphrase(phraseList, method, accountId);
        verifyWithKeyPair(keyPairRecovered, accountId);
    }

    public KeyPair recoverKeypairWithPassphrase(List<String> phraseList, String method, String accountId) {
        KeyPair keyPair;
        if (accountManager.isBip(method)) {
            keyPair = recoverEDKeypairPostBipMigration(phraseList);
        } else {
            keyPair = recoverEd25519AccountKeypair(phraseList);
        }
        return keyPair;
    }

    public void verifyWithKeyPair(KeyPair keyPair, String accountId) {
        if (verifyAndSaveWithKeyPair(keyPair, accountId)) {
            printKeyPair(keyPair, accountId);
        }
    }

    public void verifyWithPrivKey(Ed25519PrivateKey ed25519PrivateKey, String accountId) {
        if (verifyAndSaveWithPrivKey(ed25519PrivateKey, accountId)) {
            printKeyPairWithPrivKey(ed25519PrivateKey, accountId);
        }
    }

    public boolean verifyAndSaveWithKeyPair(KeyPair keypair, String accountId) {
        Ed25519PrivateKey ed25519PrivateKey1 = Ed25519PrivateKey.fromString(keypair.getPrivateKeyHex());
        return verifyAndSaveWithPrivKey(ed25519PrivateKey1, accountId);
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

    public boolean verifyAccountExistsLocally(AccountInfo accountInfo, String accountId) {
        boolean accountIdMatches = accountInfo.accountId.equals(AccountId.fromString(accountId));
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
            q = new AccountInfoQuery()
                .setAccountId(AccountId.fromString(accountId));
            accountInfo = q.execute(client);
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
        recoveredAccountModel.setPublicKey(ed25519PrivateKey.publicKey.toString().substring(24));
        recoveredAccountModel.setPrivateKeyEncoded(ed25519PrivateKey.toString());
        recoveredAccountModel.setPublicKeyEncoded(ed25519PrivateKey.publicKey.toString());
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