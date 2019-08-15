package com.hedera.cli.hedera.network;

import java.io.File;
import java.io.InputStream;

import com.hedera.cli.hedera.utils.DataDirectory;

import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;

@Component
@Command(name = "ls", description = "List all available Hedera network", helpCommand = true)
public class NetworkList implements Runnable {

  @Override
  public void run() {
    String addressBookJson = File.separator + "addressbook.json";
    InputStream addressBookInputStream = getClass().getResourceAsStream(addressBookJson);
    DataDirectory dataDirectory = new DataDirectory();
    dataDirectory.listNetworks(addressBookInputStream);
  }
}