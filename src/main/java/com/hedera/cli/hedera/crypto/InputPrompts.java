package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Setter
@Component
public class InputPrompts {

    public boolean keysOrPassphrasePrompt(InputReader inputReader) {
        String choice = inputReader.prompt("Recover account using 24 words or keys? Enter words/keys");
        return choice.equalsIgnoreCase("words");
    }

    public List<String> passphrasePrompt(InputReader inputReader, AccountManager accountManager) {
        String phrase = inputReader.prompt("24 words phrase", "secret", false);
        return accountManager.verifyPhraseList(Arrays.asList(phrase.split(" ")));
    }

    public Ed25519PrivateKey ed25519PrivKeysPrompt(InputReader inputReader, String accountId, ShellHelper shellHelper) {
        Ed25519PrivateKey ed25519PrivateKey = null;
        String privateKeyStr = inputReader.prompt("Enter the private key of account " + accountId, "secret", false);
        try {
            ed25519PrivateKey = Ed25519PrivateKey.fromString(privateKeyStr);
        } catch (Exception e) {
            shellHelper.printError("Private key is not in the right ED25519 string format");
        }
        return ed25519PrivateKey;
    }

    public String methodPrompt(InputReader inputReader, AccountManager accountManager) {
        String method = inputReader
                .prompt("Have you migrated your account on Hedera wallet? If migrated, enter `bip`, else enter `hgc`");
        return accountManager.verifyMethod(method);
    }
}
