package com.hedera.cli.models;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.hjson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class AccountManager {

    @Autowired
    private DataDirectory dataDirectory;

    @Autowired
    private RandomNameGenerator randomNameGenerator;

    @Autowired
    private ShellHelper shellHelper;

    private static final String DEFAULT = "default.txt";
    private static final String PRIVATEKEY = "privateKey";
    private static final String PUBLICKEY = "publicKey";

    public String pathToAccountsFolder() {
        String networkName = dataDirectory.readFile("network.txt");
        return networkName + File.separator + "accounts" + File.separator;
    }

    public String pathToIndexTxt() {
        return pathToAccountsFolder() + "index.txt";
    }

    public String pathToDefaultTxt() {
        return pathToAccountsFolder() + DEFAULT;
    }

    /**
     * Default Account ID found in default.txt
     *
     * @return
     */
    public String[] defaultAccountString() {
        String fileString = dataDirectory.readFile(pathToDefaultTxt());
        return fileString.split(":");
    }

    public JsonObject createAccountJsonWithPrivateKey(String accountId, Ed25519PrivateKey privateKey) {
        JsonObject account = new JsonObject();
        account.add("accountId", accountId);
        account.add("privateKey", privateKey.toString());
        account.add("publicKey", privateKey.publicKey.toString());
        return account;
    }

    private JsonObject createAccountJsonWithKeyPair(String accountId, KeyPair keypair) {
        JsonObject account = new JsonObject();
        account.add("accountId", accountId);
        account.add("privateKey", keypair.getPrivateKeyHex());
        account.add("publicKey", keypair.getPublicKeyHex());
        return account;
    }

    public void setDefaultAccountId(AccountId accountId, KeyPair keypair) {
        JsonObject account = createAccountJsonWithKeyPair(accountId.toString(), keypair);
        writeAccountId(accountId, account);
    }

    public void setDefaultAccountId(AccountId accountId, Ed25519PrivateKey privateKey) {
        JsonObject account = createAccountJsonWithPrivateKey(accountId.toString(), privateKey);
        writeAccountId(accountId, account);
    }

    private void writeAccountId(AccountId accountId, JsonObject account) {
        // ~/.hedera/[network_name]/accounts/[account_name].json
        String fileName = randomNameGenerator.getRandomName();
        String fileNameWithExt = fileName + ".json";
        String networkName = dataDirectory.readFile("network.txt");
        String pathToAccountsFolder = networkName + File.separator + "accounts" + File.separator;
        String pathToAccountFile = pathToAccountsFolder + fileNameWithExt;

        String pathToDefaultTxt = pathToAccountsFolder + "default.txt";
        String pathToIndexTxt = pathToAccountsFolder + "index.txt";

        HashMap<String, String> mHashMap = new HashMap<>();
        mHashMap.put(accountId.toString(), fileName);
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
            shellHelper.printInfo(accountId.toString() + " saved");
        } catch (Exception e) {
            shellHelper.printError("Failed to save " + accountId.toString());
        }
    }

    public AccountId getDefaultAccountId() {
        String[] accountString = defaultAccountString();
        return AccountId.fromString(accountString[1]);
    }

    public String getDefaultAccountKeyInHexString() {
        String pathToDefaultJsonAccount = pathToAccountsFolder() + defaultAccountString()[0] + ".json";
        HashMap<String, String> defaultJsonAccount = dataDirectory.readJsonToHashmap(pathToDefaultJsonAccount);
        return defaultJsonAccount.get(PRIVATEKEY).toString();
    }

    public String getDefaultAccountPublicKeyInHexString() {
        String pathToDefaultJsonAccount = pathToAccountsFolder() + defaultAccountString()[0] + ".json";
        HashMap<String, String> defaultJsonAccount = dataDirectory.readJsonToHashmap(pathToDefaultJsonAccount);
        return defaultJsonAccount.get(PUBLICKEY).toString();
    }


    public boolean isAccountId(String str) {
        return str != null && str.matches("\\d+\\.\\d+\\.\\d+");
    }


    public List<String> verifyPhraseList(List<String> phraseList) {
        if (phraseList.size() != 24) {
            shellHelper.printError("Recovery words must contain 24 words");
            return null;
        }
        return phraseList;
    }

    public String verifyAccountId(String accountIdInString) {
        boolean isAccountId = isAccountId(accountIdInString);
        if (!isAccountId) {
            shellHelper.printError("AccountId must be in the format of 0.0.xxxx");
            return null;
        }
        return accountIdInString;
    }

    public String verifyMethod(String method) {
        if ("bip".equals(method)) {
            return method;
        } else if ("hgc".equals(method)) {
            return method;
        } else {
            shellHelper.printError("Enter hgc ONLY IF you have created your account via Hedera wallet and HAVE NOT updated, otherwise enter bip");
            return null;
        }
    }

    public boolean isBip(String method) {
        return method.equalsIgnoreCase("bip");
    }

    public String promptMemoString(InputReader inputReader) {
        String memoString = inputReader.prompt("Memo field");
        if (StringUtil.isNullOrEmpty(memoString)) {
            memoString = "";
        }
        return  memoString;
    }
    
}
