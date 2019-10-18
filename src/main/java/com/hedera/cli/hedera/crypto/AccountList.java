package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.utils.AccountUtils;
import com.hedera.cli.hedera.utils.DataDirectory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;

@Component
@Command(name = "ls", description = "@|fg(225) List of all accounts for the current network.|@")
public class AccountList implements Runnable {

    @Autowired
    DataDirectory dataDirectory;

    @Autowired
    AccountUtils accountUtils;

    @Override
    public void run() {
        System.out.println("List of accounts in the current network");
        String pathToIndexTxt = accountUtils.pathToIndexTxt();
        dataDirectory.readIndex(pathToIndexTxt);
    }
}