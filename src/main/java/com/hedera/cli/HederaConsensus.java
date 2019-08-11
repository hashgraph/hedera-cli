package com.hedera.cli;

import com.hedera.cli.defaults.CliDefaults;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
public class HederaConsensus extends CliDefaults {

  @Autowired
  ShellHelper shellHelper;
  
  @ShellMethodAvailability("isNotCompleted")
  @ShellMethod(value = "manage Hedera consensus service")
  public void consensus() {
    System.out.println("Stub function.");
  }

}
