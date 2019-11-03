package com.hedera.cli.commands;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.hedera.cli.hedera.network.Network;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HederaNetworkTest {

  @InjectMocks
  private HederaNetwork hederaNetwork;

  @Mock
  private Network network;

  @Test
  public void network() {
    hederaNetwork.network("", "");
    verify(network, times(1)).handle("");
  }

  @Test
  public void networkList() {
    hederaNetwork.network("ls", "");
    verify(network, times(1)).handle("ls");
  }

  @Test
  public void networkUse() {
    hederaNetwork.network("use", "");
    verify(network, times(1)).handle("use");
  }

  @Test
  public void networkUseWithName() {
    hederaNetwork.network("use", "testnet");
    verify(network, times(1)).handle("use", "testnet");
  }

}