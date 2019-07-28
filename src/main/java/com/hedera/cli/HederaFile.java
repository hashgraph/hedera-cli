package com.hedera.cli;

import com.hedera.cli.hedera.file.File;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class HederaFile {

  @Autowired
  ShellHelper shellHelper;
  
  @ShellMethod(value = "manage hedera file")
  public void file(
          @ShellOption(defaultValue = "") String subCommand,
          @ShellOption(defaultValue = "", arity = -1) String... args) {
      File file = new File();
      file.handle(subCommand, args);
  }
}