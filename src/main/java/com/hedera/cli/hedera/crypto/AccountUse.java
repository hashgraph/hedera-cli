package com.hedera.cli.hedera.crypto;

import java.io.File;
import java.util.Map;

import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.services.CurrentAccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Component
@Command(name = "use", separator = " ", description = "@|fg(225) Allows to toggle between multiple Hedera Accounts|@", helpCommand = true)
public class AccountUse implements Runnable {

    @Autowired
    ApplicationContext context;

    @Spec
    CommandSpec spec;

    // @Option(names = { "-a", "--accountId" }, description = "Account ID in %nshardNum.realmNum.accountNum format")
    // private String accountId;

    @Parameters(index = "0")
    private String accountId;

    @Override
    public void run() {
        DataDirectory dataDirectory = new DataDirectory();
        boolean exists = accountIdExistsInIndex(dataDirectory, accountId);
        if (exists) {
            // since this accountId exists, we set it into our CurrentAccountService
            // singleton
            CurrentAccountService currentAccountService = (CurrentAccountService) context.getBean("currentAccount",
                    CurrentAccountService.class);
            currentAccountService.setAccountNumber(accountId);
        } else {
            System.out.println("This account does not exist, please add new account using `account recovery`");
        }
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
        Map<String, String> readingIndexAccount = dataDirectory.readIndexToHashmap(pathToIndexTxt);
        for (Object key : readingIndexAccount.keySet()) {
            System.out.println(key.toString());
            if (accountId.equals(key.toString())) {
                return true;
            }
        }
        return false;
    }

}
