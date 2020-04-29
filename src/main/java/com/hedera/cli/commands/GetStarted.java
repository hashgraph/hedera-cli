package com.hedera.cli.commands;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.setup.Setup;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent(value = "Get Started")
public class GetStarted extends CommandBase {

  @Autowired
  private ShellHelper shellHelper;

  @Autowired
  private Hedera hedera;

  @Autowired
  private Setup setup;

  @ShellMethod(value = "initialise a Hedera account as the default operator")
  public void setup() {
    String defaultAccount = hedera.getDefaultAccount();
    if (defaultAccount.isEmpty()) {
      shellHelper.printInfo("default account does not exist");
      setup.run();
    } else {
      shellHelper.printInfo("\nYou have already setup a default Hedera account.\n"
          + "Use `account recovery` command to import another account\n"
          + "or `account default` command to set a different default account\n"
          + "if you would like to change this default account.\n");
      setup.help();
    }
  }

}