package com.hedera.cli.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hedera.cli.defaults.CliDefaults;
import com.hedera.cli.hedera.hcs.Consensus;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class HederaConsensus extends CliDefaults {

  @Autowired
  private Consensus consensus;

  @Autowired
  private ShellHelper shellHelper;

  @ShellMethod(value = "manage Hedera consensus service")
  public void hcs(@ShellOption(defaultValue = "") String subCommand, @ShellOption(defaultValue = "") String topic) {

    String[] args;
    List<String> argsList = new ArrayList<>();
    Object[] objs;

    switch (subCommand) {
    case "create":
      argsList = addToArgsList(topic, argsList);
      if (argsList.isEmpty()) {
        shellHelper.printError("Please provide a topic name");
      }
      break;
    case "submit":
      break;
    case "read":
      break;
    default:
      break;
    }
    objs = argsList.toArray();
    args = Arrays.copyOf(objs, objs.length, String[].class);
    consensus.handle(subCommand, args);
  }

  public List<String> addToArgsList(String topic, List<String> argsList) {
    if (!topic.isEmpty()) {
      argsList.add(topic);
      return argsList;
    } else {
      return argsList;
    }
  }

}
