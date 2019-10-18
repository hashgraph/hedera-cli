package com.hedera.cli.hedera.network;

import com.hedera.cli.hedera.crypto.PicocliSpringFactory;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name = "network", description = "List and set the network in use", subcommands = { NetworkList.class,
    NetworkSet.class })
public class Network implements Runnable {

  @Autowired
  ApplicationContext context;

  @Autowired
  ShellHelper shellHelper;

  @Override
  public void run() {
    CommandLine.usage(this, System.out);
  }

  public void handle(String subCommand, String... args) {
    PicocliSpringFactory factory = new PicocliSpringFactory(context);
    switch (subCommand) {
    case "ls":
      try {
        NetworkList networkList = factory.create(NetworkList.class);
        if (args.length == 0) {
          new CommandLine(networkList).execute(args);
        } else {
          CommandLine.usage(networkList, System.out);
        }
      } catch (Exception e) {
        shellHelper.printError(e.getMessage());
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