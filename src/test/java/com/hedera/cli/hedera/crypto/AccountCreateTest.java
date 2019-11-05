package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.keygen.EDBip32KeyChain;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.services.Hapi;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;

import org.hjson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountCreateTest {

  @InjectMocks
  private AccountCreate accountCreate;

  @Mock
  private Hedera hedera;

  @Mock
  private Hapi hapi;

  private List<String> phraseList = Arrays.asList("hello", "fine", "demise", "ladder", "glow", "hard", "magnet", "fan",
      "donkey", "carry", "chuckle", "assault", "leopard", "fee", "kingdom", "cheap", "odor", "okay", "crazy", "raven",
      "goose", "focus", "shrimp", "carbon");
  private String accountId = "0.0.1234";
  private KeyPair keyPair; // operator key pair for tests

  @BeforeEach
  public void setUp() {
    EDBip32KeyChain keyChain = new EDBip32KeyChain();
    int index = 0;
    keyPair = keyChain.keyPairFromWordList(index, phraseList);
  }

  @Test
  public void run() {
    Ed25519PrivateKey operatorKey = Ed25519PrivateKey.fromString(keyPair.getPrivateKeyHex());
    when(hedera.getOperatorKey()).thenReturn(operatorKey);
    when(hedera.getOperatorId()).thenReturn(AccountId.fromString(accountId));
    when(hapi.createNewAccount(any(Ed25519PublicKey.class), any(AccountId.class), anyLong()))
        .thenReturn(AccountId.fromString("0.0.1235"));
    JsonObject account = new JsonObject();
    account.add("accountId", "0.0.1235");
    account.add("privateKey", "somePrivateKey");
    account.add("publicKey", "somePublicKey");
    when(hapi.printAccount(anyString(), anyString(), anyString())).thenReturn(account);
    AccountManager accountManager = mock(AccountManager.class);
    when(hedera.getAccountManager()).thenReturn(accountManager);

    accountCreate.run();

    ArgumentCaptor<AccountId> v1 = ArgumentCaptor.forClass(AccountId.class);
    ArgumentCaptor<Ed25519PrivateKey> v2 = ArgumentCaptor.forClass(Ed25519PrivateKey.class);
    verify(accountManager).setDefaultAccountId(v1.capture(), v2.capture());
    AccountId actual = v1.getValue();
    assertEquals("0.0.1235", actual.toString());
  }

}