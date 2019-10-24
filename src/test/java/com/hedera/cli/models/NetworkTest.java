package com.hedera.cli.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("serial")
public class NetworkTest {

  private final String ADDRESSBOOK_DEFAULT = "addressbook.json";
  private Network network;
  // manually added list for comparison against addressbook.json
  private List<Map<String, String>> testnetNodes = new ArrayList<Map<String, String>>();

  @BeforeEach
  public void setup() {
    // use our default addressbook as test data
    String addressBookJsonPath = File.separator + ADDRESSBOOK_DEFAULT;
    ObjectMapper mapper = new ObjectMapper();
    InputStream input = getClass().getResourceAsStream(addressBookJsonPath);
    try {
      AddressBook addressBook = mapper.readValue(input, AddressBook.class);
      List<Network> networks = addressBook.getNetworks();
      for (Network n: networks) {
        if ("testnet".equals(n.getName())) {
          network = n;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    // literal declaration for comparison with the parsed-in addressbook.json above
    Map<String, String> node = new HashMap<String, String>() {{
      put("account", "0.0.3");
      put("address", "35.188.20.11:50211");
    }};
    Map<String, String> node2 = new HashMap<String, String>() {{
      put("account", "0.0.4");
      put("address", "35.224.154.10:50211");
    }};
    Map<String, String> node3 = new HashMap<String, String>() {{
      put("account", "0.0.5");
      put("address", "34.66.20.182:50211");
    }};
    Map<String, String> node4 = new HashMap<String, String>() {{
      put("account", "0.0.6");
      put("address", "35.238.127.7:50211");
    }};
    testnetNodes.add(node);
    testnetNodes.add(node2);
    testnetNodes.add(node3);
    testnetNodes.add(node4);
  }

  @Test
  public void getRandomNode() {
    HederaNode node = network.getRandomNode();
    Map<String, String> nodeMap = new HashMap<String, String>() {{
      put("account", node.getAccount());
      put("address", node.getAddress());
    }};
    assertTrue(testnetNodes.contains(nodeMap));
  }

  @Test
  public void getNodeByAccountId() {
    HederaNode node = network.getNodeByAccountId("0.0.3");
    assertEquals("35.188.20.11:50211", node.getAddress());
  }

}