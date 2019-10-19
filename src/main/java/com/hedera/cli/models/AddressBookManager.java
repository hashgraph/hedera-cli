package com.hedera.cli.models;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
  DataDirectory dataDirectory;

  @Autowired
  ShellHelper shellHelper;

  private String defaultNetworkName = "testnet";

  @PostConstruct
  public void init() {
    String addressBookJsonPath = File.separator + "addressbook.json";
    ObjectMapper mapper = new ObjectMapper();
    InputStream input = getClass().getResourceAsStream(addressBookJsonPath);
    try {
      AddressBook addressBook = mapper.readValue(input, AddressBook.class);
      setNetworks(addressBook.getNetworks());
    } catch (IOException e) {
      shellHelper.printError(e.getMessage());
    }
  }

  public List<String> getNetworksAsStrings() {
    List<String> list = new ArrayList<String>();
    for (Network network : networks) {
      list.add(network.getName());
    }
    return list;
  }

  public Network getCurrentNetwork() {
    try {
      String currentNetworkString = dataDirectory.readFile("network.txt");
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
      String currentNetwork = dataDirectory.readFile("network", defaultNetworkName);
      if (currentNetwork != null) {
        if (currentNetwork.equals(network.getName())) {
          System.out.println("* " + network.getName());
        } else {
          System.out.println("  " + network.getName());
        }
      }
    }
  }

}