package com.hedera.cli.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransactionObjTest {

  @Test
  public void transactionObj() {
    TransactionObj txObj = new TransactionObj();

    // just randomly create a fake consensus timestamp for our tests
    Instant txConsensusTimestamp = Instant.now();

    txObj.setTxID("sometransactionidgoeshere");
    txObj.setTxFee(10000L);
    txObj.setReceiptStatus("OK");
    txObj.setTxConsensusTimestamp(txConsensusTimestamp);
    txObj.setTxMemo("hello");
    txObj.setTxTimestamp("sometransactiontimestamp");
    txObj.setTxValidStart("somevalidstarttimestamp");

    assertEquals("sometransactionidgoeshere", txObj.getTxID());
    assertEquals(10000L, txObj.getTxFee());
    assertEquals("OK", txObj.getReceiptStatus());
    assertEquals(txConsensusTimestamp, txObj.getTxConsensusTimestamp());
    assertEquals("hello", txObj.getTxMemo());
    assertEquals("sometransactiontimestamp", txObj.getTxTimestamp());
    assertEquals("somevalidstarttimestamp", txObj.getTxValidStart());
  }

}