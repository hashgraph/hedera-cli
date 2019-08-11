package com.hedera.cli.models;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class HederaNodeTest {

  @Test
  public void testNode() {
    AddressBook addressBook = AddressBook.init();
    List<Network> networks = addressBook.getNetworks();
    Network network = networks.get(0);
    HederaNode node = network.getNodeByAccountId("0.0.3");
    String expected = "0.0.3";
    String actual = node.getAccount();
    assertEquals(expected, actual);
    assertEquals("35.237.200.180:50211", node.getAddress());
  }

}