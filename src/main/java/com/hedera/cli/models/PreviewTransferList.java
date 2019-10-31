package com.hedera.cli.models;

import com.hedera.hashgraph.sdk.account.AccountId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreviewTransferList {

    private AccountId accountId;

    private String amount;

    public PreviewTransferList(AccountId accountId, String amount) {
        this.accountId = accountId;
        this.amount = amount;
    }
}
