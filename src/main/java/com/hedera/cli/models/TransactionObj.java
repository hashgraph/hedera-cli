package com.hedera.cli.models;

import java.math.BigInteger;
import java.time.Instant;

public class TransactionObj {
    private String txID;
    private String txValidStart;
    private String txTimestamp;
    private Instant txConsensusTimestamp;
    private BigInteger txFee;
    private String receiptStatus;
    private String txMemo;

    public String getTxID() {
        return txID;
    }

    public void setTxID(String txID) {
        this.txID = txID;
    }

    public String getTxValidStart() {
        return txValidStart;
    }

    public void setTxValidStart(String txValidStart) {
        this.txValidStart = txValidStart;
    }

    public String getTxTimestamp() {
        return txTimestamp;
    }

    public void setTxTimestamp(String txTimestamp) {
        this.txTimestamp = txTimestamp;
    }

    public Instant getTxConsensusTimestamp() {
        return txConsensusTimestamp;
    }

    public void setTxConsensusTimestamp(Instant txConsensusTimestamp) {
        this.txConsensusTimestamp = txConsensusTimestamp;
    }

    public BigInteger getTxFee() {
        return txFee;
    }

    public void setTxFee(BigInteger txFee) {
        this.txFee = txFee;
    }

    public String getReceiptStatus() {
        return receiptStatus;
    }

    public void setReceiptStatus(String receiptStatus) {
        this.receiptStatus = receiptStatus;
    }

    public String getTxMemo() {
        return txMemo;
    }

    public void setTxMemo(String txMemo) {
        this.txMemo = txMemo;
    }
}