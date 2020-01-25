package com.hedera.cli.hedera.hcs;

import com.hedera.cli.hedera.Hedera;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.HederaNetworkException;
import com.hedera.hashgraph.sdk.HederaStatusException;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.consensus.ConsensusMessageSubmitTransaction;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "submit")
public class SubmitMessage implements Runnable {

  @Autowired
  private Hedera hedera;

  @Parameters(index = "0", description = "topic name" + "%n@|bold,underline Usage:|@%n"
      + "@|fg(yellow) hcs submit|@")
  private String topicIdString;

  // needs options 
  // declaring message
  // declaring public key etc

  @Override
  public void run() {
    System.out.println("hcs submit " + topicIdString);

    Client client = hedera.createHederaClient();

    ConsensusTopicId topicId = ConsensusTopicId.fromString(topicIdString);

    try {
      TransactionId transactionId = new ConsensusMessageSubmitTransaction()
        .setTopicId(topicId)
        .setMessage("hello, HCS!")
        .execute(client);
        
      TransactionReceipt receipt = transactionId.getReceipt(client);
      
      System.out.println(receipt.getConsensusTopicSequenceNumber());
    } catch (HederaNetworkException | HederaStatusException e) {
      e.printStackTrace();
    }

  }

  public void handle(String subCommand, String... args) {
    new CommandLine(this).execute(args);
  }

}