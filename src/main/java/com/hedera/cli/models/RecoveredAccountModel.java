package com.hedera.cli.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecoveredAccountModel {
    private String accountId;
    private String privateKey;
    private String publicKey;
    private String privateKeyEncoded;
    private String publicKeyEncoded;
    private String privateKeyBrowserCompatible;
}
