package com.hedera.cli.models;

import java.util.List;

public class Network {

  public String name;

  public List<HederaNode> nodes;

  public List<HederaNode> getNodes() {
    return nodes;
  }
   
  public String getName() {
    return name;
  }
}