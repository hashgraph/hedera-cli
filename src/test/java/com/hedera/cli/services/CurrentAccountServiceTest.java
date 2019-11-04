package com.hedera.cli.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CurrentAccountServiceTest {
  @Test
  public void settersAndGetters() {
    CurrentAccountService c = new CurrentAccountService();

    c.setNetwork("testnet");
    c.setAccountNumber("0.0.1001");
    c.setPrivateKey("somePrivateKey");
    c.setPublicKey("somePublicKey");

    assertEquals("testnet", c.getNetwork());
    assertEquals("0.0.1001", c.getAccountNumber());
    assertEquals("somePrivateKey", c.getPrivateKey());
    assertEquals("somePublicKey", c.getPublicKey());
  }
}