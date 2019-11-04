package com.hedera.cli.hedera.network;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

  @Test
  public void testListNetwork() {
    networkList.run();

    verify(addressBookManager, times(1)).listNetworks();
  }

}