package com.hedera.cli.models;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class HederaNodeTest {

  @Autowired
  AddressBook addressBook;

  @Test
  public void testNode() {
    // List<Network> networks = addressBook.getNetworks();
    // Network network = networks.get(0);
    // HederaNode node = network.getNodeByAccountId("0.0.3");
    // String expected = "0.0.3";
    // String actual = node.getAccount();
    // assertEquals(expected, actual);
    // assertEquals("35.237.200.180:50211", node.getAddress());
  }

}