package com.hedera.cli.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hedera.hashgraph.sdk.account.AccountId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RecipientTest {

  @Test
  public void recipient() {
    AccountId accountId = AccountId.fromString("0.0.1234");
    Long amount = 1000L;
    Recipient recipient = new Recipient(accountId, amount);

    assertEquals(accountId, recipient.getAccountId());
    assertEquals(amount, recipient.getAmount());
  }

}