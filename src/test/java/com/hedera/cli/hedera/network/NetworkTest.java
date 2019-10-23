package com.hedera.cli.hedera.network;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.MatcherAssert.assertThat;

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
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NetworkTest {

  private final PrintStream stdout = System.out;
  private final ByteArrayOutputStream output = new ByteArrayOutputStream();
  private List<String> outputResultArray;

  @InjectMocks
  private Network network;

  @BeforeEach
  public void setup() throws UnsupportedEncodingException {
    // ensures that System.out is captured by output
    System.setOut(new PrintStream(output, true, "UTF-8"));
  }

  @AfterEach
  public void teardown() {
    System.setOut(stdout);
  }

  @Test
  public void run() {
    assertNotNull(network);

    network.run();

    // after network.run() is executed, we retrieve the captured output
    String outputResult = new String(output.toByteArray());
    outputResultArray = Arrays.asList(outputResult.split("\n"));
    outputResultArray.stream().map(s -> s.trim()).collect(Collectors.toList());
    // our captured output should tally with this expected usage results
    assertThat(outputResultArray, containsInAnyOrder(
      "Usage: network [COMMAND]",
      "List and set the network in use",
      "Commands:",
      "  ls   List all available Hedera network.",
      "  use  Set specific Hedera network we will use."));
  }

}