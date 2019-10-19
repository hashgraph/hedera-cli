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
  public AddressBookManager() {
    String  addressBookJsonPath = File.separator + "addressbook.json";
    ObjectMapper mapper = new ObjectMapper();
    InputStream input = getClass().getResourceAsStream(addressBookJsonPath);
    try {
      AddressBook addressBook = mapper.readValue(input, AddressBook.class);
      setNetworks(addressBook.getNetworks());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public List<String> getNetworksAsStrings() {
    List<String> list = new ArrayList<String>();
    for (Network network: networks) {
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
      // do nothing
    }
    return null;
  }

}