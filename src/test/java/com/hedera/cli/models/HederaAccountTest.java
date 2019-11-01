package com.hedera.cli.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HederaAccountTest {

  @InjectMocks
  private HederaAccount hederaAccount;

  @Test
  public void settersAndGetters() {
    assertNotNull(hederaAccount);
    
    hederaAccount.setAccountId("0.0.1001");
    hederaAccount.setPrivateKey("somePrivateKey");
    hederaAccount.setPublicKey("somePublicKey");

    assertEquals("0.0.1001", hederaAccount.getAccountId());
    assertEquals("somePrivateKey", hederaAccount.getPrivateKey());
    assertEquals("somePublicKey", hederaAccount.getPublicKey());
  }

}