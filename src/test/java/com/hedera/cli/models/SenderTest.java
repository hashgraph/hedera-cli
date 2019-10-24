package com.hedera.cli.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hedera.hashgraph.sdk.account.AccountId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SenderTest {

  @Test
  public void sender() {

    AccountId accountId = AccountId.fromString("0.0.1001");
    Long amount = 1000L;

    Sender sender = new Sender();
    sender.setAccountId(accountId);
    sender.setAmount(amount);

    assertEquals(accountId, sender.getAccountId());
    assertEquals(amount, sender.getAmount());
  }

}