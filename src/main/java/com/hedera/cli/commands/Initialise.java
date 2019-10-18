package com.hedera.cli.commands;

import java.io.File;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.setup.Setup;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import picocli.CommandLine;

@ShellComponent(value = "Initialise")
public class Initialise {

  @Autowired
  ShellHelper shellHelper;

  @Autowired
  InputReader inputReader;

  @Autowired
  DataDirectory dataDirectory;

  @Autowired
  Setup setup;

  private String defaultNetworkName = "aspen";

  @ShellMethod(value = "Initialise a Hedera account as the default operator")
  public void setup() {
    String currentNetwork = dataDirectory.readFile("network.txt", defaultNetworkName);
    String pathToDefaultAccount = currentNetwork + File.separator + "accounts" + File.separator + "default.txt";
    String defaultAccount = "";
    try {
      defaultAccount = dataDirectory.readFile(pathToDefaultAccount);
    } catch (Exception e) {
      // do nothing
    }

    if (defaultAccount.isEmpty()) {
      System.out.println("defaultAccount does not exist");
      setup.handle(inputReader, shellHelper);
    } else {
      System.out.println("defaultAccount already exists");
      CommandLine.usage(this, System.out);
    }

  }

}