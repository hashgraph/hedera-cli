package com.hedera.cli.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.setup.Setup;
import com.hedera.cli.shell.ShellHelper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetStartedTest {

  @InjectMocks
  private GetStarted getStarted;

  @Mock
  private ShellHelper shellHelper;

  @Mock
  private Hedera hedera;

  @Mock
  private Setup setup;

  @Test
  public void getStartedNoDefaultAccount() {
    when(hedera.getDefaultAccount()).thenReturn("");

    getStarted.setup();

    verify(setup, times(1)).run();

    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    verify(shellHelper).printInfo(valueCapture.capture());
    String actual = valueCapture.getValue();
    String expected = "default account does not exist";

    assertEquals(expected, actual);
  }

  @Test
  public void getStartedExistinGDefaultAccount() {
    when(hedera.getDefaultAccount()).thenReturn("0.0.1001");

    getStarted.setup();

    verify(setup, times(1)).help();

    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    verify(shellHelper).printInfo(valueCapture.capture());
    String actual = valueCapture.getValue();
    String expected = "\nYou have already setup a default Hedera account.\n"
        + "Use `account recovery` command to import another account\n"
        + "or `account default` command to set a different default account\n"
        + "if you would like to change this default account.\n";

    assertEquals(expected, actual);

  }

}
