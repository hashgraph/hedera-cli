package com.hedera.cli.hedera.network;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.hedera.cli.models.DataDirectory;
import com.hedera.cli.shell.ShellHelper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NetworkUseTest {

  @InjectMocks
  private NetworkUse networkUse;

  @Mock
  private DataDirectory dataDirectory;

  @Mock
  private ShellHelper shellHelper;

  @Test
  public void run() {
    networkUse.run();

    verify(shellHelper, times(1)).printSuccess(anyString());
    verify(dataDirectory, times(1)).writeFile(anyString(), any());
  }
  
}