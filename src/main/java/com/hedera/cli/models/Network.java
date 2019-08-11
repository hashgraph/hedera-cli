package com.hedera.cli.models;

import java.util.List;
import java.util.Random;

public class Network {

  private String name;

  private List<HederaNode> nodes;

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

  public HederaNode getNodeByAccountId(String accountId) {
    HederaNode selectedNode = null;
    for (HederaNode node: nodes) {
      if (node.getAccount().equals(accountId)) {
        selectedNode = node;
      }
    }
    return selectedNode;
  }
}