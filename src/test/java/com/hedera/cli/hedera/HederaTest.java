package com.hedera.cli.hedera;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.hedera.cli.models.Network;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.models.AddressBook;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class HederaTest {

    @Mock
    DataDirectory dataDirectory;

    @InjectMocks
    AddressBook addressBook;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testGetRandomNode() {
        when(dataDirectory.readFile("network.txt")).thenReturn("mainnet");
        addressBook = AddressBook.init();
        Network network = addressBook.getCurrentNetwork();
        assertEquals("mainnet", network.getName());
    }
}
