package com.hedera.cli.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RecoveredAccountModelTest {

  @Test
  public void recoveredAccountModel() {

    RecoveredAccountModel r = new RecoveredAccountModel();

    r.setAccountId("0.0.1001");
    r.setPrivateKey("somePrivateKey");
    r.setPublicKey("somePublicKey");
    r.setPrivateKeyEncoded("somePrivateKeyInASN1");
    r.setPublicKeyEncoded("somePublicKeyInASN1");
    r.setPrivateKeyBrowserCompatible("someBrowserCompatiblePrivateKey");


    assertEquals("0.0.1001", r.getAccountId());
    assertEquals("somePrivateKey", r.getPrivateKey());
    assertEquals("somePublicKey", r.getPublicKey());
    assertEquals("somePrivateKeyInASN1", r.getPrivateKeyEncoded());
    assertEquals("somePublicKeyInASN1", r.getPublicKeyEncoded());
    assertEquals("someBrowserCompatiblePrivateKey", r.getPrivateKeyBrowserCompatible());
  }

}