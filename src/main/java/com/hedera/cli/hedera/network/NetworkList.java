package com.hedera.cli.hedera.network;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.models.AddressBook;
import com.hedera.cli.models.Network;

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