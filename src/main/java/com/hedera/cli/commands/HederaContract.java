package com.hedera.cli.commands;

import com.hedera.cli.defaults.CliDefaults;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
public class HederaContract extends CliDefaults {

  @Autowired
  ShellHelper shellHelper;

  @ShellMethodAvailability("isNotCompleted")
  @ShellMethod(value = "manage Solidity contracts on Hedera")
  public void contract() {
      System.out.println("Stub function.");
  }

}