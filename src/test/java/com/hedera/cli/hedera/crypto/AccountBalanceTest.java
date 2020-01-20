package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;

import org.junit.jupiter.api.BeforeEach;
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

    @Mock
    private Hedera hedera;

    // test data
    private Client client;
    private String accountId;
    private long balance;

    @BeforeEach
    public void setUp() {
        client = mock(Client.class);
        accountId = "0.0.1121";
        balance = 99 * 100000000L; // 99 hbars
    }

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

    // FIX THIS
    // @Test
    // public void run() throws HederaNetworkException, IllegalArgumentException, HederaStatusException {
    //     accountBalance.setAccountIdInString(accountId);
    //     when(accountManager.verifyAccountId(eq(accountId))).thenReturn(accountId);
    //     when(hedera.createHederaClient()).thenReturn(client);
    //     // deprecated
    //     // when(client.getAccountBalance(AccountId.fromString(accountId))).thenReturn(balance);

    //     accountBalance.run();

    //     verify(accountManager, times(1)).verifyAccountId(accountId);

    //     ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    //     verify(shellHelper).printSuccess(valueCapture.capture());
    //     String actual = valueCapture.getValue();
    //     String expected = "Balance: " + Long.toString(balance, 10);
    //     assertEquals(expected, actual);
    // }

    @Test
    public void runInvalidAccountId() {
        accountBalance = spy(accountBalance);
        accountBalance.setAccountIdInString("");
        when(accountManager.verifyAccountId(eq(""))).thenReturn(null);

        accountBalance.run();

        // getBalance will not be called because the user supplied ""
        // which is not a valid accountId String
        verify(accountBalance, times(0)).getBalance();
    }
}
