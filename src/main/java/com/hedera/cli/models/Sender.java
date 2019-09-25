package com.hedera.cli.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hedera.hashgraph.sdk.account.AccountId;
import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sender {

    @JsonProperty("accountid")
    public AccountId accountId;

    @JsonProperty("amount")
    public BigInteger amount;

    public AccountId getAccountId() {
        return accountId;
    }

    public void setAccountId(AccountId accountId) {
        this.accountId = accountId;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }
}
