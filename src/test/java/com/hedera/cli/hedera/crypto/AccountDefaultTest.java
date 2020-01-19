package com.hedera.cli.hedera.crypto;

import com.hedera.cli.services.HederaGrpc;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class AccountDefaultTest {

    @InjectMocks
    private AccountDefault accountDefault;

    @Mock
    private HederaGrpc hederaGrpc;

    @Mock
    private ShellHelper shellHelper;

    @Test
    public void run() {
        String account = "0.0.1001";
        accountDefault.setAccountIDInString(account);
        accountDefault.run();
        verify(hederaGrpc, times(1)).updateDefaultAccountInDisk(AccountId.fromString(account));
    }

    @Test
    public void runWithInvalidAcc() {
        String account = "0.1001";
        accountDefault.setAccountIDInString(account);
        accountDefault.run();
        verify(shellHelper, times(1)).printError("Invalid account id provided");
    }

    @Test
    public void defaultAccountNotUpdated() {
        String account = "0.0.1001";
        accountDefault.setAccountIDInString(account);
        when(hederaGrpc.updateDefaultAccountInDisk(AccountId.fromString(account))).thenReturn(false);
        accountDefault.run();
        verify(shellHelper, times(1)).printError("Account chosen does not exist in index. Please use `account recovery` first.");
    }

    @Test
    public void defaultAccountUpdated() {
        String account = "0.0.1001";
        accountDefault.setAccountIDInString(account);
        when(hederaGrpc.updateDefaultAccountInDisk(AccountId.fromString(account))).thenReturn(true);
        accountDefault.run();
        verify(shellHelper, times(1)).printSuccess("Default operator updated " + true);
    }
}
