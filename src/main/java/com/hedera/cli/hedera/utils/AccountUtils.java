package com.hedera.cli.hedera.utils;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountUtils {

    @Autowired
    private DataDirectory dataDirectory;

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

    public AccountId retrieveDefaultAccountID() {
        String[] accountString = defaultAccountString();
        return AccountId.fromString(accountString[1]);
    }

    public String retrieveDefaultAccountKeyInHexString() {
        String pathToDefaultJsonAccount = pathToAccountsFolder() + defaultAccountString()[0] + ".json";
        HashMap<String, String> defaultJsonAccount = dataDirectory.jsonToHashmap(pathToDefaultJsonAccount);
        return defaultJsonAccount.get(PRIVATEKEY).toString();
    }

    public String retrieveDefaultAccountPublicKeyInHexString() {
        String pathToDefaultJsonAccount = pathToAccountsFolder() + defaultAccountString()[0] + ".json";
        HashMap<String, String> defaultJsonAccount = dataDirectory.jsonToHashmap(pathToDefaultJsonAccount);
        return defaultJsonAccount.get(PUBLICKEY).toString();
    }


    public boolean isAccountId(String str) {
        // checks null or empty
        if (str == null || str.isEmpty()) {
            return false;
        }
        String[] strSplit = str.split("\\.");
        if (strSplit.length != 3) {
            return false;
        }
        return strSplit[0].matches("^[0-9*]+$")
                && strSplit[1].matches("^[0-9*]+$")
                && (strSplit[2].matches("^[1-9][0-9*]+$"));
    }


    public List<String> verifyPhraseList(List<String> phraseList, ShellHelper shellHelper) {
        if (phraseList.size() != 24) {
            shellHelper.printError("Recovery words must contain 24 words");
            return null;
        }
        return phraseList;
    }

    public String verifyAccountId(String accountIdInString, ShellHelper shellHelper) {
        boolean isAccountId = isAccountId(accountIdInString);
        if (!isAccountId) {
            shellHelper.printError("AccountId must be in the format of 0.0.xxxx");
            return null;
        }
        return accountIdInString;
    }

    public String verifyMethod(String method, ShellHelper shellHelper) {
        if ("bip".equals(method)) {
            return method;
        } else if ("hgc".equals(method)) {
            return method;
        } else {
            shellHelper.printError("Enter hgc ONLY IF you have created your account via Hedera wallet and HAVE NOT updated, otherwise enter bip");
            return null;
        }
    }

    public String promptMemoString(InputReader inputReader) {
        String memoString = inputReader.prompt("Memo field");
        if (StringUtil.isNullOrEmpty(memoString)) {
            memoString = "";
        }
        return  memoString;
    }
}
