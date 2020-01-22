package com.hedera.cli.hedera.hcs;

import picocli.CommandLine.Command;

@Command(name = "submit")
public class SubmitMessage implements Runnable {

  @Override
  public void run() {
    System.out.println("submit message to hcs");
  }

}