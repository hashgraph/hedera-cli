package com.hedera.cli.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.hedera.utils.DataDirectory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * AddressBook class manages the parsing of the addressbook.json file, which is
 * stored in resources directory
 */
public class AddressBook {

  private List<Network> networks;

  static public AddressBook init() {
    return create("");
  }

  static public AddressBook init(String addressBookFilename) {
    return create(addressBookFilename);
  }

  static private AddressBook create(String addressBookFilename) {
    AddressBook addressBook = null;
    if (addressBookFilename.isEmpty()) {
      addressBookFilename = "addressbook.json";
    }
    String addressBookJson = File.separator + addressBookFilename;
    try {
      // mapper.readerForUpdating(this).readValue(addressBookInputStream);
      ObjectMapper mapper = new ObjectMapper();
      InputStream input = AddressBook.class.getResourceAsStream(addressBookJson);
      addressBook = mapper.readValue(input, AddressBook.class);
    } catch (IOException e) {
      // do nothing
    }
    return addressBook;
  }

  public List<Network> getNetworks() {
    return networks;
  }

  public List<String> getNetworksAsStrings() {
    List<String> list = new ArrayList<String>();
    List<Network> networks = this.getNetworks();
    for (Network network: networks) {
        list.add(network.getName());
    }
    return list;
  }

  public Network getCurrentNetwork() {
    try {
      DataDirectory dataDirectory = new DataDirectory();
      System.out.println(dataDirectory);
      String currentNetworkString = dataDirectory.readFile("network.txt");
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

}