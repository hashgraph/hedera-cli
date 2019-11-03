package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hedera.cli.models.AccountManager;
import com.hedera.cli.shell.ShellHelper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountBalanceTest {

    @InjectMocks
    private AccountBalance accountBalance;

    @Mock
    private ShellHelper shellHelper;

    @Mock
    private AccountManager accountManager;

    @Test
    public void autoWiredDependenciesNotNull() {
        accountManager = accountBalance.getAccountManager();
        assertNotNull(accountManager);

        shellHelper = accountBalance.getShellHelper();
        assertNotNull(shellHelper);

        accountBalance.setAccountIdInString("0.0.1111");
        String accountIdInString = accountBalance.getAccountIdInString();
        assertEquals("0.0.1111", accountIdInString);
    }

    @Test
    public void runAccountBalanceVerifies() {
        String accountId = "0.0.1121";
        accountBalance.setAccountIdInString(accountId);
        when(accountManager.verifyAccountId(accountId)).thenReturn(accountId);
        accountBalance.run();
        verify(accountManager, times(1)).verifyAccountId(accountId);
    }
}
