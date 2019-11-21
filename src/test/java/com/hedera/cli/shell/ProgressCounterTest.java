package com.hedera.cli.shell;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProgressCounterTest {

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
  public void progressCounter() throws IOException {
    InputStream mockIn = mock(InputStream.class);
    TerminalBuilder t = TerminalBuilder.builder();
    Terminal terminal = t.jna(false).streams(mockIn, output).build();
    ProgressCounter p = new ProgressCounter(terminal);

    List<String> allLines = new ArrayList<String>();
    for (int i = 1; i <= 100; i++) {
      p.display();
      String line = captureLine();
      allLines.add(line);
    }
    p.reset();
    // System.setOut(stdout);
    // System.out.println(allLines.size());
    assertEquals(100, allLines.size());
  }

  @Test
  public void progressCounterWithCountAndMessage() throws IOException {
    InputStream mockIn = mock(InputStream.class);
    TerminalBuilder t = TerminalBuilder.builder();
    Terminal terminal = t.jna(false).streams(mockIn, output).build();
    ProgressCounter p = new ProgressCounter(terminal);

    List<String> allLines = new ArrayList<String>();
    for (int i = 1; i <= 100; i++) {
      p.display(i, "hello");
      String line = captureLine();
      allLines.add(line);
    }
    p.reset();

    int length = allLines.size();
    String allLinesString = allLines.get(length - 1);
    String[] allLinesArray = allLinesString.trim().split("\n");
    String lastLine = allLinesArray[allLinesArray.length - 1];
    String[] lastLineArray = lastLine.trim().split(" ");
    // ? character gets added in randomly, so we explicitly remove it if it exists
    String lastWord = lastLineArray[lastLineArray.length - 1].trim().replaceAll("\\p{C}+", "");
    String secondLastWord = lastLineArray[lastLineArray.length - 2].trim().replaceAll("\\p{C}+", "");

    assertEquals("100", lastWord);
    assertEquals("hello:", secondLastWord);
  }

  @Test
  public void patternAndSpinner() throws IOException {
    InputStream mockIn = mock(InputStream.class);
    TerminalBuilder t = TerminalBuilder.builder();
    Terminal terminal = t.jna(false).streams(mockIn, output).build();
    ProgressCounter p = new ProgressCounter(terminal);

    char[] spinner = { '|', '/', '-', '\\' };
    String pattern = " %s: %d ";
    assertTrue(Arrays.equals(spinner, p.getSpinner()));
    assertTrue(pattern.equals(p.getPattern()));

    char[] newSpinner = { 'x', '+' };
    String newPattern = " %d %s";
    p.setSpinner(newSpinner);
    p.setPattern(newPattern);
    assertTrue(Arrays.equals(newSpinner, p.getSpinner()));
    assertTrue(newPattern.equals(p.getPattern()));
  }

  @Test
  public void progressCounterWithPatternAndSpinner() throws IOException {
    InputStream mockIn = mock(InputStream.class);
    TerminalBuilder t = TerminalBuilder.builder();
    Terminal terminal = t.jna(false).streams(mockIn, output).build();

    char[] newSpinner = { 'x', '+' };
    String newPattern = " %d %s";

    ProgressCounter p = new ProgressCounter(terminal, newPattern);
    assertTrue(newPattern.equals(p.getPattern()));

    ProgressCounter p2 = new ProgressCounter(terminal, newPattern, newSpinner);
    assertTrue(Arrays.equals(newSpinner, p2.getSpinner()));
  }

  private String captureLine() {
    String capturedString = new String(output.toByteArray());
    return capturedString.trim().replaceAll("\\p{C}+", ""); // always escape control characters
  }

}