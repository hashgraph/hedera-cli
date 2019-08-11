package com.hedera.cli.models;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * AddressBook class manages the parsing of the addressbook.json file, which is stored in resources directory
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
      File file = ResourceUtils.getFile(AddressBook.class.getResource(addressBookJson));
      addressBook = mapper.readValue(file, AddressBook.class);
    } catch (IOException e) {
      // do nothing
    }
    return addressBook;
  }

  public List<Network> getNetworks() {
    return networks;
  }

}