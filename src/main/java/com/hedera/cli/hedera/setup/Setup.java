
package com.hedera.cli.hedera.setup;

import java.util.List;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.crypto.AccountRecovery;
import com.hedera.cli.hedera.crypto.InputPrompts;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.shell.ShellHelper;

import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
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
    private InputPrompts inputPrompts;

    @Autowired
    private Hedera hedera;

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
        if (accountId == null) return;

        boolean isWords = inputPrompts.keysOrPassphrasePrompt(inputReader);
        if (!isWords) {
            ed25519PrivateKey = inputPrompts.ed25519PrivKeysPrompt(inputReader, accountId, shellHelper);
            accountRecovery.verifyWithPrivKey(ed25519PrivateKey, accountId);
            return;
        }
        List<String> phraseList = inputPrompts.passphrasePrompt(inputReader, accountManager);
        if (phraseList.isEmpty()) return;
        String method = inputPrompts.methodPrompt(inputReader, accountManager);
        if (StringUtil.isNullOrEmpty(method)) return;
        KeyPair keyPairRecovered = accountRecovery.recoverKeypairWithPassphrase(phraseList, method, accountId);
        accountRecovery.verifyWithKeyPair(keyPairRecovered, accountId);
    }
}
