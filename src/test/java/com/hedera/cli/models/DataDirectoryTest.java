package com.hedera.cli.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.hedera.cli.models.DataDirectory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.FileSystemUtils;

@ExtendWith(MockitoExtension.class)
public class DataDirectoryTest {

  @TempDir
  public Path tempDir;

  @InjectMocks
  private DataDirectory dataDirectory;

  @BeforeEach
  public void setUp() {
    // this will override dataDir's default value "~/.hedera"
    dataDirectory.setDataDir(tempDir);
  }

  @AfterEach
  public void tearDown() throws IOException {
    FileSystemUtils.deleteRecursively(tempDir);
  }

  @Test
  public void init() {
    assertNotNull(dataDirectory);
    // Prove that tempDir's value has been set to dataDir
    String actual = tempDir.toAbsolutePath().toString();
    String expected = dataDirectory.getDataDir().toAbsolutePath().toString();
    assertEquals(expected, actual);
  }

  @Test
  public void mkHederaSubDir() {
    String pathToSubDir = "randomdir" + File.separator + "anotherrandomdir";
    boolean created = dataDirectory.mkHederaSubDir(pathToSubDir);
    assertTrue(created);
  }

  @Test
  public void mkDataDir() throws NoSuchMethodException, SecurityException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, IOException {
      Method method = dataDirectory.getClass().getDeclaredMethod("mkDataDir");
      method.setAccessible(true);
      method.invoke(dataDirectory);
      method.setAccessible(false);

      Path directory = Paths.get(dataDirectory.getDataDir().toString());
      boolean actual = Files.exists(directory);
      boolean expected = true;
      assertEquals(expected, actual);

      // deliberately delete dataDir, but our dataDir will still exist
      FileSystemUtils.deleteRecursively(tempDir);
      method.setAccessible(true);
      method.invoke(dataDirectory);
      method.setAccessible(false);
      directory = Paths.get(dataDirectory.getDataDir().toString());
      actual = Files.exists(directory);
      expected = true;
      assertEquals(expected, actual);
  }

}