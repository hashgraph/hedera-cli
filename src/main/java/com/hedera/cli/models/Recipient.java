package com.hedera.cli.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hedera.hashgraph.sdk.account.AccountId;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipient {

    @JsonProperty("accountid")
    public AccountId accountId;

    @JsonProperty("amount")
    public Long amount;


    public Recipient(AccountId accountId, Long amount) {
        this.accountId = accountId;
        this.amount = amount;
    }


    public AccountId getAccountId() {
        return accountId;
    }

    public void setAccountId(AccountId accountId) {
        this.accountId = accountId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
