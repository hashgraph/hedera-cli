package com.hedera.cli.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hedera.cli.defaults.CliDefaults;
import com.hedera.cli.hedera.hcs.Consensus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class HederaConsensus extends CliDefaults {

  @Autowired
  private Consensus consensus;

  @ShellMethod(value = "manage Hedera consensus service") // @formatter:off
  public void hcs(@ShellOption(defaultValue = "") String subCommand,
                  @ShellOption(defaultValue = "") String topicIdString,
                  // Specifying -y flag will set y to be true (which will require submit key)
                  @ShellOption(value = {"-y", "--yes"}, arity = 0, defaultValue = "false") boolean y) { // @formatter:on

    String[] args;
    List<String> argsList = new ArrayList<>();
    Object[] objs;

    switch (subCommand) {
    case "create":
      // argsList.add("-y " + y);
      break;
    case "submit":
      argsList = addToArgsList(topicIdString, argsList);
      break;
    case "read":
      argsList = addToArgsList(topicIdString, argsList);
      break;
    default:
      break;
    }
    objs = argsList.toArray();
    args = Arrays.copyOf(objs, objs.length, String[].class);
    consensus.handle(subCommand, args);
  }

  public List<String> addToArgsList(String topicIdString, List<String> argsList) {
    if (!topicIdString.isEmpty()) {
      argsList.add(topicIdString);
      return argsList;
    } else {
      return argsList;
    }
  }
}
