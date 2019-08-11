package com.hedera.cli.models;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AddressBookTest {

  @Test
  public void getNetworks() {
    AddressBook addressBook = AddressBook.init();
    List<Network> networks = addressBook.getNetworks();
    int expected = 3;
    int actual = networks.size();
    assertEquals(expected, actual);

    Network network = networks.get(0);
    assertEquals("mainnet", network.getName());
  }

}