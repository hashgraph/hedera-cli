package com.hedera.cli.defaults;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hedera.cli.hedera.utils.DataDirectory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;

public abstract class CliDefaults {

  static Logger logger = LogManager.getFormatterLogger();

  private String defaultNetworkName = "aspen";

  @Autowired
  DataDirectory dataDirectory;

  public CliDefaults() {}

  public Availability isDefaultNetworkAndAccountSet() {
    // sequentially
    // invoke isDefaultNetworkSet
    String defaultNetwork = dataDirectory.readFile("network.txt", defaultNetworkName);
    if (StringUtils.isEmpty(defaultNetwork)) {
      return Availability.unavailable("you have not set your default network");
    }

    String currentNetwork = dataDirectory.readFile("network.txt", defaultNetworkName);
    String pathToDefaultAccount = currentNetwork + File.separator + "accounts" + File.separator + "default.txt";
    String defaultAccount = "";
    try {
      dataDirectory.readFile(pathToDefaultAccount);
    } catch (Exception e) {
      if (defaultAccount.isEmpty()) {
        return Availability.unavailable("you have not set your default account for the current network");
      }  
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