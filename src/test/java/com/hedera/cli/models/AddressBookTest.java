package com.hedera.cli.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AddressBookTest {

  private final String ADDRESSBOOK_DEFAULT = "addressbook.json";
  private AddressBook addressBook;

  @BeforeEach
  public void setup() {
    // use our default addressbook as test data
    String addressBookJsonPath = File.separator + ADDRESSBOOK_DEFAULT;
    ObjectMapper mapper = new ObjectMapper();
    InputStream input = getClass().getResourceAsStream(addressBookJsonPath);
    try {
      addressBook = mapper.readValue(input, AddressBook.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void getNetworks() {
    assertNotNull(addressBook);

    List<Network> networks = addressBook.getNetworks();
    int expected = 2;
    int actual = networks.size();
    assertEquals(expected, actual);

    Network network = networks.get(0);
    assertEquals("mainnet", network.getName());
  }

}