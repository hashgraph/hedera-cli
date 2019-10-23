package com.hedera.cli.hedera.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DataDirectoryTest {

  @TempDir 
  public Path tempDir;

  @InjectMocks
  private DataDirectory dataDirectory;


  @BeforeEach
  public void setup() {
    // System.out.println(tempDir.toAbsolutePath().toString());
    dataDirectory.setDataDir(tempDir);
  }

  @AfterEach
  public void teardown() {
    File tempDirFolder = new File(tempDir.toString());
    tempDirFolder.delete();
    System.out.println(tempDirFolder.exists());
  }

  @Test
  public void init() {

    assertNotNull(dataDirectory);

    // Prove that we can swap in a temporary directory to replace "~/.hedera" for executing of our tests
    // We can also destroy the temporary directory after all tests
    assertEquals(tempDir.toAbsolutePath().toString(), dataDirectory.getDataDir().toAbsolutePath().toString());

  }

}