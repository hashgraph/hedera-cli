package com.hedera.cli.hedera;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.hedera.cli.models.Network;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.models.AddressBook;

import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class HederaTest {

    @Test
    public void testGetRandomNode() {
        DataDirectory dataDirectory = Mockito.mock(DataDirectory.class);
        when(dataDirectory.readFile("network.txt")).thenReturn("mainnet");

        AddressBook addressBook = AddressBook.init();
        addressBook.setDataDirectory(dataDirectory); // only using this for tests, to set the mock dataDirectory

        Network network = addressBook.getCurrentNetwork();
        assertEquals("mainnet", network.getName());
    }

    @Test
    public void testRetrieveDefaultAccountKeyInHexString() {
        Hedera hedera = new Hedera();
        String privateKey = hedera.retrieveDefaultAccountKeyInHexString();
        System.out.println(privateKey);
    }

    @Test
    public void testRetrieveDefaultAccountPublicKeyInHexString() {
        Hedera hedera = new Hedera();
        String publicKey = hedera.retrieveDefaultAccountPublicKeyInHexString();
        System.out.println(publicKey);
    }

    @Test
    public void testRetrieveDefaultAccountID() {
        Hedera hedera = new Hedera();
        AccountId accountId = hedera.retrieveDefaultAccountID();
        System.out.println(accountId);
    }
}
