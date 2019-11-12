
package com.hedera.cli.hedera.setup;

import java.util.Arrays;
import java.util.List;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.crypto.AccountRecovery;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;

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
        String phrase = inputReader.prompt("24 words phrase", "secret", false);
        List<String> phraseList = accountManager.verifyPhraseList(Arrays.asList(phrase.split(" ")));
        if (phraseList == null)
            return;
        String method = inputReader
                .prompt("Have you migrated your account on Hedera wallet? If migrated, enter `bip`, else enter `hgc`");
        String strMethod = accountManager.verifyMethod(method);
        if (strMethod == null)
            return;

        KeyPair keyPair;
        if ("bip".equals(method)) {
            keyPair = accountRecovery.recoverEDKeypairPostBipMigration(phraseList);
        } else {
            keyPair = accountRecovery.recoverEd25519AccountKeypair(phraseList);
        }

        boolean accountVerified = accountRecovery.verifyAndSaveAccount(accountId, keyPair);
        if (accountVerified) {
            accountRecovery.printKeyPair(keyPair, accountId);
            accountManager.setDefaultAccountId(AccountId.fromString(accountId), keyPair);
        } else {
            shellHelper.printError("Error in verifying that accountId and recovery words match");
        }
    }

}
