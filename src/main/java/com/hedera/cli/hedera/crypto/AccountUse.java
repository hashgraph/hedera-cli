package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.hedera.setup.Setup;
import com.hedera.cli.hedera.utils.AccountUtils;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import org.hjson.JsonObject;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Model.CommandSpec;

import java.io.File;
import java.util.HashMap;

@Command(name = "use", description = "@|fg(225) Allows to toggle between multiple Hedera Accounts|@", helpCommand = true)
public class AccountUse implements Runnable {

    @Spec
    CommandSpec spec;

    @Option(names = {"-a", "--account-id"}, description = "Account ID in %nshardNum.realmNum.accountNum format")
    private String accountId;

    private Ed25519PrivateKey accPrivKey;
    private InputReader inputReader;
    private String setAsCurrentAccount;
    private String addNewAccount;
    private static final String YES = "yes";
    private static final String NO = "no";

    public AccountUse(InputReader inputReader) {
        this.inputReader = inputReader;
    }

    @Override
    public void run() {
        DataDirectory dataDirectory = new DataDirectory();
        Object key = retrieveIndexAccount(dataDirectory);
        if (key.toString().equals(accountId)) {
            System.out.println("Account exists in index.txt, so switch the account");
            // If account already exist in index.txt and is default account?
        } else {
            addNewAccount = inputReader.prompt("Account does not exist, add new account?  yes/no \n");
            if (addNewAccount.equals(YES)) {
                String accPrivKeyInString = inputReader.prompt("Input account's private key", "secret", false);
                accPrivKey = Ed25519PrivateKey.fromString(accPrivKeyInString);
                setAsCurrentAccount = inputReader.prompt("Do you want to set as current account?  yes/no \n");
                if (setAsCurrentAccount.equals(YES)) {
                    // set network and curr account here
                    Setup setup = new Setup();
                    JsonObject account = setup.addAccountToJsonWithPrivateKey(accountId, accPrivKey);
                    setup.saveToJson(accountId, account);
                    AccountUtils accountUtils = new AccountUtils();
                    // TODO
                } else if (setAsCurrentAccount.equals(NO)) {
                    System.out.println("Using default account");
                } else {
                    throw new ParameterException(spec.commandLine(), "Input must either been yes or no");
                }
            } else if (addNewAccount.equals(NO)) {
                System.out.println("No changes made");
            } else {
                throw new ParameterException(spec.commandLine(), "Input must either been yes or no");
            }
        }
        // do we want to return anything here?
    }

    private Object retrieveIndexAccount(DataDirectory dataDirectory) {
        String networkName = dataDirectory.readFile("network.txt");
        String pathToAccountsFolder = networkName + File.separator + "accounts" + File.separator;
        String pathToDefaultTxt = pathToAccountsFolder + "default.txt";
        String pathToCurrentTxt = pathToAccountsFolder + "current.txt";
        String pathToIndexTxt = pathToAccountsFolder + "index.txt";

        String readingDefaultAccount = dataDirectory.readFile(pathToDefaultTxt);
        String defaultAccountID = readingDefaultAccount.split(":")[1];
        HashMap readingIndexAccount = dataDirectory.readFileHashmap(pathToIndexTxt);

        Object keyMatches = null;
        for (Object key : readingIndexAccount.keySet()) {
            System.out.println("what is this here" + key.toString());
            keyMatches = key;
        }
        return keyMatches;
    }
}
