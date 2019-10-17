package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.utils.DataDirectory;
import org.junit.Test;

public class AccountUseTest {

    @Test
    public void testAccountSwitch() {
        AccountUse accountUse = new AccountUse();
        DataDirectory dataDirectory = new DataDirectory();
        accountUse.accountIdExistsInIndex(dataDirectory, "0.0.1003");
    }
}
