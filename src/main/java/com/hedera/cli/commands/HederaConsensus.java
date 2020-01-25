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

  @ShellMethod(value = "manage Hedera consensus service") // @formatter:off
  public void hcs(@ShellOption(defaultValue = "") String subCommand, 
                  @ShellOption(defaultValue = "") String topic,
                  // Specifying -y flag will set y to be true (which will require submit key)
                  @ShellOption(value = {"-y", "--yes"}, arity = 0, defaultValue = "false") boolean y) { // @formatter:on

    String[] args;
    List<String> argsList = new ArrayList<>();
    Object[] objs;

    switch (subCommand) {
    case "create":
      if (!topic.isEmpty()) {
        argsList.add(topic);
      } else {
        shellHelper.printError("Please provide a topic name");
      }
      argsList.add("-y " + y);
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
