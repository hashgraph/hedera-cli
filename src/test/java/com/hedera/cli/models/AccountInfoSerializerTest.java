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
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.AccountInfo;
import com.hedera.hashgraph.sdk.account.Claim;
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

    // add fake claims (not implemented on Hedera yet so our test data doesn't have
    // it)
    List<Claim> claims = new ArrayList<>();
    String testHashString = "test";
    com.hederahashgraph.api.proto.java.Claim claimProto = com.hederahashgraph.api.proto.java.Claim.newBuilder()
        .setAccountID(AccountId.fromString(testAccount.get("accountId").toString()).toProto())
        .setHash(ByteString.copyFrom(testHashString.getBytes())).build();
    claims.add(new Claim(claimProto));
    testAccount.put("claims", claims);

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

      // innstantiate a new ObjectMapper and read it into a Map to verify
      ObjectMapper mapper2 = new ObjectMapper();
      TypeReference<HashMap<String, Object>> t = new TypeReference<HashMap<String, Object>>() {
      };
      Map<String, Object> accountInfo2 = mapper2.readValue(result, t);

      assertEquals(testAccount.get("key").toString(), accountInfo2.get("key").toString());

      List<Claim> testAccountClaims = getClaims(testAccount);
      assertEquals(1, testAccountClaims.size());

      List<Claim> accountInfo2Claims = buildClaims(accountInfo2);
      assertEquals(1, accountInfo2Claims.size());

      String actualAccountString = accountInfo2Claims.get(0).getAcccount().toString();
      String expectedAccountString = testAccount.get("accountId").toString();
      assertEquals(expectedAccountString, actualAccountString);

      byte[] actualHash = accountInfo2Claims.get(0).getHash();
      byte[] decodedHash = Base64.getDecoder().decode(actualHash);
      String actualHashString = new String(decodedHash);
      String expectedHashString = testHashString;
      assertEquals(expectedHashString, actualHashString);

    } catch (Exception e) {
      System.out.println("Serialization failed");
      e.printStackTrace();
    }

  }

  private List<Claim> buildClaims(Map<String, Object> accountInfo2) {
    List<Claim> accountInfo2Claims = new ArrayList<>();
    List<?> accountInfo2ClaimObjects = (List<?>) accountInfo2.get("claims");
    if (accountInfo2ClaimObjects instanceof List<?>) {
      for (Object o : accountInfo2ClaimObjects) {
        String oString = o.toString();
        String[] list = oString.replaceAll("\\{", "").replaceAll("\\}", "").split(",");

        com.hederahashgraph.api.proto.java.Claim.Builder cBuilder = com.hederahashgraph.api.proto.java.Claim
            .newBuilder();
        for (String item : list) {
          String[] kv = item.split("=", 2);
          switch (kv[0].trim()) {
          case "accountId":
            cBuilder.setAccountID(AccountId.fromString(kv[1]).toProto());
            break;
          case "hash":
            cBuilder.setHash(ByteString.copyFrom(kv[1].getBytes()));
            break;
          default:
            // do nothing
          }
        }
        Claim claim = new Claim(cBuilder.build());
        accountInfo2Claims.add(claim);
      }
    }
    return accountInfo2Claims;
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

  private Ed25519PublicKey getKey(Map<String, Object> testAccount) {
    return Ed25519PublicKey.fromString(testAccount.get("key").toString());
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
      // for (Map.Entry<String, Object> e : testAccount.entrySet()) {
      //   System.out.println(e.getKey() + ": " + e.getValue());
      // }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return testAccount;
  }

}