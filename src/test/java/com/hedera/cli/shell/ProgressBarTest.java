package com.hedera.cli.shell;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class ProgressBarTest {

  private final PrintStream stdout = System.out;
  private final ByteArrayOutputStream output = new ByteArrayOutputStream();

  @BeforeEach
  public void setUp() throws UnsupportedEncodingException, IOException {
    System.setOut(new PrintStream(output, true, "UTF-8"));
  }

  @AfterEach
  public void tearDown() throws IOException {
    System.setOut(stdout);
  }

  @Test
  public void progressBar() throws InterruptedException, IOException {
    InputStream mockIn = mock(InputStream.class);
    TerminalBuilder t = TerminalBuilder.builder();
    Terminal terminal = t.jna(false).streams(mockIn, output).build();

    ShellHelper shellHelper = new ShellHelper(terminal);
    ProgressBar progressBar = new ProgressBar(shellHelper);

    List<String> allLines = new ArrayList<String>();
    for (int i = 1; i <= 100; i++) {
      progressBar.display(i);      
      // we do not use Thread.sleep in tests
      // Thread.sleep(100);
      String line = captureLine();
      allLines.add(line);
    }
    progressBar.reset();

    int length = allLines.size();
    String lastLine = allLines.get(length - 1).trim();
    String[] lastLineArray = lastLine.stripTrailing().split(" ");
    String last = lastLineArray[lastLineArray.length - 1];

    assertEquals(100, length);
    assertEquals("100%", last);
  }

  @Test
  public void progressBarWithMessage() throws InterruptedException, IOException {
    String testMessage = "hello";
    InputStream mockIn = mock(InputStream.class);
    TerminalBuilder t = TerminalBuilder.builder();
    Terminal terminal = t.jna(false).streams(mockIn, output).build();

    ShellHelper shellHelper = new ShellHelper(terminal);
    ProgressBar progressBar = new ProgressBar(shellHelper);

    List<String> allLines = new ArrayList<String>();
    for (int i = 1; i <= 100; i++) {
      progressBar.display(i, testMessage);      
      // we do not use Thread.sleep in tests
      // Thread.sleep(100);
      String line = captureLine();
      allLines.add(line);
    }
    progressBar.reset();

    int length = allLines.size();
    String lastLine = allLines.get(length - 1).trim();
    String[] lastLineArray = lastLine.stripTrailing().split(" ");
    String last = lastLineArray[lastLineArray.length - 1];

    assertEquals(100, length);
    assertEquals(testMessage, last);
  }

  private String captureLine() {
    return new String(output.toByteArray());
  }

}