package com.hedera.cli.hedera;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.models.AddressBook;
import com.hedera.cli.models.Network;

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
}
