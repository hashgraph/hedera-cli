package com.hedera.cli.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import com.hedera.cli.hedera.crypto.Account;
import com.hedera.cli.hedera.crypto.Transfer;
import com.hedera.cli.shell.ShellHelper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HederaCryptoTest {

  @InjectMocks
  private HederaCrypto hederaCrypto;

  @Mock
  private ShellHelper shellHelper;

  @Mock
  private Account account;

  @Mock
  private Transfer transfer;

  @Test
  public void accountCreate() {
    assertNotNull(shellHelper);

    // account create
    String subCommand = "create";
    String accountId = "";
    boolean y = true;
    String b = "";
    boolean k = true;
    String pk = "";
    String o = "";
    String n = "";
    hederaCrypto.account(subCommand, accountId, y, b, k, pk, o, n);

    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> valueCapture2 = ArgumentCaptor.forClass(String.class);
    verify(account).handle(valueCapture.capture(), valueCapture2.capture());

    String actual = valueCapture.getValue();
    String expected = subCommand;
    assertEquals(expected, actual);

    List<String> varArgs = valueCapture2.getAllValues();
    String actual2 = varArgs.get(0);
    String expected2 = "-k true";
    assertEquals(expected2, actual2);
  }

  @Test
  public void accountUpdate() {
    // account update
    String subCommand = "update";
    String accountId = "";
    boolean y = true;
    String b = "";
    boolean k = true;
    String pk = "";
    String o = "";
    String n = "";
    hederaCrypto.account(subCommand, accountId, y, b, k, pk, o, n);
    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    verify(shellHelper).printError(valueCapture.capture());
    String actual = valueCapture.getValue();
    String expected = "To be implemented";
    assertEquals(expected, actual);
  }

  @Test
  public void accountInfo() {
    // account info
    String subCommand = "info";
    String accountId = "";
    boolean y = true;
    String b = "";
    boolean k = true;
    String pk = "";
    String o = "";
    String n = "";
    hederaCrypto.account(subCommand, accountId, y, b, k, pk, o, n);
    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    verify(shellHelper).printError(valueCapture.capture());
    String actual = valueCapture.getValue();
    String expected = "Please provide an account id";
    assertEquals(expected, actual);
  }

  @Test
  public void accountRecovery() {
    // account recovery
    String subCommand = "recovery";
    String accountId = "";
    boolean y = true;
    String b = "";
    boolean k = true;
    String pk = "";
    String o = "";
    String n = "";
    hederaCrypto.account(subCommand, accountId, y, b, k, pk, o, n);
    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    verify(shellHelper).printError(valueCapture.capture());
    String actual = valueCapture.getValue();
    String expected = "Please provide an account id";
    assertEquals(expected, actual);
  }

  @Test
  public void accountUse() {
    // account use
    String subCommand = "use";
    String accountId = "";
    boolean y = true;
    String b = "";
    boolean k = true;
    String pk = "";
    String o = "";
    String n = "";
    hederaCrypto.account(subCommand, accountId, y, b, k, pk, o, n);
    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    verify(shellHelper).printError(valueCapture.capture());
    String actual = valueCapture.getValue();
    String expected = "Please provide an account id";
    assertEquals(expected, actual);
  }

  @Test
  public void accountBalance() {
    // account balance
    String subCommand = "balance";
    String accountId = "";
    boolean y = true;
    String b = "";
    boolean k = true;
    String pk = "";
    String o = "";
    String n = "";
    hederaCrypto.account(subCommand, accountId, y, b, k, pk, o, n);
    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    verify(shellHelper).printError(valueCapture.capture());
    String actual = valueCapture.getValue();
    String expected = "Please provide an account id";
    assertEquals(expected, actual);
  }

  @Test
  public void accountList() {
    // account ls
    String subCommand = "ls";
    String accountId = "";
    boolean y = true;
    String b = "";
    boolean k = true;
    String pk = "";
    String o = "";
    String n = "";
    hederaCrypto.account(subCommand, accountId, y, b, k, pk, o, n);

    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> valueCapture2 = ArgumentCaptor.forClass(String.class);
    verify(account).handle(valueCapture.capture(), valueCapture2.capture());
    String arg = valueCapture.getValue();
    List<String> varArgs = valueCapture2.getAllValues();
    System.out.println(arg);
    for (String s: varArgs) {
      System.out.println(s);
    }
  }

  @Test
  public void accountDelete() {
    // account delete
    String subCommand = "delete";
    String accountId = "";
    boolean y = true;
    String b = "";
    boolean k = true;
    String pk = "";
    String o = "";
    String n = "";
    hederaCrypto.account(subCommand, accountId, y, b, k, pk, o, n);
    
    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> valueCapture2 = ArgumentCaptor.forClass(String.class);
    verify(account).handle(valueCapture.capture(), valueCapture2.capture());
    String arg = valueCapture.getValue();
    List<String> varArgs = valueCapture2.getAllValues();
    System.out.println(arg);
    for (String s: varArgs) {
      System.out.println(s);
    }
  }

  @Test
  public void transfer() {
    assertNotNull(transfer);

    // transfer
    String[] sender = {};
    String[] recipient = {};
    boolean y = false;
    String[] hb = {};
    String[] tb = {};
    hederaCrypto.transfer(sender, recipient, y, hb, tb);
    verify(shellHelper).printError("Recipient cannot be empty");

    String[] recipient2 = { "0.0.1001" };
    String[] hb2 = { "1" };
    String[] tb2 = { "100000000" };
    hederaCrypto.transfer(sender, recipient2, y, hb2, tb2);
    verify(shellHelper).printError("Transfer amounts must either be in hbars or tinybars, not both");

    String[] hb3 = { "1" };
    hederaCrypto.transfer(sender, recipient2, y, hb3, tb);

    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    verify(transfer).handle(valueCapture.capture());
    List<String> varArgs = valueCapture.getAllValues();
    assertEquals("-s=", varArgs.get(0));
    assertEquals("-r=0.0.1001", varArgs.get(1));
    assertEquals("-y=false", varArgs.get(2));
    assertEquals("-hb=1", varArgs.get(3));

    String[] transferArgs = hederaCrypto.getTransferArgs();
    List<String> transferArgsList = Arrays.asList(transferArgs);
    assertTrue(transferArgsList.containsAll(varArgs));
  }

}