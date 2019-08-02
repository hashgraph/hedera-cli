package com.hedera.cli;

import com.hedera.cli.hedera.keygen.KeyGeneration;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class HederaKey {

  @Autowired
  ShellHelper shellHelper;

  @Autowired
  KeyGeneration keyGeneration;

  @ShellMethod(value = "generate private and public keypair for Hedera account(s)")
  public void keygen() {
    KeyGeneration keyGeneration = new KeyGeneration();
    keyGeneration.run();
  }

}