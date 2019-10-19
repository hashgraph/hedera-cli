package com.hedera.cli.models;

import org.springframework.stereotype.Component;

@Component
public class HederaNode {

  private String account;
  
  private String address;

  public String getAccount() {
    return account;
  }

  public String getAddress() {
    return address;
  }
}