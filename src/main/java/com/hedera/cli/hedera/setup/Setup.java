
package com.hedera.cli.hedera.setup;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.crypto.AccountRecovery;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.models.RecoveredAccountModel;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.AccountInfoQuery;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
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

    public void help() {
        CommandLine.usage(this, System.out);
    }

    @Override
    public void run() {
        shellHelper.print("Start the setup process");
        String accountIdInString = inputReader
                .prompt("account ID in the format of 0.0.xxxx that will be used as default operator");
        String accountId = hedera.accountManager.verifyAccountId(accountIdInString, shellHelper);
        if (accountId == null) return;
        String phrase = inputReader.prompt("24 words phrase", "secret", false);
        List<String> phraseList = hedera.accountManager.verifyPhraseList(Arrays.asList(phrase.split(" ")), shellHelper);
        if (phraseList == null) return;
        String method = inputReader
                .prompt("Have you migrated your account on Hedera wallet? If migrated, enter `bip`, else enter `hgc`");
        String strMethod = hedera.accountManager.verifyMethod(method, shellHelper);
        if (strMethod == null) return;

        if ("bip".equals(method)) {
            KeyPair keypair = accountRecovery.recoverEDKeypairPostBipMigration(phraseList);
            boolean accountVerified = verifyAndSaveAccount(accountId, keypair, shellHelper);
            if (accountVerified) {
                printKeyPair(keypair, accountId, shellHelper);
                hedera.accountManager.setDefaultAccountId(AccountId.fromString(accountId), keypair);
            } else {
                shellHelper.printError("Error in verifying that accountId and recovery words match");
            }
        } else {
            KeyPair keypair = accountRecovery.recoverEd25519AccountKeypair(phraseList, accountId, shellHelper);
            boolean accountVerified = verifyAndSaveAccount(accountId, keypair, shellHelper);
            if (accountVerified) {
                printKeyPair(keypair, accountId, shellHelper);
                hedera.accountManager.setDefaultAccountId(AccountId.fromString(accountId), keypair);
            } else {
                shellHelper.printError("Error in verifying that accountId and recovery words match");
            }
        }
    }

    public boolean verifyAndSaveAccount(String accountId, KeyPair keypair, ShellHelper shellHelper) {
        com.hedera.hashgraph.sdk.account.AccountInfo accountResponse;
        boolean accountVerified = false;
        try {
            accountResponse = getAccountInfoWithPrivKey(accountId, Ed25519PrivateKey.fromString(keypair.getPrivateKeyHex()), shellHelper);
            if (accountResponse.getAccountId().equals(AccountId.fromString(accountId))) {
                // Check if account already exists in index.txt
                shellHelper.printSuccess("Account recovered and saved in ~/.hedera");
                accountVerified = true;
            }
        } catch (Exception e) {
            accountVerified = false;
        }
        return accountVerified;
    }

    public com.hedera.hashgraph.sdk.account.AccountInfo getAccountInfoWithPrivKey(String accountId, Ed25519PrivateKey accPrivKey, ShellHelper shellHelper) {
        com.hedera.hashgraph.sdk.account.AccountInfo accountResponse = null;
        try {
            // check account exists on hedera by hardcoding initial
            // because the application might not have been fully spun up yet.
            hedera.accountManager.setDefaultAccountId(AccountId.fromString(accountId), accPrivKey);
            Client client = hedera.createHederaClient();
            AccountInfoQuery q;
            q = new AccountInfoQuery(client)
                    .setAccountId(AccountId.fromString(accountId));
            accountResponse = q.execute();
            client.close();
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
        return accountResponse;
    }

    public void printKeyPair(KeyPair keypair, String accountId, ShellHelper shellHelper) {
        RecoveredAccountModel recoveredAccountModel = new RecoveredAccountModel();
        recoveredAccountModel.setAccountId(accountId);
        recoveredAccountModel.setPrivateKey(keypair.getPrivateKeyHex());
        recoveredAccountModel.setPublicKey(keypair.getPublicKeyHex());
        recoveredAccountModel.setPrivateKeyEncoded(keypair.getPrivateKeyEncodedHex());
        recoveredAccountModel.setPublicKeyEncoded(keypair.getPublicKeyEncodedHex());
        recoveredAccountModel.setPrivateKeyBrowserCompatible(keypair.getSeedAndPublicKeyHex());
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            shellHelper.print(ow.writeValueAsString(recoveredAccountModel));
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }
}
