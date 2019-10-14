package com.hedera.cli.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecoveredAccountModel {
    String accountId;
    String privateKey;
    String publicKey;
    String privateKeyEncoded;
    String publicKeyEncoded;
    String privateKeyBrowserCompatible;
}
