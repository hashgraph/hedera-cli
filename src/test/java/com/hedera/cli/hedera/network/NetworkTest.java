package com.hedera.cli.hedera.network;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;

import picocli.CommandLine;

@ExtendWith(MockitoExtension.class)
public class NetworkTest {

  private final PrintStream stdout = System.out;
  private final ByteArrayOutputStream output = new ByteArrayOutputStream();

  @InjectMocks
  private Network network;

  @Mock
  private NetworkList networkList;

  @Mock
  private NetworkUse networkUse;

  private List<String> expected = Arrays.asList("Usage: network [COMMAND]", "List and set the network in use",
      "Commands:", "ls   List all available Hedera network.", "use  Set specific Hedera network we will use.");

  @BeforeEach
  public void setUp() throws UnsupportedEncodingException {
    // ensures that System.out is captured by output
    System.setOut(new PrintStream(output, true, "UTF-8"));
  }

  @AfterEach
  public void tearDown() {
    System.setOut(stdout);
  }

  @Test
  public void run() {
    assertNotNull(network);

    network.run();

    // after network.run() is executed, we retrieve the captured output
    String outputResult = new String(output.toByteArray());
    List<String> outputResultArray = Arrays.asList(outputResult.split("\n"));
    outputResultArray.stream().map(s -> s.trim()).collect(Collectors.toList());
    outputResultArray.replaceAll(s -> s.trim());
    // our captured output should tally with this expected usage results
    assertThat(outputResultArray, containsInAnyOrder(expected.toArray()));
  }

  @Test
  public void handleOthers() {
    CommandLine networkListCmd = new CommandLine(networkList);
    networkListCmd = spy(networkListCmd);
    network.setNetworkListCmd(networkListCmd);
    
    network.handle("randominvalidcommand");

    // after network.run() is executed, we retrieve the captured output
    String outputResult = new String(output.toByteArray());
    List<String> outputResultArray = Arrays.asList(outputResult.split("\n"));
    outputResultArray.stream().map(s -> s.trim()).collect(Collectors.toList());
    outputResultArray.replaceAll(s -> s.trim());
    // our captured output should tally with this expected usage results
    assertThat(outputResultArray, containsInAnyOrder(expected.toArray()));
  }

  @Test
  public void handleList() {
    CommandLine networkListCmd = new CommandLine(networkList);
    networkListCmd = spy(networkListCmd);
    network.setNetworkListCmd(networkListCmd);
    
    network.handle("ls");

    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    verify(networkListCmd).execute(valueCapture.capture());
    assertThrows(MockitoException.class, () -> { 
      valueCapture.getValue();
    });
  }

  @Test
  public void handleListWithAdditionalEmptyString() {
    CommandLine networkListCmd = new CommandLine(networkList);
    networkListCmd = spy(networkListCmd);
    network.setNetworkListCmd(networkListCmd);
    
    network.handle("ls", "");

    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    verify(networkListCmd).execute(valueCapture.capture());
    String actual = valueCapture.getValue();
    String expected = "";
    assertEquals(expected, actual);
  }

  @Test
  public void handleUse() {
    CommandLine networkUseCmd = new CommandLine(networkUse);
    networkUseCmd = spy(networkUseCmd);
    network.setNetworkUseCmd(networkUseCmd);
    
    network.handle("use");

    String outputResult = new String(output.toByteArray());
    List<String> outputResultArray = Arrays.asList(outputResult.split("\n"));
    outputResultArray.stream().map(s -> s.trim()).collect(Collectors.toList());
    outputResultArray.replaceAll(s -> s.trim());
    List<String> expected = Arrays.asList("Usage: use <name>", "Set specific Hedera network we will use.",
        "<name>   Name of the network", "Usage:", "network use mainnet");
    assertThat(outputResultArray, containsInAnyOrder(expected.toArray()));
  }

  @Test
  public void handleUseNetwork() {
    CommandLine networkUseCmd = new CommandLine(networkUse);
    networkUseCmd = spy(networkUseCmd);
    network.setNetworkUseCmd(networkUseCmd);
    
    network.handle("use", "mainnet");

    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    verify(networkUseCmd).execute(valueCapture.capture());
    String actual = valueCapture.getValue();
    String expected = "mainnet";
    assertEquals(expected, actual);
  }

}