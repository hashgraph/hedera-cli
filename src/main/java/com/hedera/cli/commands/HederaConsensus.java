package com.hedera.cli.commands;

import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
public class HederaConsensus extends CommandBase {

  @Autowired
  private ShellHelper shellHelper;
  
  @ShellMethodAvailability("isNotCompleted")
  @ShellMethod(value = "manage Hedera consensus service")
  public void consensus() {
    shellHelper.printInfo("Stub function.");
  }

}
