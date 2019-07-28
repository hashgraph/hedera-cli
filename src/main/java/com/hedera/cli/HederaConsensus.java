package com.hedera.cli;

import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class HederaConsensus {

  @Autowired
  ShellHelper shellHelper;
  
  @ShellMethod(value = "manage Hedera consensus service")
  public void consensus() {
    System.out.println("Stub function.");
  }

}
