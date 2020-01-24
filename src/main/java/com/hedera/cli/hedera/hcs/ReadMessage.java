package com.hedera.cli.hedera.hcs;

import picocli.CommandLine.Command;

@Command(name = "read")
public class ReadMessage implements Runnable {

  @Override
  public void run() {
    // to use this in conjunction with local server mode
    System.out.println("Example: hcs read --topic-id 0.0.1001");    
  }

}