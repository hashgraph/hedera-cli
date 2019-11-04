package com.hedera.cli.shell;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.AddressBookManager;

import org.jline.utils.AttributedString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CliPromptProviderTest {

  @InjectMocks
  private CliPromptProvider cliPromptProvider;

  @Mock
  private Hedera hedera;

  @Mock
  private AddressBookManager addressBookManager;

  @Test
  public void getPromptNoOperatorAccount() {
    // mock with "testnet" and no operator account ("")
    when(addressBookManager.getCurrentNetworkAsString()).thenReturn("testnet");
    when(hedera.getAddressBookManager()).thenReturn(addressBookManager);
    // when(hedera.addressBookManager.getCurrentNetworkAsString()).thenReturn("testnet");
    when(hedera.getOperatorAccount()).thenReturn("");

    AttributedString prompt = cliPromptProvider.getPrompt();
    String ansiPrompt = prompt.toAnsi();
    String[] ansiPromptArray = ansiPrompt.split("\n");

    String line1 = ansiPromptArray[0].trim();
    assertEquals("[33mTo see available networks, enter `network ls`", line1);

    String line2 = ansiPromptArray[1].trim();
    assertEquals("You do not have a default operator account for this network. Please run `setup`", line2);

    String line3 = ansiPromptArray[2].trim();
    assertEquals("[34mhedera [32m[testnet][34m :> [0m", line3);
  }

  @Test
  public void getPromptWithOperatorAccount() {
    // mock with "testnet" and a valid operator account ("0.0.1001")
    when(addressBookManager.getCurrentNetworkAsString()).thenReturn("testnet");
    when(hedera.getAddressBookManager()).thenReturn(addressBookManager);
    // when(hedera.addressBookManager.getCurrentNetworkAsString()).thenReturn("testnet");
    when(hedera.getOperatorAccount()).thenReturn("0.0.1001");

    AttributedString prompt = cliPromptProvider.getPrompt();
    String ansiPrompt = prompt.toAnsi();
    String[] ansiPromptArray = ansiPrompt.split("\n");

    String line1 = ansiPromptArray[0].trim();
    assertEquals("[34mhedera [32m[testnet][33m[0.0.1001][34m :> [0m", line1);
  }

}