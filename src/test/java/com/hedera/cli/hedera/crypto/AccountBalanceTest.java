package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.utils.AccountUtils;
import com.hedera.cli.shell.ShellHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class AccountBalanceTest {

    @InjectMocks
    private AccountBalance accountBalance;

    @Mock
    private ShellHelper shellHelper;

    @Mock
    private AccountUtils accountUtils;

    @Mock
    private Hedera hedera;

    @Test
    public void autoWiredDependenciesNotNull() {
        accountUtils = accountBalance.getAccountUtils();
        assertNotNull(accountUtils);

        shellHelper = accountBalance.getShellHelper();
        assertNotNull(shellHelper);

        hedera = accountBalance.getHedera();
        assertNotNull(hedera);

        accountBalance.setAccountIdInString("0.0.1111");
        String accountIdInString = accountBalance.getAccountIdInString();
        assertEquals("0.0.1111", accountIdInString);
    }

    @Test
    public void runAccountBalanceVerifies() {
        String accountId = "0.0.1121";
        accountBalance.setAccountIdInString(accountId);
        when(accountUtils.verifyAccountId(accountId, shellHelper)).thenReturn(accountId);
        accountBalance.run();
        verify(accountUtils, times(1)).verifyAccountId(accountId, shellHelper);
    }
}
