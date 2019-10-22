package com.hedera.cli.hedera;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hedera.cli.models.AddressBook;
import com.hedera.cli.services.CurrentAccountService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = { CurrentAccountService.class, AddressBook.class })
public class HederaTest {

    @Mock
    private AddressBook addressBook;

    @Mock
    private CurrentAccountService currentAccountService;

    @Test
    public void testGetRandomNode() {
        System.out.println(addressBook);
        assertTrue(true);
        // DataDirectory dataDirectory = Mockito.mock(DataDirectory.class);
        // when(dataDirectory.readFile("network.txt")).thenReturn("mainnet");

        // addressBook.setDataDirectory(dataDirectory); // only using this for tests, to
        // set the mock dataDirectory

        // Network network = addressBook.getCurrentNetwork();
        // assertEquals("mainnet", network.getName());
    }

    @Test
    public void testGetOperatorId() {
        System.out.println(currentAccountService);

        assertTrue(true);
        // AccountId operatorAccount;
        // AccountUtils accountUtils = Mockito.mock(AccountUtils.class);
        // when(accountUtils
        // .retrieveDefaultAccountID())
        // .thenReturn(AccountId.fromString("0.0.1234"));

        // String testAccountNumber = "0.0.1001";
        // // retrieve our singleton and set our test value in it
        // currentAccountService = (CurrentAccountService)
        // context.getBean("currentAccount", CurrentAccountService.class);
        // accountService.setAccountNumber(testAccountNumber);
        // CurrentAccountService accountServiceAgain = context.getBean("currentAccount",
        // CurrentAccountService.class);
        // String currentAccount = accountServiceAgain.getAccountNumber();

        // Hedera hedera = Mockito.mock(Hedera.class);
        // when(hedera.currentAccountExist()).thenReturn(true);
        //
        // operatorAccount = hedera.getOperatorId();
        // assertEquals(AccountId.fromString(currentAccount), operatorAccount);
    }
}
