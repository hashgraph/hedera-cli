package com.hedera.cli.hedera.network;

import java.io.File;
import java.io.InputStream;

import com.hedera.cli.hedera.utils.DataDirectory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "ls", description = "@|fg(225) List all available Hedera network.|@", helpCommand = true)
public class NetworkList implements Runnable {

  @Autowired
  DataDirectory dataDirectory;

  private String addressBookJsonPath;

  @Override
  public void run() {
    addressBookJsonPath = File.separator + "addressbook.json";
    InputStream addressBookInputStream = getClass().getResourceAsStream(addressBookJsonPath);
    dataDirectory.listNetworks(addressBookInputStream);
  }

  // allow for dependency injection
  public void setAddressBookJson(String addressBookJsonRelativePath) {
    this.addressBookJsonPath = addressBookJsonRelativePath;
  }

}