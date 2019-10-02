package com.hedera.cli.hedera.crypto;

import java.io.File;
import java.util.HashMap;

import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.services.CurrentAccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@Component
@Command(name = "use", description = "@|fg(225) Allows to toggle between multiple Hedera Accounts|@", helpCommand = true)
public class AccountUse implements Runnable {

    @Autowired
    ApplicationContext context;

    @Spec
    CommandSpec spec;

    @Option(names = { "-a", "--account-id" }, description = "Account ID in %nshardNum.realmNum.accountNum format")
    private String accountId;

    // private Ed25519PrivateKey accPrivKey;
    // private InputReader inputReader;
    // private String setAsCurrentAccount;
    // private String addNewAccount;
    // private static final String YES = "yes";
    // private static final String NO = "no";

    // public AccountUse(InputReader inputReader) {
    // this.inputReader = inputReader;
    // }

    // account use -a=0.0.1001
    @Override
    public void run() {
        DataDirectory dataDirectory = new DataDirectory();
        boolean exists = accountIdExistsInIndex(dataDirectory, accountId);
        if (exists) {
            // since this accountId exists, we set it into our CurrentAccountService
            // singleton
            System.out.println(context);
            CurrentAccountService currentAccountService = (CurrentAccountService) context.getBean("currentAccount",
                    CurrentAccountService.class);
            currentAccountService.setAccountNumber(accountId);
        } else {
            System.out.println("This account does not exist, please add new account using `account recovery`");
        }
        // if (key.toString().equals(accountId)) {
        // System.out.println("Account exists in index.txt, so switch the account");
        // If account already exist in index.txt and is default account?
        // }
        // } else {
        // addNewAccount = inputReader.prompt("Account does not exist, add new account?
        // yes/no \n");
        // if (addNewAccount.equals(YES)) {
        // String accPrivKeyInString = inputReader.prompt("Input account's private key",
        // "secret", false);
        // accPrivKey = Ed25519PrivateKey.fromString(accPrivKeyInString);
        // setAsCurrentAccount = inputReader.prompt("Do you want to set as current
        // account? yes/no \n");
        // if (setAsCurrentAccount.equals(YES)) {
        // // set network and curr account here
        // Setup setup = new Setup();
        // JsonObject account = setup.addAccountToJsonWithPrivateKey(accountId,
        // accPrivKey);
        // setup.saveToJson(accountId, account);
        // // AccountUtils accountUtils = new AccountUtils();
        // // TODO
        // } else if (setAsCurrentAccount.equals(NO)) {
        // System.out.println("Using default account");
        // } else {
        // throw new ParameterException(spec.commandLine(), "Input must either been yes
        // or no");
        // }
        // } else if (addNewAccount.equals(NO)) {
        // System.out.println("No changes made");
        // } else {
        // throw new ParameterException(spec.commandLine(), "Input must either been yes
        // or no");
        // }
        // }
        // do we want to return anything here?
    }

    /**
     * 
     * @param dataDirectory
     * @return boolean accountIdExists
     */
    private boolean accountIdExistsInIndex(DataDirectory dataDirectory, String accountId) {
        String networkName = dataDirectory.readFile("network.txt");
        String pathToAccountsFolder = networkName + File.separator + "accounts" + File.separator;
        String pathToIndexTxt = pathToAccountsFolder + "index.txt";
        HashMap<String, String> readingIndexAccount = dataDirectory.readFileHashmap(pathToIndexTxt);
        for (Object key : readingIndexAccount.keySet()) {
            if (accountId.equals(key.toString())) {
                return true;
            }
        }
        return false;
    }

}
