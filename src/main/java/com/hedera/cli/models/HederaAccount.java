package com.hedera.cli.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HederaAccount {
  
  private String accountId;
  private String privateKey;
  private String publicKey;

}