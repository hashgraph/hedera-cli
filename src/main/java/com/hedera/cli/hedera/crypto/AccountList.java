package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.utils.AccountUtils;
import com.hedera.cli.shell.ShellHelper;
import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hedera.cli.hedera.utils.DataDirectory;

@Component
@Command(name = "ls", description = "@|fg(225) List of all accounts for the current network.|@")
public class AccountList implements Runnable {

  @Override
  public void run() {
    System.out.println("List of accounts in the current network");
    DataDirectory dataDirectory = new DataDirectory();
    AccountUtils accountUtils = new AccountUtils();
    String pathToIndexTxt = accountUtils.pathToAccountsFolder() + "index.txt";
    Map<String, String> readingIndexAccount = dataDirectory.readFileHashmap(pathToIndexTxt);

    for (Map.Entry<String, String> entry : readingIndexAccount.entrySet()) {
      System.out.println(entry.getKey() + " (" + entry.getValue() + ")");
    }
  }
}