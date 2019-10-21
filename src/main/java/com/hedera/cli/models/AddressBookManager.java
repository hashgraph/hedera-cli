package com.hedera.cli.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class AddressBookManager {

  private List<Network> networks;

  @Autowired
  private DataDirectory dataDirectory;

  @Autowired
  private ShellHelper shellHelper;

  static private final String ADDRESSBOOK_DEFAULT = "addressbook.json";
  static private final String NETWORK_DEFAULT = "testnet";
  static private final String NETWORK_FILE = "network.txt";
  static private final String ACCOUNT_DEFAULT_FILE = "default.txt";

  @PostConstruct
  public void init() {
    // read in addressbook.json
    String addressBookJsonPath = File.separator + ADDRESSBOOK_DEFAULT;
    ObjectMapper mapper = new ObjectMapper();
    InputStream input = getClass().getResourceAsStream(addressBookJsonPath);
    try {
      AddressBook addressBook = mapper.readValue(input, AddressBook.class);

      // check to see if there is an additional addressbook.json in ~/.hedera directory
      // if it exists, we will deep merge our /resources/addressbook.json with ~/.hedera/addressbook.json
      Path additionalAddressBook = Paths.get(System.getProperty("user.home"), ".hedera", "addressbook.json");
      File additionalAddressBookFile = new File(additionalAddressBook.toString());
      if (additionalAddressBookFile.exists()) {
        InputStream additionalInput = new FileInputStream(additionalAddressBookFile);
        addressBook = mapper.readerForUpdating(addressBook).readValue(additionalInput);
      }

      setNetworks(addressBook.getNetworks());
    } catch (IOException e) {
      shellHelper.printError(e.getMessage());
    }

    // ensure that all sub-directories are created
    for (String network : getNetworksAsStrings()) {
      String accountsDirForNetwork = network + File.separator + "accounts";
      dataDirectory.mkHederaSubDir(accountsDirForNetwork);
    }
  }

  public List<String> getNetworksAsStrings() {
    List<String> list = new ArrayList<String>();
    for (Network network : networks) {
      list.add(network.getName());
    }
    return list;
  }

  public String getCurrentNetworkAsString() {
    return dataDirectory.readFile(NETWORK_FILE, NETWORK_DEFAULT);
  }

  public Network getCurrentNetwork() {
    try {
      String currentNetworkString = getCurrentNetworkAsString();
      for (Network network : networks) {
        if (network.getName().equals(currentNetworkString)) {
          return network;
        }
      }
    } catch (Exception e) {
      shellHelper.printError(e.getMessage());
    }
    return null;
  }

  public void listNetworks() {
    for (Network network : networks) {
      String currentNetwork = dataDirectory.readFile(NETWORK_FILE, NETWORK_DEFAULT);
      if (currentNetwork != null) {
        if (currentNetwork.equals(network.getName())) {
          System.out.println("* " + network.getName());
        } else {
          System.out.println("  " + network.getName());
        }
      }
    }
  }

  // Returns an empty string if there's no default account
  public String getDefaultAccount() {
    String defaultAccount = "";
    String currentNetwork = dataDirectory.readFile(NETWORK_FILE, NETWORK_DEFAULT);
    String pathToDefaultAccount = currentNetwork + File.separator + "accounts" + File.separator + ACCOUNT_DEFAULT_FILE;
    try {
      defaultAccount = dataDirectory.readFile(pathToDefaultAccount);
    } catch (Exception e) {
      // no default account
      return "";
    }
    return defaultAccount;
  }

}