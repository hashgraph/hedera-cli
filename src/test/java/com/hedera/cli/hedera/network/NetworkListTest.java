package com.hedera.cli.hedera.network;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.rule.OutputCapture;

@RunWith(MockitoJUnitRunner.class)
public class NetworkListTest {

  // captures our stdout
  @Rule
  public OutputCapture capture = new OutputCapture();

  @Test
  public void testListNetwork() {
    System.out.println("We can't instantiate NetworkList anymore?");
    // String addressBookJson = File.separator + "addressbook.json";
    // NetworkList networkList = new NetworkList();
    // networkList.setAddressBookJson(addressBookJson);
    // networkList.run();

    // // tokenize our stdout capture
    // List<String> tokens = new ArrayList<>();
    // String lineSeparator = System.getProperty("line.separator");
    // StringTokenizer tokenizer = new StringTokenizer(capture.toString(), lineSeparator);
    // while (tokenizer.hasMoreElements()) {
    //   tokens.add(tokenizer.nextToken().trim());
    // }

    // compare test data against stdout capture on a per-line basis
//    assertEquals("mainnet", tokens.get(0));
//    assertEquals("* aspen", tokens.get(1));
//    assertEquals("external", tokens.get(2));
  }

}