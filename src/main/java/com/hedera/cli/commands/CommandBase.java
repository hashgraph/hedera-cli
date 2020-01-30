package com.hedera.cli.commands;

import java.io.File;

import com.hedera.cli.models.DataDirectory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;

public abstract class CommandBase {

  private String defaultNetworkName = "testnet";

  @Autowired
  private DataDirectory dataDirectory;

  public Availability isDefaultNetworkAndAccountSet() {
    // do we have a default network?
    String defaultNetwork = dataDirectory.readFile("network.txt", defaultNetworkName);
    if (StringUtils.isEmpty(defaultNetwork)) {
      return Availability.unavailable("you have not set your default network");
    }

    // do we have a default account?
    String pathToDefaultAccount = defaultNetwork + File.separator + "accounts" + File.separator + "default.txt";
    String defaultAccount = dataDirectory.readFile(pathToDefaultAccount);
    if (defaultAccount.isEmpty()) {
      return Availability.unavailable("you have not set your default account for the current network");
    }
    // invoke isDefaultAccountSet
    return Availability.available();
  }

  public Availability isNotCompleted() {
    return Availability.unavailable("it is not completed");
  }

  public void setDataDirectory(DataDirectory dataDirectory) {
    this.dataDirectory = dataDirectory;
  }
}