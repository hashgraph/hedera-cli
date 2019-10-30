package com.hedera.cli.models;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class AddressBookManagerTest {

  private final PrintStream stdout = System.out;
  private final ByteArrayOutputStream output = new ByteArrayOutputStream();

  @TempDir
  public Path tempDir;

  private DataDirectory dataDirectory;

  @InjectMocks
  private AddressBookManager addressBookManager;

  @Mock
  private ShellHelper shellHelper;

  @BeforeEach
  public void setUp() throws UnsupportedEncodingException, IOException {

    // ensures that System.out is captured by output
    System.setOut(new PrintStream(output, true, "UTF-8"));

    // prepare our AddressBookManager with test data
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
  }

  @AfterEach
  public void tearDown() {
    System.setOut(stdout);
  }

  @Test
  public void listNetworks() {
    assertNotNull(addressBookManager);

    // since we are manually instantiating AddressBookManager with new,
    // we have to manually invoke init() in order to parse our default
    // addressbook.jsom
    addressBookManager.init();

    addressBookManager.listNetworks();

    // Retrieve the captured output
    List<String> outputResultArray = captureSystemOut();

    assertThat(outputResultArray, containsInAnyOrder("  mainnet", "* testnet"));
  }

  @Test
  public void listNetworksFail() {
    ReflectionTestUtils.setField(addressBookManager, "ADDRESSBOOK_DEFAULT", "nosuchaddressbook.json");
    
    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    doNothing().when(shellHelper).printError(valueCapture.capture());

    addressBookManager.init();
    
    String actual = valueCapture.getValue();
    String expected = "argument \"src\" is null";

    assertEquals(expected, actual);

    ReflectionTestUtils.setField(addressBookManager, "ADDRESSBOOK_DEFAULT", "addressbook.json");
  }

  @Test
  public void gettersAndSetters() {
    System.setOut(stdout);
    addressBookManager.init();

    List<Network> networks = addressBookManager.getNetworks();
    assertEquals(2, networks.size());

    Network currentNetwork = addressBookManager.getCurrentNetwork();
    assertEquals("testnet", currentNetwork.getName());

    // deliberately remove testnet from network list
    for (Iterator<Network> iter = networks.listIterator(); iter.hasNext();) {
      Network n = iter.next();
      if ("testnet".equals(n.getName())) {
        iter.remove();
      }
    }
    addressBookManager.setNetworks(networks);
    currentNetwork = addressBookManager.getCurrentNetwork();
    assertNull(currentNetwork);

    // deliberately setNetworks to null
    addressBookManager.setNetworks(null);
    currentNetwork = addressBookManager.getCurrentNetwork();
    assertNull(currentNetwork);

    dataDirectory = addressBookManager.getDataDirectory();
    assertNotNull(dataDirectory);

    shellHelper = addressBookManager.getShellHelper();
    assertNotNull(shellHelper);
  }

  private List<String> captureSystemOut() {
    String outputResult = new String(output.toByteArray());
    List<String> outputResultArray = Arrays.asList(outputResult.split("\n"));
    outputResultArray.stream().map(s -> s.trim()).collect(Collectors.toList());
    return outputResultArray;
  }

}