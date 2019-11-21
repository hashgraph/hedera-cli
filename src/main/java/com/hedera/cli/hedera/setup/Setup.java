
package com.hedera.cli.hedera.setup;

import java.util.Arrays;
import java.util.List;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.crypto.AccountRecovery;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.shell.ShellHelper;

import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Getter
@Component
@Command(name = "setup", description = "")
public class Setup implements Runnable {

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private InputReader inputReader;

    @Autowired
    private AccountRecovery accountRecovery;

    @Autowired
    private Hedera hedera;

    private List<String> phraseList;
    private Ed25519PrivateKey ed25519PrivateKey;
    private KeyPair keyPair;

    public void help() {
        CommandLine.usage(this, System.out);
    }

    @Override
    public void run() {
        shellHelper.print("Start the setup process");
        AccountManager accountManager = hedera.getAccountManager();
        String accountIdInString = inputReader
                .prompt("account ID in the format of 0.0.xxxx that will be used as default operator");
        String accountId = accountManager.verifyAccountId(accountIdInString);
        if (accountId == null)
            return;

        boolean isWords = accountRecovery.promptPreview(inputReader);
        if (isWords) {
            String phrase = inputReader.prompt("24 words phrase", "secret", false);
            phraseList = accountManager.verifyPhraseList(Arrays.asList(phrase.split(" ")));
            if (phraseList == null)
                return;
        } else {
            String privateKeyStr = inputReader.prompt("Enter the private key of account " + accountId, "secret", false);
            try {
                ed25519PrivateKey = Ed25519PrivateKey.fromString(privateKeyStr);
            } catch (Exception e) {
                shellHelper.printError("Private key is not in the right ED25519 string format");
                return;
            }
        }

        String method = inputReader
                .prompt("Have you migrated your account on Hedera wallet? If migrated, enter `bip`, else enter `hgc`");
        String strMethod = accountManager.verifyMethod(method);
        if (strMethod == null)
            return;

        if ("bip".equals(method)) {
            if (isWords) {
                keyPair = accountRecovery.recoverEDKeypairPostBipMigration(phraseList);
                accountRecovery.verifyAndSaveWithKeyPair(keyPair, accountId);
            } else {
                accountRecovery.verifyAndSaveWithPrivKey(ed25519PrivateKey, accountId);
            }
        }

        if ("hgc".equals(method)) {
            if (isWords) {
                keyPair = accountRecovery.recoverEd25519AccountKeypair(phraseList);
                accountRecovery.verifyAndSaveWithKeyPair(keyPair, accountId);
            } else {
                accountRecovery.verifyAndSaveWithPrivKey(ed25519PrivateKey, accountId);
            }
        }
    }
}
