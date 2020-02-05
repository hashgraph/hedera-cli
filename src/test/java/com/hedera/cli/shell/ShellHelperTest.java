package com.hedera.cli.shell;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ShellHelperTest {

  private final PrintStream stdout = System.out;
  private final ByteArrayOutputStream output = new ByteArrayOutputStream();

  // ansi escape code reference https://gist.github.com/dainkaplan/4651352
  private final Map<String, String> ANSI = ImmutableMap.<String, String>builder() // @formatter:off
    .put("BLACK", "\u001b[30m")
    .put("RED", "\u001b[31m")
    .put("GREEN", "\u001b[32m")
    .put("YELLOW", "\u001b[33m")
    .put("BLUE", "\u001b[34m")
    .put("MAGENTA", "\u001b[35m")
    .put("CYAN", "\u001b[36m")
    .put("WHITE", "\u001b[37m")
    .put("BRIGHT", "\u001b[36m")
    .put("RESET", "\u001b[0m")
    .build(); // @formatter:on

  private Terminal terminal;

  private ShellHelper shellHelper2;
  
  private String testMessage = "the message";

  @BeforeEach
  public void setUp() throws IOException {
    TerminalBuilder t = TerminalBuilder.builder();
    terminal = t.name("test name").jna(false).streams(System.in, System.out).build();

    InputStream mockIn = mock(InputStream.class);
    Terminal terminal2 = t.jna(false).streams(mockIn, output).build();
    shellHelper2 = new ShellHelper(terminal2);

    System.setOut(new PrintStream(output, true, "UTF-8"));
  }

  @AfterEach
  public void tearDown() {
    terminal = null;
    System.setOut(stdout);
  }

  @Test
  public void shellHelperColors() throws IOException { // @formatter:off
    ShellHelper shellHelper = new ShellHelper(terminal);
    assertEquals("CYAN", shellHelper.infoColor);
    assertEquals("GREEN", shellHelper.successColor);
    assertEquals("YELLOW", shellHelper.warningColor);
    assertEquals("RED", shellHelper.errorColor);
  } // @formatter:on

  @Test
  public void getColored() {
    ShellHelper shellHelper = new ShellHelper(terminal);

    String testMessage = "the message";

    assertEquals(ANSI.get("BLACK") + testMessage + ANSI.get("RESET"),
        shellHelper.getColored(testMessage, PromptColor.BLACK));
    assertEquals(ANSI.get("RED") + testMessage + ANSI.get("RESET"),
        shellHelper.getColored(testMessage, PromptColor.RED));
    assertEquals(ANSI.get("GREEN") + testMessage + ANSI.get("RESET"),
        shellHelper.getColored(testMessage, PromptColor.GREEN));
    assertEquals(ANSI.get("YELLOW") + testMessage + ANSI.get("RESET"),
        shellHelper.getColored(testMessage, PromptColor.YELLOW));
    assertEquals(ANSI.get("BLUE") + testMessage + ANSI.get("RESET"),
        shellHelper.getColored(testMessage, PromptColor.BLUE));
    assertEquals(ANSI.get("MAGENTA") + testMessage + ANSI.get("RESET"),
        shellHelper.getColored(testMessage, PromptColor.MAGENTA));
    assertEquals(ANSI.get("CYAN") + testMessage + ANSI.get("RESET"),
        shellHelper.getColored(testMessage, PromptColor.CYAN));
    assertEquals(ANSI.get("WHITE") + testMessage + ANSI.get("RESET"),
        shellHelper.getColored(testMessage, PromptColor.WHITE));
    assertEquals(ANSI.get("BRIGHT") + testMessage + ANSI.get("RESET"),
        shellHelper.getColored(testMessage, PromptColor.BRIGHT));
  }

  @Test
  public void testInfoSuccessWarningError() throws IOException { // @formatter:off

    ShellHelper shellHelper = new ShellHelper(terminal);
    String testMessage = "the message";
    assertEquals(ANSI.get("CYAN") + testMessage + ANSI.get("RESET"),
      shellHelper.getInfoMessage(testMessage));
    assertEquals(ANSI.get("GREEN") + testMessage + ANSI.get("RESET"),
      shellHelper.getSuccessMessage(testMessage));
    assertEquals(ANSI.get("YELLOW") + testMessage + ANSI.get("RESET"),
      shellHelper.getWarningMessage(testMessage));
    assertEquals(ANSI.get("RED") + testMessage + ANSI.get("RESET"),
      shellHelper.getErrorMessage(testMessage));
  } // @formatter:on

  @Test
  public void print() throws IOException {
    // print uses default color
    shellHelper2.print(testMessage); 
    String line = captureLine();
    assertEquals(testMessage, line);
  }

  @Test
  public void printInfo() {
    shellHelper2.printInfo(testMessage);
    String ansiLine = ANSI.get("CYAN") + testMessage + ANSI.get("RESET");
    String expected = ansiLine.trim().replaceAll("\\p{C}+", "");
    String actual = captureLine();
    assertEquals(expected, actual);
  }

  @Test
  public void printSuccess() {
    String ansiLine = ANSI.get("GREEN") + testMessage + ANSI.get("RESET");
    String expected = ansiLine.trim().replaceAll("\\p{C}+", "");

    shellHelper2.printSuccess(testMessage);
    String actual = captureLine();

    assertEquals(expected, actual);
  }

  @Test
  public void printWarning() {
    shellHelper2.printWarning(testMessage);
    String ansiLine = ANSI.get("YELLOW") + testMessage + ANSI.get("RESET");
    String expected = ansiLine.trim().trim().replaceAll("\\p{C}+", "");
    String actual = captureLine();
    assertEquals(expected, actual);
  }

  @Test
  public void printError() {
    shellHelper2.printError(testMessage);
    String ansiLine = ANSI.get("RED") + testMessage + ANSI.get("RESET");
    String expected = ansiLine.trim().trim().replaceAll("\\p{C}+", "");
    String actual = captureLine().trim();
    assertEquals(expected, actual);
  }

  private String captureLine() {
    String capturedString = new String(output.toByteArray());
    return capturedString.trim().replaceAll("\\p{C}+", ""); // always escape control characters
  }

}