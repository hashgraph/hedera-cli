package com.hedera.cli.commands;

import static org.mockito.Mockito.verify;

import com.hedera.cli.shell.ShellHelper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HederaContractTest {

  @InjectMocks
  private HederaContract hederaContract;

  @Mock
  private ShellHelper shellHelper;

  @Test
  public void consensus() {
    hederaContract.contract();

    verify(shellHelper).printInfo("Stub function.");
  }


}