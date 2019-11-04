package com.hedera.cli.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hedera.cli.hedera.keygen.CryptoUtils;
import com.hedera.cli.hedera.keygen.HGCSeed;
import com.hedera.cli.hedera.keygen.KeyGeneration;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;

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

  private final PrintStream stdout = System.out;
  private final ByteArrayOutputStream output = new ByteArrayOutputStream();

  @BeforeEach
  public void setUp() throws UnsupportedEncodingException {
    System.setOut(new PrintStream(output, true, "UTF-8"));
    // this will override dataDir's default value "~/.hedera"
    dataDirectory.setDataDir(tempDir);
  }

  @AfterEach
  public void tearDown() throws IOException {
    System.setOut(stdout);
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

    // Supplying a pathToFile "/" will cause an IOException that invokes
    // shellHelper.printError
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

    // Supplying a pathToFile "/" will cause an IOException that will return empty
    // string
    String value = dataDirectory.readFile("/");
    assertEquals("", value);
  }

  @Test
  public void readWriteToIndexNoData() {
    // prepare data
    String testIndexFile = "testfolder" + File.separator + "index.txt";
    HashMap<String, String> testDefaultMap = new HashMap<String, String>();
    testDefaultMap.put("0.0.1001", "some_random_name_a");
    testDefaultMap.put("0.0.1002", "some_random_name_b");
    testDefaultMap.put("0.0.1003", "some_random_name_c");
    HashMap<String, String> resultMap = dataDirectory.readWriteToIndex(testIndexFile, testDefaultMap);

    assertTrue(resultMap.equals(testDefaultMap));
  }

  @Test
  public void readWriteToIndexWithNewData() {
    // prepare data
    String testIndexFile = "testfolder" + File.separator + "index.txt";
    HashMap<String, String> testDefaultMap = new HashMap<String, String>();
    testDefaultMap.put("0.0.1001", "some_random_name_a");
    testDefaultMap.put("0.0.1002", "some_random_name_b");
    testDefaultMap.put("0.0.1003", "some_random_name_c");
    HashMap<String, String> resultMap = dataDirectory.readWriteToIndex(testIndexFile, testDefaultMap);
    assertEquals(3, resultMap.size());

    // add in a newMap and the number of entries will increase by one
    HashMap<String, String> newMap = new HashMap<String, String>();
    newMap.put("0.0.10014", "some_random_name_d");
    HashMap<String, String> resultMap2 = dataDirectory.readWriteToIndex(testIndexFile, newMap);
    assertEquals(4, resultMap2.size());

    // add in the same newMap and the number of entries in our  map will remain unchanged
    HashMap<String, String> resultMap3 = dataDirectory.readWriteToIndex(testIndexFile, newMap);
    assertEquals(4, resultMap3.size());

    // provide an empty newMap and the number of entries in our map will remain unchanged
    HashMap<String, String> resultMap4 = dataDirectory.readWriteToIndex(testIndexFile, new HashMap<String, String>());
    assertEquals(4, resultMap4.size());

    // write to an invalid index file and we will get a null map back
    HashMap<String, String> resultMap5 = dataDirectory.readWriteToIndex("/", new HashMap<String, String>());
    assertNull(resultMap5);
  }

  @Test
  public void listIndex() throws UnsupportedEncodingException {
    String testIndexFile = "testfolder" + File.separator + "index.txt";
    HashMap<String, String> testDefaultMap = new HashMap<String, String>();
    testDefaultMap.put("0.0.1001", "some_random_name_a");
    testDefaultMap.put("0.0.1002", "some_random_name_b");
    testDefaultMap.put("0.0.1003", "some_random_name_c");
    HashMap<String, String> resultMap = dataDirectory.readWriteToIndex(testIndexFile, testDefaultMap);
    
    dataDirectory.listIndex(testIndexFile);
    List<String> outputResultArray = captureSystemOut();
    System.setOut(stdout);

    HashMap<String, String> outputMap = new HashMap<String, String>();
    for (String line: outputResultArray) {
      String[] kv = line.split("=");
      outputMap.put(kv[0], kv[1]);
    }

    assertTrue(resultMap.equals(testDefaultMap));
    assertTrue(outputMap.equals(testDefaultMap));
  }

  @Test
  public void listIndexFails() {
    dataDirectory.listIndex("/");
    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    verify(shellHelper).printError(valueCapture.capture());
    String actual = valueCapture.getValue();
    String expected = "Unable to read index";
    assertEquals(expected, actual);
  }

  private List<String> captureSystemOut() {
    String outputResult = new String(output.toByteArray());
    List<String> outputResultArray = Arrays.asList(outputResult.split("\n"));
    outputResultArray.stream().map(s -> s.trim()).collect(Collectors.toList());
    return outputResultArray;
  }

  @Test
  public void readIndexToHashmap() {
    String testIndexFile = "testfolder" + File.separator + "index.txt";
    HashMap<String, String> testDefaultMap = new HashMap<String, String>();
    testDefaultMap.put("0.0.1001", "some_random_name_a");
    testDefaultMap.put("0.0.1002", "some_random_name_b");
    testDefaultMap.put("0.0.1003", "some_random_name_c");
    HashMap<String, String> resultMap = dataDirectory.readWriteToIndex(testIndexFile, testDefaultMap);

    HashMap<String, String> map = dataDirectory.readIndexToHashmap(testIndexFile);

    assertTrue(map.equals(resultMap));
  }

  @Test
  public void readIndexToHashmapFails() {
    HashMap<String, String> map = dataDirectory.readIndexToHashmap("/");
    assertNull(map);
  }

  @Test
  public void readJsonToHashmap() {
    // manually setup AccountManager to create a json account on disk
    AccountManager accountManager = new AccountManager();
    accountManager.setDataDirectory(dataDirectory);
    accountManager.setRandomNameGenerator(new RandomNameGenerator());
    accountManager.setShellHelper(shellHelper); // shellHelper is a mock from above
    
    // test data
    KeyPair keypair = prepareKeyPair();
    String testAccountId = "0.0.1001";
    AccountId accountId = AccountId.fromString(testAccountId); 
    accountManager.setDefaultAccountId(accountId, keypair); // writes into dataDir (i.e. tmpDir)

    // This can be simplified by implementing an actual function in AccountManager
    Map<String, String> defaultAccountJson = new HashMap<String, String>();
    String pathToIndexTxt = accountManager.pathToIndexTxt();
    Map<String, String> readingIndexAccount = dataDirectory.readIndexToHashmap(pathToIndexTxt);
    for (Map.Entry<String, String> entry : readingIndexAccount.entrySet()) {
        String key = entry.getKey(); // key refers to the account id
        String value = entry.getValue(); // value refers to the filename json
        if (testAccountId.equals(key)) {
            String pathToCurrentJsonAccount = accountManager.pathToAccountsFolder() + value + ".json";
            defaultAccountJson = dataDirectory.readJsonToHashmap(pathToCurrentJsonAccount);
        }
    }

    // the defaultAccountJson that is read out via readJsonToHashmap must match our expected test data
    assertEquals(testAccountId, defaultAccountJson.get("accountId"));
    assertEquals(keypair.getPrivateKeyHex(), defaultAccountJson.get("privateKey"));
    assertEquals(keypair.getPublicKeyHex(), defaultAccountJson.get("publicKey"));    
  }

  @Test
  public void readJsonToHashmapFails() {
    Map<String, String> defaultAccountJson = dataDirectory.readJsonToHashmap("/");
    assertNull(defaultAccountJson);
  }

  private KeyPair prepareKeyPair() {
    KeyGeneration keyGeneration = new KeyGeneration("bip");
    HGCSeed hgcSeed = new HGCSeed((CryptoUtils.getSecureRandomData(32)));
    List<String> mnemonic = keyGeneration.generateMnemonic(hgcSeed);
    KeyPair keypair = keyGeneration.generateKeysAndWords(hgcSeed, mnemonic);
    return keypair;
}

}