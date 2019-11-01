package com.hedera.cli.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.hedera.cli.models.DataDirectory;
import com.hedera.cli.shell.ShellHelper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.FileSystemUtils;

@ExtendWith(MockitoExtension.class)
public class DataDirectoryTest {

  @TempDir
  public Path tempDir;

  @InjectMocks
  private DataDirectory dataDirectory;

  @Mock
  private ShellHelper shellHelper;

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
    assertNotNull(dataDirectory.getShellHelper());
    assertEquals(shellHelper, dataDirectory.getShellHelper());
    
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
      // when mkDataDir method is invoked
      FileSystemUtils.deleteRecursively(tempDir);
      method.setAccessible(true);
      method.invoke(dataDirectory);
      method.setAccessible(false);
      directory = Paths.get(dataDirectory.getDataDir().toString());
      actual = Files.exists(directory);
      expected = true;
      assertEquals(expected, actual);
  }

  @Test
  public void checkFileExists() throws NoSuchMethodException, SecurityException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, IOException {
        
    String pathToSomeTestFile = "somefolder" + File.separator + "somefile.txt";
    
    Method method = dataDirectory.getClass().getDeclaredMethod("checkFileExists", String.class);
    method.setAccessible(true);
    File file = (File) method.invoke(dataDirectory, pathToSomeTestFile);
    assertNull(file);

    // deliberately write this test file
    dataDirectory.writeFile(pathToSomeTestFile, "sometext");
    file = (File) method.invoke(dataDirectory, pathToSomeTestFile);
    assertNotNull(file);
    method.setAccessible(false);
  }

  @Test
  public void writeFileFails() {
    assertThrows(NullPointerException.class, () -> {
      dataDirectory.writeFile(null, "anything");
    });

    // Supplying a pathToFile "/" will cause an IOException that invokes shellHelper.printError
    dataDirectory.writeFile("/", "anything");
    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    verify(shellHelper).printError(valueCapture.capture());
    String actual = valueCapture.getValue();
    String expected = "Failed to save";
    assertEquals(expected, actual);
  }

  @Test
  public void readFile() {
    String testFile = "testfolder" + File.separator + "testfile.txt";

    // write testFile
    dataDirectory.writeFile(testFile, "testValue");
    String actual = dataDirectory.readFile(testFile);
    String expected = "testValue";
    assertEquals(expected, actual);

    String testFile2 = "testfolder" + File.separator + "testfile2.txt";

    // testFile2 is not written
    actual = dataDirectory.readFile(testFile2);
    expected = "";
    assertEquals(expected, actual);
  }

  @Test
  public void readFileWithDefaultValue() {
    String testFile = "testfolder" + File.separator + "testfile.txt";
    String value = dataDirectory.readFile(testFile, "defaultValue");
    assertEquals("defaultValue", value);
  }

  @Test
  public void readFileFails() {
    assertThrows(NullPointerException.class, () -> {
      dataDirectory.readFile(null);
    });

    assertThrows(NullPointerException.class, () -> {
      dataDirectory.readFile(null, "someDefaultValue");
    });
  }

}