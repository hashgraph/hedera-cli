package com.hedera.cli;

import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class HederaNetwork {

  @Autowired
  ShellHelper shellHelper;
  
  @ShellMethod(value = "switch and manage different Hedera network")
  public void network() {
    System.out.println("Stub function.");
  }

  @ShellMethod(value = "list available Hedera network")
  public void list() {
    System.out.println("Stub function.");
  }
}