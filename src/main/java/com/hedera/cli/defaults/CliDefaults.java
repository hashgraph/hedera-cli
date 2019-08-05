package com.hedera.cli.defaults;

import com.hedera.cli.hedera.utils.DataDirectory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.shell.Availability;

public abstract class CliDefaults {

  public Availability isDefaultNetworkAndAccountSet() {
    // sequentially
    // invoke isDefaultNetworkSet
    String defaultNetwork = DataDirectory.readFile("network.txt", "aspen");
    if (StringUtils.isEmpty(defaultNetwork)) {
      return Availability.unavailable("Please set your default network with network set command");
    }
    // DataDirectory.readFile()
    // invoke isDefaultAccountSet
    System.out.println("Invoke one function after another here");
    return Availability.available();
  }

  private Availability isDefaultNetworkSet() {
    // String defaultNetwork = DataDirectory.readFile("network.txt", "aspen");
    // if (StringUtils.isEmpty(defaultNetwork)) {
      // return Availability.unavailable("Please set your default network with network set command");
    // } else {
      return Availability.available();
    // }
  }

  private Availability isDefaultAccountSet() {
    return Availability.unavailable("Please set your default account in current network");
  }

}