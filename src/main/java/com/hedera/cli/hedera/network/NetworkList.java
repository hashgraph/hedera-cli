package com.hedera.cli.hedera.network;

import java.io.File;
import java.io.InputStream;

import com.hedera.cli.hedera.utils.DataDirectory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "ls", description = "List all available Hedera network", helpCommand = true)
public class NetworkList implements Runnable {

  @Autowired
  DataDirectory dataDirectory;

  private String addressBookJson;

  @Override
  public void run() {
    addressBookJson = File.separator + "addressbook.json";
    InputStream addressBookInputStream = getClass().getResourceAsStream(addressBookJson);
    dataDirectory.listNetworks(addressBookInputStream);
  }

  // allow for dependency injection
  public void setAddressBookJson(String addressBookJsonRelativePath) {
    this.addressBookJson = addressBookJsonRelativePath;
  }

}