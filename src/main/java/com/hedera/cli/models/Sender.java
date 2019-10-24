package com.hedera.cli.models;

import com.hedera.hashgraph.sdk.account.AccountId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Sender {

    private AccountId accountId;

    private Long amount;
    
}
