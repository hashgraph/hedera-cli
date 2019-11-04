package com.hedera.cli.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStream;

import com.hedera.cli.shell.ProgressBar;
import com.hedera.cli.shell.ProgressCounter;
import com.hedera.cli.shell.ShellHelper;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.junit.jupiter.api.Test;

public class SpringShellConfigTest {

  @Test
  public void config() throws IOException {
    SpringShellConfig config = new SpringShellConfig();

    InputStream mockIn = mock(InputStream.class);
    TerminalBuilder t = TerminalBuilder.builder();
    Terminal terminal = t.jna(false).streams(mockIn, System.out).build();

    ShellHelper shellHelper = config.shellHelper(terminal);
    assertNotNull(shellHelper);

    ProgressBar progressBar = config.progressBar(shellHelper);
    assertNotNull(progressBar);

    ProgressCounter progressCounter = config.progressCounter(terminal);
    assertNotNull(progressCounter);

    
  }


}