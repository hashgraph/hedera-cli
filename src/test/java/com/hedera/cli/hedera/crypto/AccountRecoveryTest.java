package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hedera.cli.hedera.keygen.EDBip32KeyChain;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.models.RecoveredAccountModel;
import com.hedera.cli.shell.ShellHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountRecoveryTest {

  @InjectMocks
  private AccountRecovery accountRecovery;

  @Mock
  private ShellHelper shellHelper;

  private List<String> phraseList = Arrays.asList("hello", "fine", "demise", "ladder", "glow", "hard", "magnet", "fan",
      "donkey", "carry", "chuckle", "assault", "leopard", "fee", "kingdom", "cheap", "odor", "okay", "crazy", "raven",
      "goose", "focus", "shrimp", "carbon");
  private String accountId = "0.0.1234";
  private KeyPair keyPair;

  @BeforeEach
  public void setUp() {
    EDBip32KeyChain keyChain = new EDBip32KeyChain();
    int index = 0;
    keyPair = keyChain.keyPairFromWordList(index, phraseList);
  }

  @Test
  public void run() {
    accountRecovery.run();

    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    verify(shellHelper).printInfo(valueCapture.capture());
    String actual = valueCapture.getValue();
    String expected = "Start the recovery process";
    assertEquals(expected, actual);
  }

  @Test
  public void printKeyPairInRecoveredAccountModelFormat() throws JsonProcessingException {
    RecoveredAccountModel recoveredAccountModel;
    recoveredAccountModel = new RecoveredAccountModel();
    recoveredAccountModel.setAccountId(accountId);
    recoveredAccountModel.setPrivateKey(keyPair.getPrivateKeyHex());
    recoveredAccountModel.setPublicKey(keyPair.getPublicKeyHex());
    recoveredAccountModel.setPrivateKeyEncoded(keyPair.getPrivateKeyEncodedHex());
    recoveredAccountModel.setPublicKeyEncoded(keyPair.getPublicKeyEncodedHex());
    recoveredAccountModel.setPrivateKeyBrowserCompatible(keyPair.getSeedAndPublicKeyHex());
    accountRecovery.printKeyPair(keyPair, accountId);
    assertEquals(accountId, recoveredAccountModel.getAccountId());
    assertEquals(keyPair.getPrivateKeyHex(), recoveredAccountModel.getPrivateKey());
    assertEquals(keyPair.getPublicKeyHex(), recoveredAccountModel.getPublicKey());
    assertEquals(keyPair.getPrivateKeyEncodedHex(), recoveredAccountModel.getPrivateKeyEncoded());
    assertEquals(keyPair.getPublicKeyEncodedHex(), recoveredAccountModel.getPublicKeyEncoded());
    assertEquals(keyPair.getSeedAndPublicKeyHex(), recoveredAccountModel.getPrivateKeyBrowserCompatible());

    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    String result = ow.writeValueAsString(recoveredAccountModel);
    verify(shellHelper, times(1)).printSuccess(result);
  }

}