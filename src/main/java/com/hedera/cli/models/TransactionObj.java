package com.hedera.cli.models;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionObj {
    private String txID;
    private String txValidStart;
    private String txTimestamp;
    private Instant txConsensusTimestamp;
    private Long txFee;
    private String receiptStatus;
    private String txMemo;
}