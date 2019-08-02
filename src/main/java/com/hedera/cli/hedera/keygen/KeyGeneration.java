package com.hedera.cli.hedera.keygen;

import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;

@Component
@Command(name = "generate",
         description = "@|fg(magenta) Transfer hbars to a single account|@%n",
         helpCommand = true)
public class KeyGeneration implements Runnable {

  @Override
  public void run() {
    System.out.println("KeyGeneration");
  }

}