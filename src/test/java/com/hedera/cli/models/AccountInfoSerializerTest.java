package com.hedera.cli.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.AccountInfo;
import com.hedera.hashgraph.sdk.account.Claim;
import com.hedera.hashgraph.sdk.crypto.Key;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountInfoSerializerTest {

  @InjectMocks
  private AccountInfoSerializer accountInfoSerializer;

  @Test
  public void testAccountInfoSerializer() {

    assertNotNull(accountInfoSerializer);

    Map<String, Object> testAccount = prepareTestAccount();

    // use testAccount data to mock AccountInfo class
    AccountInfo accountInfo = mock(AccountInfo.class);
    when(accountInfo.getAccountId()).thenReturn(AccountId.fromString(testAccount.get("accountId").toString()));
    when(accountInfo.getKey()).thenReturn(getKey(testAccount));
    when(accountInfo.getContractAccountId()).thenReturn(testAccount.get("contractId").toString());
    when(accountInfo.getBalance()).thenReturn(Long.parseLong(testAccount.get("balance").toString()));
    when(accountInfo.getClaims()).thenReturn(getClaims(testAccount));
    when(accountInfo.getAutoRenewPeriod()).thenReturn(Duration.parse(testAccount.get("autoRenewPeriod").toString()));
    when(accountInfo.getExpirationTime()).thenReturn(getExpirationTime(testAccount));
    when(accountInfo.getProxyAccountId())
        .thenReturn(AccountId.fromString(testAccount.get("proxyAccountId").toString()));
    when(accountInfo.getGenerateReceiveRecordThreshold())
        .thenReturn(Long.parseLong(testAccount.get("generateReceiveRecordThreshold").toString()));
    when(accountInfo.getGenerateSendRecordThreshold())
        .thenReturn(Long.parseLong(testAccount.get("generateSendRecordThreshold").toString()));

    // check our mock
    assertEquals(AccountId.fromString("0.0.82319"), accountInfo.getAccountId());

    try {
      ObjectMapper mapper = new ObjectMapper();
      SimpleModule module = new SimpleModule();
      module.addSerializer(AccountInfo.class, accountInfoSerializer);
      mapper.registerModule(module);
      ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
      String result = ow.writeValueAsString(accountInfo); // serialized result shows accountInfoSerializer works as
                                                          // expected

      System.out.println(result);
      // innstantiate a new ObjectMapper and read it into a Map to verify                                                 
      ObjectMapper mapper2 = new ObjectMapper();
      TypeReference<HashMap<String, Object>> t = new TypeReference<HashMap<String, Object>>() {};
      Map<String, Object> accountInfo2 = mapper2.readValue(result, t);
      
      assertEquals(testAccount.get("key").toString(), accountInfo2.get("key").toString());

      // note that claims feature is not live yet, so we have no test data for a list of claims that can be used to complete this test
      assertEquals(testAccount.get("claims"), accountInfo2.get("claims"));

    } catch (Exception e) {
      System.out.println("Serialization failed");
      e.printStackTrace();
    }

  }

  private Instant getExpirationTime(Map<String, Object> testAccount) {
    String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    Date date = null;
    Instant expirationTime = null;
    try {
      date = formatter.parse(testAccount.get("expirationTime").toString());
      expirationTime = date.toInstant();
    } catch (ParseException e1) {
      e1.printStackTrace();
    }
    return expirationTime;
  }

  private Key getKey(Map<String, Object> testAccount) {
    Key key = (Key) Ed25519PublicKey.fromString(testAccount.get("key").toString());
    return key;
  }

  private List<Claim> getClaims(Map<String, Object> testAccount) {
    List<Claim> claims = new ArrayList<>();
    if (testAccount.get("claims") instanceof List<?>) {
      List<?> claimsObj = (List<?>) testAccount.get("claims");
      for (Object o : claimsObj) {
        claims.add((Claim) o);
      }
    }
    return claims;
  }

  private Map<String, Object> prepareTestAccount() {
    // load test data (response from a get info query)
    Map<String, Object> testAccount = new HashMap<>();
    String testAccountPath = File.separator + "testaccount.json";
    ObjectMapper mapper = new ObjectMapper();
    InputStream input = getClass().getResourceAsStream(testAccountPath);
    try {
      testAccount = mapper.readValue(input, new TypeReference<Map<String, Object>>() {
      });
      for (Map.Entry<String, Object> e : testAccount.entrySet()) {
        System.out.println(e.getKey() + ": " + e.getValue());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return testAccount;
  }

}