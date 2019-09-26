package com.hedera.cli.hedera.crypto;

import org.junit.Test;

public class AccountUseTest {

    @Test
    public void testAccountSwitch() {
        AccountUse accountUse = new AccountUse();
        accountUse.switchAccount();
    }
}
