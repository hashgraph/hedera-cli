package com.hedera.cli.models;

import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.Claim;
import com.hedera.hashgraph.sdk.crypto.Key;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class AccountInfoModel {
    AccountId accountId;
    String contractId;
    long balance;
    List<Claim> claim;
    Duration autoRenewPeriod;
    Instant expirationTime;
    long receivedRecordThreshold;
    Key key;
}
