package com.hedera.cli.models;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NetworkTest {

  @Autowired
  AddressBook addressBook;

  @Test
  public void getNetworks() {
    // List<Network> networks = addressBook.getNetworks();
    // int expected = 3;
    // int actual = networks.size();
    // assertEquals(expected, actual);
  
    // Network network = networks.get(0);
    // assertEquals("mainnet", network.getName());

    // List<HederaNode> mainnetNodes = network.getNodes();
    // assertEquals(10, mainnetNodes.size());

    // // retrieve a random node and check that we can get it back through its account id
    // HederaNode node = network.getRandomNode();
    // HederaNode nodeActual = network.getNodeByAccountId(node.getAccount());
    // assertEquals(node, nodeActual);
  }

}