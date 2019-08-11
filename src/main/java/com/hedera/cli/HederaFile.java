package com.hedera.cli;

import com.hedera.cli.defaults.CliDefaults;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.file.File;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@ShellComponent
public class HederaFile extends CliDefaults {

  @Autowired
  ShellHelper shellHelper;

  public HederaFile(DataDirectory dataDirectory) {
    super(dataDirectory);
}
  
  @ShellMethodAvailability("isDefaultNetworkAndAccountSet")
  @ShellMethod(value = "manage hedera file")
  public void file(
          @ShellOption(defaultValue = "") String subCommand,
          @ShellOption(defaultValue = "", arity = -1) String... args) {

//      DataDirectory.readFile("network.txt", "aspen");
//      Hedera hedera = new Hedera();
//      List<String> networkList = hedera.getNetworksStrings();
//      for (String network: networkList) {
//          DataDirectory.mkHederaSubDir(network + "/accounts");
//      }

      File file = new File();
      file.handle(subCommand, args);
  }
}