package com.hedera.cli.models;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    // load test data (response from a get info query)
    Map<String, Object> testAccount;
    String testAccountPath = File.separator + "testaccount.json";
    ObjectMapper mapper = new ObjectMapper();
    InputStream input = getClass().getResourceAsStream(testAccountPath);
    try {
      testAccount = mapper.readValue(input, new TypeReference<Map<String,Object>>(){});
      for (Map.Entry<String, Object> e: testAccount.entrySet()) {
        System.out.println(e.getKey() + ": " + e.getValue());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}