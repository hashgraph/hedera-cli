package com.hedera.cli.models;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.hedera.utils.DataDirectory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AddressBook class manages the parsing of the addressbook.json file, which is
 * stored in resources directory
 */
@Component
public class AddressBook {

  private List<Network> networks;

  @Autowired
  DataDirectory dataDirectory;

  private List<Network> read() {
    List<Network> networks = null;
    String addressBookJson = File.separator + "addressbook.json";
    try {
      // mapper.readerForUpdating(this).readValue(addressBookInputStream);
      ObjectMapper mapper = new ObjectMapper();
      InputStream input = AddressBook.class.getResourceAsStream(addressBookJson);
      AddressBook addressBook = mapper.readValue(input, AddressBook.class);
      networks = addressBook.getNetworks();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return networks;
  }

  public List<Network> getNetworks() {
    return networks;
  }

  public List<String> getNetworksAsStrings() {
    List<String> list = new ArrayList<String>();
    List<Network> networks = read();
    for (Network network: networks) {
        list.add(network.getName());
    }
    return list;
  }

  public Network getCurrentNetwork() {
    try {
      String currentNetworkString = dataDirectory.readFile("network.txt");
      List<Network> networks = read();
      for (Network network : networks) {
        if (network.getName().equals(currentNetworkString)) {
          return network;
        }
      }
    } catch (Exception e) {
      // do nothing
    }
    return null;
  }

  public void setDataDirectory(DataDirectory dataDirectory) {
    this.dataDirectory = dataDirectory;
  }

}