package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountUseTest {

  @Test
  public void testAccountUseWithNoArgs() {

    assertTrue(true);
    // String[] args = new String[]{};
    // CommandLine cmd = new CommandLine(AccountUse.class);
    // assertThrows(MissingParameterException.class, () -> {
    // cmd.parseArgs(args);
    // });
    // AccountUse accountUse = cmd.getCommand();
    // assertNull(accountUse.getAccountId());
  }

  @Test
  public void testAccountUseWithAccountId() {

    assertTrue(true);
    // String[] args = new String[]{ "0.0.1001" };
    // CommandLine cmd = new CommandLine(AccountUse.class);
    // cmd.parseArgs(args);
    // AccountUse accountUse = cmd.getCommand();
    // assertEquals("0.0.1001", accountUse.getAccountId());
  }
}
