package com.hedera.cli.hedera.network;

import java.io.InputStream;

import com.hedera.cli.hedera.utils.DataDirectory;

import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;

@Component
@Command(name = "ls",
         description = "List all available Hedera network",
         helpCommand = true)
public class NetworkList implements Runnable {

  @Override
  public void run() {
    DataDirectory dataDirectory = new DataDirectory();
    InputStream addressBookInputStream = getClass().getResourceAsStream("/addressbook.json");
    dataDirectory.readJsonToMap(addressBookInputStream);
  }
}