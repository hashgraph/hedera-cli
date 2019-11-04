package com.hedera.cli.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

import java.util.List;

import com.hedera.cli.hedera.crypto.Account;
import com.hedera.cli.hedera.crypto.Transfer;
import com.hedera.cli.shell.ShellHelper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HederaCryptoTest {

  @InjectMocks
  private HederaCrypto hederaCrypto;

  @Mock
  private ShellHelper shellHelper;

  @Mock
  private Account account;

  @Mock
  private Transfer transfer;

  @Test
  public void accountCreate() {
    assertNotNull(shellHelper);

    // account create
    hederaCrypto.account("create", "", true, "", true, "", false, "", "");

    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> valueCapture2 = ArgumentCaptor.forClass(String.class);
    verify(account).handle(valueCapture.capture(), valueCapture2.capture());
    
    String actual = valueCapture.getValue();
    String expected = "create";
    assertEquals(expected, actual);

    List<String> varArgs = valueCapture2.getAllValues();
    String actual2 = varArgs.get(0);
    String expected2 = "-k";
    assertEquals(expected2, actual2);
  }

  @Test
  public void transfer() {
    assertNotNull(transfer);
  }

}