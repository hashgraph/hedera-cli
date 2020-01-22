package com.hedera.cli.hedera.hcs;

import picocli.CommandLine.Command;

@Command(name = "read")
public class ReadMessage implements Runnable {

  @Override
  public void run() {
    System.out.println("Example: hcs read --topic-id 0.0.1001");    
  }

}