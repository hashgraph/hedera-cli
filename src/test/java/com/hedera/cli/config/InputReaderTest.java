package com.hedera.cli.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.junit.jupiter.api.Test;



public class InputReaderTest {

  @Test
  public void emptyStringGivesEOFNoJna() throws EndOfFileException, UserInterruptException {
    LineReader lineReader = mock(LineReader.class);
    when(lineReader.readLine()).thenReturn("user's answer");

    InputReader i = new InputReader(lineReader);

    // general question
    when(lineReader.readLine(eq("Continue? y/n: "))).thenReturn("y");
    String answer = i.prompt("Continue? y/n");
    assertEquals("y", answer);

    // general question with default
    when(lineReader.readLine(eq("Continue? y/n [defaults to 'n']: "))).thenReturn("");
    String answer2 = i.prompt("Continue? y/n [defaults to 'n']", "n");
    assertEquals("n", answer2);

    // do not echo when accepting sensitive user answers (e.g. passwords or keys)
    when(lineReader.readLine(eq("Password?: "), eq("*".charAt(0)))).thenReturn("**********");
    String answer3 = i.prompt("Password?", null, false);
    assertEquals("**********", answer3);

    InputReader i2 = new InputReader(lineReader, "#".charAt(0));
    when(lineReader.readLine(eq("Password?: "), eq("#".charAt(0)))).thenReturn("##########");
    String answer4 = i2.prompt("Password?", null, false);
    assertEquals("##########", answer4);
  }

}
