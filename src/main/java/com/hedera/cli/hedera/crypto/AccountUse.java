package com.hedera.cli.hedera.crypto;

import java.io.File;
import java.util.Map;

import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.services.CurrentAccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Getter
@Component
@Command(name = "use", 
        separator = " ", 
        description = "@|fg(225) Switch to use a specific Hedera account as operator.|@",
        helpCommand = true) // @formatter:on
public class AccountUse implements Runnable {

    @Autowired
    ApplicationContext context;

    @Parameters(index = "0", description = "Hedera account in the format shardNum.realmNum.accountNum")
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
            if (accountId.equals(key.toString())) {
                return true;
            }
        }
        return false;
    }

}

