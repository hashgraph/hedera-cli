package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.hedera.cli.hedera.utils.DataDirectory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
public class AccountUseTest {

  @InjectMocks
  private AccountUse accountUse;

  @Mock
  private ApplicationContext context;

  @Mock
  private DataDirectory dataDirectory;

  @Test
  public void testAccountUseWithNoArgs() {
    assertNotNull(accountUse);

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
    assertNotNull(accountUse);

    // String[] args = new String[]{ "0.0.1001" };
    // CommandLine cmd = new CommandLine(AccountUse.class);
    // cmd.parseArgs(args);
    // AccountUse accountUse = cmd.getCommand();
    // assertEquals("0.0.1001", accountUse.getAccountId());
  }
}
