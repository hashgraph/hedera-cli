package com.hedera.cli.commands;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.setup.Setup;
import com.hedera.cli.models.AddressBookManager;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import picocli.CommandLine;

@ShellComponent(value = "Get Started")
public class GetStarted {

  @Autowired
  ShellHelper shellHelper;

  @Autowired
  InputReader inputReader;

  @Autowired
  AddressBookManager addressBookManager;

  @Autowired
  Setup setup;

  @ShellMethod(value = "initialise a Hedera account as the default operator")
  public void setup() {
    String defaultAccount = addressBookManager.getDefaultAccount();
    if (defaultAccount.isEmpty()) {
      System.out.println("defaultAccount does not exist");
      setup.handle(inputReader, shellHelper);
    } else {
      System.out.println("defaultAccount already exists");
      CommandLine.usage(this, System.out);
    }
  }

}