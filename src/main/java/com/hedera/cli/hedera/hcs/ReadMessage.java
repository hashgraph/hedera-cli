package com.hedera.cli.hedera.hcs;

import java.nio.charset.StandardCharsets;

import com.hedera.cli.models.AddressBookManager;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.mirror.MirrorClient;
import com.hedera.hashgraph.sdk.mirror.MirrorConsensusTopicQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "read")
public class ReadMessage implements Runnable {

  @Parameters(index = "0", description = "topic id" + "%n@|bold,underline Usage:|@%n"
      + "@|fg(yellow) hcs create helloworld|@")
  private String topicIdString;

  @Autowired
  private AddressBookManager am;

  @Override
  public void run() {

    ConsensusTopicId topicId = ConsensusTopicId.fromString(topicIdString);

    final MirrorClient mirrorClient = new MirrorClient(am.getCurrentMirrorNetwork());

    // to use this in conjunction with local server mode
    System.out.println("Example: hcs read " + topicIdString);
    new MirrorConsensusTopicQuery().setTopicId(topicId).subscribe(mirrorClient, resp -> {
      String messageAsString = new String(resp.message, StandardCharsets.UTF_8);
      System.out.println(resp.consensusTimestamp + " received topic message: " + messageAsString);
    },
        // On gRPC error, print the stack trace
        Throwable::printStackTrace);
  }

  public void handle(String subCommand, String... args) {
    new CommandLine(this).execute(args);
  }

}