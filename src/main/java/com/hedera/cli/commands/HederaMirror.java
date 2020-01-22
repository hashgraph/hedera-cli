package com.hedera.cli.commands;

import com.hedera.cli.defaults.CliDefaults;
import com.hedera.cli.hedera.mirror.Mirror;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class HederaMirror extends CliDefaults {

  @Autowired
  private Mirror mirror;

  @ShellMethod(value = "introspect Hedera mirror nodes")
  public void mirror(@ShellOption(defaultValue = "") String subCommand) {
    mirror.handle(subCommand);
  }
}
