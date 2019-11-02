package com.hedera.cli.shell;

import java.io.IOException;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.junit.jupiter.api.Test;

public class ShellHelperTest {

  @Test
  public void shellHelperColors() throws IOException {
    TerminalBuilder t = TerminalBuilder.builder();
    Terminal terminal = t.jna(false).streams(System.in, System.out).build();
    ShellHelper shellHelper = new ShellHelper(terminal);
    System.out.println(shellHelper.infoColor);
  }

}