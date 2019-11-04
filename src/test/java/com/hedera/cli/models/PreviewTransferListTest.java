package com.hedera.cli.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hedera.hashgraph.sdk.account.AccountId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PreviewTransferListTest {

  @Test
  public void previewTransferList() {

    AccountId accountId = AccountId.fromString("0.0.1001");
    String amount = "1000";

    PreviewTransferList previewTransferList = new PreviewTransferList(accountId, amount);

    assertEquals(accountId, previewTransferList.getAccountId());
    assertEquals(amount, previewTransferList.getAmount());

    AccountId accountId2 = AccountId.fromString("0.0.1002");
    String amount2 = "2000";

    previewTransferList.setAccountId(accountId2);
    previewTransferList.setAmount(amount2);

    assertEquals(accountId2, previewTransferList.getAccountId());
    assertEquals(amount2, previewTransferList.getAmount());
  }

}