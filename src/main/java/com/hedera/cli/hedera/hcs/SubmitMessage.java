package com.hedera.cli.hedera.hcs;

import com.hedera.cli.hedera.Hedera;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.HederaNetworkException;
import com.hedera.hashgraph.sdk.HederaStatusException;
import com.hedera.hashgraph.sdk.Transaction;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.consensus.ConsensusMessageSubmitTransaction;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "submit")
public class SubmitMessage implements Runnable {

  @Autowired
  private Hedera hedera;

  @Parameters(index = "0", description = "topic name" + "%n@|bold,underline Usage:|@%n"
      + "@|fg(yellow) hcs submit|@")
  private String topicIdString;

  @Option(names = { "-m", "--memo" }, required = false, description = "memo")
  private String memo = "";

  @Option(names = { "-k", "--submit-key" }, required = false, description = "submit key")
  private boolean submitKey = false;

  @Override
  public void run() {
    System.out.println("hcs submit " + topicIdString);
    System.out.println("memo: " + memo);
    System.out.println("Is submit key required? " + submitKey);

    Client client = hedera.createHederaClient();

    ConsensusTopicId topicId = ConsensusTopicId.fromString(topicIdString);

    try {
       Transaction tx = new ConsensusMessageSubmitTransaction()
        .setTopicId(topicId)
        .setMessage(memo)
        .build(client);

      if (submitKey) {
        System.out.println("Submit key is required.");
        System.out.println("So, implement function to retrieve private key for topicId " + topicIdString);
        // then sign
        // tx = tx.sign(privateKey);
      }

      TransactionId txId = tx.execute(client);
        
      TransactionReceipt receipt = txId.getReceipt(client);
      
      System.out.println("Consensus Topic Sequence Number: " + receipt.getConsensusTopicSequenceNumber());
    } catch (HederaNetworkException | HederaStatusException e) {
      e.printStackTrace();
    }

  }

  public void handle(String subCommand, String... args) {
    new CommandLine(this).execute(args);
  }

}