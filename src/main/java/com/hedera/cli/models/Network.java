package com.hedera.cli.models;

import java.util.List;
import java.util.Random;

public class Network {

  public String name;

  public List<HederaNode> nodes;

  public String ref;

  public String getRef() {
    return ref;
  }

  public List<HederaNode> getNodes() {
    return nodes;
  }
   
  public String getName() {
    return name;
  }

  public HederaNode getRandomNode() {
    Random rand = new Random();
    List<HederaNode> nodes = this.getNodes();
    HederaNode node = nodes.get(rand.nextInt(nodes.size()));
    return node;
  }
}