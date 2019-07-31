package com.hedera.cli;

import com.hedera.cli.shell.ShellHelper;
import com.hedera.cli.hedera.network.Network;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class HederaNetwork {

  @Autowired
  ShellHelper shellHelper;
  
  @Autowired
  Network network;

  @ShellMethod(value = "switch and manage different Hedera network")
  public void network(
    @ShellOption(defaultValue = "") String subCommand,
    @ShellOption(defaultValue = "") String... args) {
    Network network = new Network();
    network.handle(subCommand, args);
  }

}