
package com.hedera.cli.hedera.setup;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.crypto.AccountRecovery;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.models.HederaAccount;
import com.hedera.cli.models.RecoveredAccountModel;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.hjson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Log
@Getter
@Setter
@Component
@Command(name = "setup", description = "")
public class Setup implements Runnable {

    @Autowired
    private AccountRecovery accountRecovery;

    @Autowired
    private DataDirectory dataDirectory;

    @Autowired
    private RandomNameGenerator randomNameGenerator;

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public void handle(InputReader inputReader, ShellHelper shellHelper) {
        shellHelper.print("Start the setup process");
        String strMethod = inputReader
                .prompt("Have you migrated your account on Hedera wallet? If migrated, enter `bip`, else enter `hgc`");
        String accountId = inputReader
                .prompt("account ID in the format of 0.0.xxxx that will be used as default operator");
        String phrase = inputReader.prompt("24 words phrase", "secret", false);
        List<String> phraseList = Arrays.asList(phrase.split(" "));
        shellHelper.print(String.valueOf(phraseList));
        // recover key from phrase
        KeyPair keyPair;
        if (phraseListSize(phraseList)) {
            if ("bip".equals(strMethod)) {
                keyPair = accountRecovery.recoverEDKeypairPostBipMigration(phraseList);
                printKeyPair(keyPair, accountId, shellHelper);
                JsonObject account = addAccountToJson(accountId, keyPair);
                saveToJson(accountId, account);
            } else if ("hgc".equals(strMethod)) {
                keyPair = accountRecovery.recoverEd25519AccountKeypair(phraseList);
                printKeyPair(keyPair, accountId, shellHelper);
                JsonObject account = addAccountToJson(accountId, keyPair);
                saveToJson(accountId, account);
            } else {
                shellHelper.printError("Method must either been bip or hgc");
            }
        } else {
            shellHelper.printError("Recovery words must contain 24 words");
        }
    }

    public boolean phraseListSize(List<String> phraseList) {
        return phraseList.size() == 24;
    }

    public JsonObject addAccountToJson(String accountId, KeyPair keyPair) {
        JsonObject account = new JsonObject();
        account.add("accountId", accountId);
        account.add("privateKey", keyPair.getPrivateKeyHex());
        account.add("publicKey", keyPair.getPublicKeyHex());
        // account.add("privateKey_ASN1", keyPair.getPrivateKeyEncodedHex());
        // account.add("publicKey_ASN1", keyPair.getPublicKeyEncodedHex());
        return account;
    }

    public JsonObject addAccountToJsonWithPrivateKey(String accountId, Ed25519PrivateKey privateKey) {
        JsonObject account = new JsonObject();
        account.add("accountId", accountId);
        account.add("privateKey", privateKey.toString());
        account.add("publicKey", privateKey.getPublicKey().toString());
        return account;
    }

    public void saveToJson(String accountId, JsonObject account) {
        // ~/.hedera/[network_name]/accounts/[account_name].json
        String fileName = randomNameGenerator.getRandomName();
        String fileNameWithExt = fileName + ".json";
        String networkName = dataDirectory.readFile("network.txt");
        String pathToAccountsFolder = networkName + File.separator + "accounts" + File.separator;
        String pathToAccountFile = pathToAccountsFolder + fileNameWithExt;

        String pathToDefaultTxt = pathToAccountsFolder + "default.txt";
        String pathToIndexTxt = pathToAccountsFolder + "index.txt";

        HashMap<String, String> mHashMap = new HashMap<>();
        mHashMap.put(accountId, fileName);
        ObjectMapper mapper = new ObjectMapper();

        try {
            // create the account json and write it to disk
            Object jsonObject = mapper.readValue(account.toString(), HederaAccount.class);
            String accountValue = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
            dataDirectory.writeFile(pathToAccountFile, accountValue);
            // default account
            dataDirectory.readFile(pathToDefaultTxt, fileName + ":" + accountId);
            // current account
            // write to index if account does not yet exist in index
            dataDirectory.readWriteToIndex(pathToIndexTxt, mHashMap);
        } catch (Exception e) {
            log.info("did not save json");
        }
    }

    public void printKeyPair(KeyPair keyPair, String accountId, ShellHelper shellHelper) {
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
