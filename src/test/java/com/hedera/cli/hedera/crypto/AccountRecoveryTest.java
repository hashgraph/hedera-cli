package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.keygen.EDBip32KeyChain;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.RecoveredAccountModel;
import com.hedera.cli.shell.ShellHelper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountRecoveryTest {

  private final PrintStream stdout = System.out;
  private final ByteArrayOutputStream output = new ByteArrayOutputStream();

  @InjectMocks
  private AccountRecovery accountRecovery;

  @Mock
  private ShellHelper shellHelper;

  @Mock
  private InputReader inputReader;

  @Mock
  private AccountManager accountManager;

  private List<String> phraseList = Arrays.asList("hello", "fine", "demise", "ladder", "glow", "hard", "magnet", "fan",
      "donkey", "carry", "chuckle", "assault", "leopard", "fee", "kingdom", "cheap", "odor", "okay", "crazy", "raven",
      "goose", "focus", "shrimp", "carbon");
  private String accountId = "0.0.1234";
  private KeyPair keyPair;

  @BeforeEach
  public void setUp() throws UnsupportedEncodingException {
    System.setOut(new PrintStream(output, true, "UTF-8"));
    EDBip32KeyChain keyChain = new EDBip32KeyChain();
    int index = 0;
    keyPair = keyChain.keyPairFromWordList(index, phraseList);
  }

  @AfterEach
  public void tearDown() {
    System.setOut(stdout);
  }


  @Test
  public void run() {
    assertNotNull(accountManager);

    accountRecovery.run();

    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    verify(shellHelper).printInfo(valueCapture.capture());
    String actual = valueCapture.getValue();
    String expected = "Start the recovery process";
    assertEquals(expected, actual);
  }

  @Test
  public void promptWords() {
    accountRecovery.setWords(true);
    when(inputReader.prompt("Recover account using 24 words or keys? Enter words/keys")).thenReturn("words");
    boolean wordsActual = accountRecovery.promptPreview(inputReader);
    assertEquals(accountRecovery.isWords(), wordsActual);
  }

  @Test
  public void promptKeys() {
    accountRecovery.setWords(false);
    when(inputReader.prompt("Recover account using 24 words or keys? Enter words/keys")).thenReturn("keys");
    boolean wordsActual = accountRecovery.promptPreview(inputReader);
    assertEquals(accountRecovery.isWords(), wordsActual);
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