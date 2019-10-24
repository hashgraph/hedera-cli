package com.hedera.cli.models;

import com.hedera.hashgraph.sdk.account.AccountId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Recipient {

    private AccountId accountId;

    private Long amount;

    public Recipient(AccountId accountId, Long amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

}
