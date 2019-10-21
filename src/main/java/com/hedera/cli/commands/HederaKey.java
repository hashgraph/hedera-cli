package com.hedera.cli.commands;

import com.hedera.cli.hedera.keygen.KeyGeneration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class HederaKey {

  @Autowired
  private KeyGeneration keyGeneration;

  @ShellMethod(value = "generate private and public keypair for Hedera account(s)")
  public void keygen() {
    keyGeneration.run();
  }

}