package com.hedera.cli.hedera.crypto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import picocli.CommandLine;
import picocli.CommandLine.MissingParameterException;

@ExtendWith(MockitoExtension.class)
public class AccountUseTest {

  @Test
  public void testAccountUseWithNoArgs() {
    String[] args = new String[]{};
    CommandLine cmd = new CommandLine(AccountUse.class);
    assertThrows(MissingParameterException.class, () -> {
        cmd.parseArgs(args);
    });
    AccountUse accountUse = cmd.getCommand();
    assertNull(accountUse.getAccountId());
  }

  @Test
  public void testAccountUseWithAccountId() {
    String[] args = new String[]{ "0.0.1001" };
    CommandLine cmd = new CommandLine(AccountUse.class);
    cmd.parseArgs(args);
    AccountUse accountUse = cmd.getCommand();
    assertEquals("0.0.1001", accountUse.getAccountId());
  }
}
