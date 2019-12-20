package com.hedera.cli.models;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hedera.hashgraph.sdk.account.AccountInfo;

import org.springframework.stereotype.Component;

@Component
public class AccountInfoSerializer extends JsonSerializer<AccountInfo> {

  @Override
  public void serialize(AccountInfo value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
        jgen.writeStartObject();

        jgen.writeFieldName("key");
        jgen.writeString(value.key.toString());
        
        jgen.writeFieldName("accountId");
        jgen.writeString(value.accountId.toString());

        jgen.writeFieldName("contractId");
        jgen.writeString(value.contractAccountId);

        jgen.writeFieldName("balance");
        jgen.writeString(Long.toString(value.balance, 10));

        // Claims have been removed from sdk
//        jgen.writeFieldName("claims");
//        jgen.writeStartArray();
//        for (Claim claim: value.getClaims()) {
//          jgen.writeStartObject();
//          jgen.writeFieldName("accountId");
//          jgen.writeString(claim.getAcccount().toString());
//          jgen.writeFieldName("hash");
//          jgen.writeBinary(claim.getHash());
//
//          // keys not implemented yet
//
//          // duration not implemented yet
//
//          jgen.writeEndObject();
//        }
//        jgen.writeEndArray();

        jgen.writeFieldName("autoRenewPeriod");
        jgen.writeString(value.autoRenewPeriod.toString());

        jgen.writeFieldName("expirationTime");
        jgen.writeString(value.expirationTime.toString());

        jgen.writeFieldName("generateReceiveRecordThreshold");
        jgen.writeNumber(value.generateReceiveRecordThreshold);

        jgen.writeFieldName("deleted");
        jgen.writeBoolean(value.isDeleted);

        jgen.writeFieldName("proxyAccountId");
        jgen.writeString(value.proxyAccountId.toString());

        jgen.writeFieldName("proxyReceived");
        jgen.writeNumber(value.proxyReceived);

        jgen.writeFieldName("generateSendRecordThreshold");
        jgen.writeNumber(value.generateSendRecordThreshold);

        jgen.writeFieldName("receiverSignatureRequired");
        jgen.writeBoolean(value.isReceiverSignatureRequired);

        jgen.writeEndObject();
  }


}