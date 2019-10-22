package com.hedera.cli.hedera.network;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.hedera.cli.models.AddressBookManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NetworkListTest {

  @InjectMocks
  private NetworkList networkList;

  @Mock
  private AddressBookManager addressBookManager;

  // captures our stdout
  // public OutputCaptureRule capture = new OutputCaptureRule();

  @Test
  public void testListNetwork() {
    assertNotNull(networkList);
    // how do we capture the output?
    addressBookManager.listNetworks();
    
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