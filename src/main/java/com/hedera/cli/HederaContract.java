package com.hedera.cli;

import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class HederaContract {

  @Autowired
  ShellHelper shellHelper;

  @ShellMethod(value = "manage Solidity contracts on Hedera")
  public void contract() {
      System.out.println("Stub function.");
  }

}