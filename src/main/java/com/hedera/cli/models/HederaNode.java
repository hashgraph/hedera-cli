package com.hedera.cli.models;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class HederaNode {

  private String account;
  
  private String address;

}