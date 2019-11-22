
package com.hedera.cli.hedera.setup;

import java.util.List;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.crypto.AccountRecovery;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.shell.ShellHelper;

import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Getter
@Setter
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

    @NonNull
    private Ed25519PrivateKey ed25519PrivateKey;

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
            phraseList = accountRecovery.phraseListFromRecoveryWordsPrompt(inputReader, accountManager);
            if (phraseList.isEmpty()) return;
        } else {
            ed25519PrivateKey = accountRecovery.ed25519PrivateKeyFromKeysPrompt(inputReader, accountId, shellHelper);
        }
        String method = accountRecovery.methodFromMethodPrompt(inputReader, accountManager);
        if (accountRecovery.isBip(method)) {
            accountRecovery.recoverWithBipMethod(phraseList, ed25519PrivateKey, accountId, isWords);
        } else {
            accountRecovery.recoverWithHgcMethod(phraseList, ed25519PrivateKey, accountId, isWords);
        }
    }
}
