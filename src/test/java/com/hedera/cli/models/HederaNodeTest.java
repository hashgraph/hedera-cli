package com.hedera.cli.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HederaNodeTest {

  @Test
  public void hederaNode() {

    String account = "0.0.3";
    String address = "35.237.200.180:50211";

    HederaNode hederaNode = new HederaNode();
    hederaNode.setAccount(account);
    hederaNode.setAddress(address);

    assertEquals(account, hederaNode.getAccount());
    assertEquals(address, hederaNode.getAddress());    
  }

}