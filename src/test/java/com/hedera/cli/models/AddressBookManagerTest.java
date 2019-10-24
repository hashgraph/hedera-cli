package com.hedera.cli.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.hedera.cli.hedera.utils.DataDirectory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AddressBookManagerTest {

  private final PrintStream stdout = System.out;
  private final ByteArrayOutputStream output = new ByteArrayOutputStream();
  private List<String> outputResultArray;

  @TempDir
  public Path tempDir;

  private DataDirectory dataDirectory;

  private AddressBookManager addressBookManager;

  @BeforeEach
  public void setUp() throws UnsupportedEncodingException {
    // ensures that System.out is captured by output
    System.setOut(new PrintStream(output, true, "UTF-8"));

    // prepare our AddressBookManager with test data
    addressBookManager = new AddressBookManager();
    // test data
    String accountId = "0.0.1234";
    String randFileName = "mushy_daisy_4820";
    // we manually invoke new DataDirectory as a real object
    dataDirectory = new DataDirectory();
    // then, we use the tempDir as its actual data directory
    dataDirectory.setDataDir(tempDir);
    dataDirectory.writeFile("network.txt", "testnet");
    dataDirectory.mkHederaSubDir("testnet/accounts/");
    dataDirectory.writeFile("testnet/accounts/default.txt", randFileName + ":" + accountId);
    addressBookManager.setDataDirectory(dataDirectory);

    // since we are manually instantiating AddressBookManager with new,
    // we have to manually invoke init() in order to parse our default
    // addressbook.jsom
    addressBookManager.init();
  }

  @AfterEach
  public void tearDown() {
    System.setOut(stdout);
  }

  @Test
  public void listNetworks() {
    assertNotNull(addressBookManager);

    addressBookManager.listNetworks();

    // after addressBookManager.listNetworks() is executed, we retrieve the captured
    // output
    String outputResult = new String(output.toByteArray());
    outputResultArray = Arrays.asList(outputResult.split("\n"));
    outputResultArray.stream().map(s -> s.trim()).collect(Collectors.toList());

    assertThat(outputResultArray, containsInAnyOrder(
      "  mainnet",
      "* testnet"
    ));
  }

  @Test
  public void getDefaultAccount() {
    assertEquals("mushy_daisy_4820:0.0.1234", addressBookManager.getDefaultAccount());

    // delete the default.txt file and we will no longer have a default account
    String currentNetwork = dataDirectory.readFile("network.txt", "testnet");
    String pathToDefaultAccount = currentNetwork + File.separator + "accounts" + File.separator + "default.txt";
    Paths.get(tempDir.toString(), pathToDefaultAccount).toFile().delete();
    assertEquals("", addressBookManager.getDefaultAccount());
  }

}