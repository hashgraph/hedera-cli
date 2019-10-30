package com.hedera.cli.models;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hedera.hashgraph.sdk.account.AccountInfo;
import com.hedera.hashgraph.sdk.account.Claim;

import org.springframework.stereotype.Component;

@Component
public class AccountInfoSerializer extends JsonSerializer<AccountInfo> {

  @Override
  public void serialize(AccountInfo value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException, JsonProcessingException {
        jgen.writeStartObject();

        jgen.writeFieldName("key");
        jgen.writeString(value.getKey().toString());
        
        jgen.writeFieldName("accountId");
        jgen.writeString(value.getAccountId().toString());

        jgen.writeFieldName("contractId");
        jgen.writeString(value.getContractAccountId().toString());

        jgen.writeFieldName("balance");
        jgen.writeString(Long.toString(value.getBalance(), 10));

        jgen.writeFieldName("claims");
        jgen.writeStartArray();
        for (Claim claim: value.getClaims()) {
          jgen.writeString(claim.toString());
        }
        jgen.writeEndArray();

        jgen.writeFieldName("autoRenewPeriod");
        jgen.writeString(value.getAutoRenewPeriod().toString());

        jgen.writeFieldName("expirationTime");
        jgen.writeString(value.getExpirationTime().toString());

        jgen.writeFieldName("generateReceiveRecordThreshold");
        jgen.writeNumber(value.getGenerateReceiveRecordThreshold());

        jgen.writeFieldName("deleted");
        jgen.writeBoolean(value.isDeleted());

        jgen.writeFieldName("proxyAccountId");
        jgen.writeString(value.getProxyAccountId().toString());

        jgen.writeFieldName("proxyReceived");
        jgen.writeNumber(value.getProxyReceived());

        jgen.writeFieldName("generateSendRecordThreshold");
        jgen.writeNumber(value.getGenerateSendRecordThreshold());

        jgen.writeFieldName("receiverSignatureRequired");
        jgen.writeBoolean(value.isReceiverSignatureRequired());

        jgen.writeEndObject();
  }


}