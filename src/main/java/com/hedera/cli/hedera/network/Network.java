package com.hedera.cli.hedera.network;

import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name = "network",
         description = "List and set the network in use",
         subcommands = {NetworkList.class, NetworkSet.class})
public class Network implements Runnable {

  @Override
  public void run() {
    CommandLine.usage(this, System.out);
  }

  public void handle(String subCommand, String... args) {
    switch (subCommand) {
      case "ls":
        if (args.length == 0) {
          new CommandLine(new NetworkList()).execute(args);
        } else {
          CommandLine.usage(new NetworkList(), System.out);
        }
        break;
      case "set":
        if (args.length == 0) {
          CommandLine.usage(new NetworkSet(), System.out);
        } else {
          new CommandLine(new NetworkSet()).execute(args);
        }
        break;
      default:
        this.run();
        break;
    }
  }

}