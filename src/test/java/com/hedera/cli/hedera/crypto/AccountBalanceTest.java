package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.utils.AccountUtils;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.HederaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountBalanceTest {

    @InjectMocks
    private AccountBalance accountBalance;

    @Mock
    ShellHelper shellHelper;

    @Mock
    AccountUtils accountUtils;

    @Mock
    Hedera hedera;

//    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @Test
    public void getBalance() throws HederaException {
        String accountId = "0.0.1001";
        Client client = mock(Client.class);
        System.out.println(client);
//        when(client.getAccountBalance(AccountId.fromString(accountId))).thenReturn(100L);
//        doNothing().when(shellHelper).printError(null);
        long value = accountBalance.getBalance();
//        verify(client, times(1)).getAccountBalance(AccountId.fromString(accountId));
        assertEquals(0, value);
//        String outputResult = new String(output.toByteArray());
//        List<String> outputResultArray = Arrays.asList(outputResult.split("\n"));
//        outputResultArray.stream().map(s -> s.trim()).collect(Collectors.toList());
//        assertThat(outputResultArray, containsInAnyOrder("Balance "));
    }

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
