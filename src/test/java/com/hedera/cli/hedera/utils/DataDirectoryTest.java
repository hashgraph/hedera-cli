package com.hedera.cli.hedera.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DataDirectoryTest {

  @InjectMocks
  private DataDirectory dataDirectory;

  @Test
  public void init() {
    assertNotNull(dataDirectory);
  }

}