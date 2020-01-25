package com.hedera.cli.hedera.hcs;

import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;

// import com.hedera.hashgraph.sdk.mirror.MirrorConsensusTopicQuery;

@Component
@Command(name = "read")
public class ReadMessage implements Runnable {

  @Override
  public void run() {
    // to use this in conjunction with local server mode
    System.out.println("Example: hcs read --topic-id 0.0.1001");
    // new MirrorConsensusTopicQuery()
    //   .setTopicId(topicId)
    //   .subscribe(mirrorClient, resp -> {
    //     String messageAsString = new String(resp.message, StandardCharsets.UTF_8);
    //     System.out.println(resp.consensusTimestamp + " received topic message: " + messageAsString);
    //   },
    //   // On gRPC error, print the stack trace
    //   Throwable::printStackTrace);
  }

  public void handle(String subCommand, String... args) {
    new CommandLine(this).execute(args);
  }

}