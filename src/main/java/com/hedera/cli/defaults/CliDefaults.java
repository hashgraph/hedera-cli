package com.hedera.cli.defaults;

import java.io.File;

import com.hedera.cli.hedera.utils.DataDirectory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.shell.Availability;

public abstract class CliDefaults {

  public Availability isDefaultNetworkAndAccountSet() {
    DataDirectory dataDirectory = new DataDirectory();
    // sequentially
    // invoke isDefaultNetworkSet
    String defaultNetwork = dataDirectory.readFile("network.txt", "aspen");
    if (StringUtils.isEmpty(defaultNetwork)) {
      return Availability.unavailable("Please set your default network with network set command");
    }

    String currentNetwork = dataDirectory.readFile("network.txt", "aspen");
    String pathToDefaultAccount = currentNetwork + File.separator + "accounts" + File.separator + "default.txt";
    String defaultAccount = "";
    try {
      dataDirectory.readFile(pathToDefaultAccount);
    } catch (Exception e) {
      if (defaultAccount.isEmpty()) {
        System.out.println("Please set your default account in current network");
      }  
    }
     // invoke isDefaultAccountSet
    return Availability.available();
  }
}