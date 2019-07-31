package com.hedera.cli.hedera.network;

import com.hedera.cli.hedera.utils.DataDirectory;

import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(name = "set",
         description = "Set specific Hedera network we will use",
         helpCommand = true)
public class NetworkSet implements Runnable {

  @Option(names = {"-n", "--name"},
          description = "Name of the network" + 
          "%n@|bold,underline Usage:|@%n" +
          "network set -n=mainnet OR %n" +
          "network set --name=mainnet")
  private String name;
  
  @Override
  public void run() {
    System.out.println("Setting network name " + name);
    String userHome = System.getProperty("user.home");
    System.out.println("We should save it to " + userHome);
    DataDirectory.writeFile("network.txt", name);
  }

}