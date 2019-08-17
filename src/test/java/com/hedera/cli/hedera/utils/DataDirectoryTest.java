package com.hedera.cli.hedera.utils;

import com.hedera.cli.models.AddressBook;
import com.hedera.cli.models.Network;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.rule.OutputCapture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@RunWith(MockitoJUnitRunner.class)
public class DataDirectoryTest {

    // captures our stdout
    @Rule
    public OutputCapture capture = new OutputCapture();

    @InjectMocks
    DataDirectory dataDirectory;

    @Test
    public void testMkHederaSubDir() {
        String currentNetwork = dataDirectory.readFile("network.txt", dataDirectory.getDefaultNetworkName());
        String pathToSubDir = currentNetwork + File.separator + "accounts";
        Path subdirpath = Paths.get(pathToSubDir);
        Path path = Paths.get(dataDirectory.getUserHome(), dataDirectory.getDirectoryName(), subdirpath.toString());
        // expected
        boolean directoryExists = Files.exists(path);

        // actual
        // if directory already exists (true), return false
        // if directory does not already exists (false), return true
        boolean result = dataDirectory.mkHederaSubDir(pathToSubDir);
        assertNotEquals(directoryExists, result);
    }

    @Test
    public void testListNetworks() {
        String addressBookJson = File.separator + "addressbook.json";
        InputStream addressBookInputStream = getClass().getResourceAsStream(addressBookJson);
        dataDirectory.listNetworks(addressBookInputStream);

        // tokenize our stdout capture
        List<String> tokens = new ArrayList<>();
        String lineSeparator = System.getProperty("line.separator");
        StringTokenizer tokenizer = new StringTokenizer(capture.toString(), lineSeparator);
        while (tokenizer.hasMoreElements()) {
            tokens.add(tokenizer.nextToken().trim());
        }

        // compare test data against stdout capture on a per-line basis
        assertEquals("mainnet", tokens.get(0));
//        assertEquals("* aspen", tokens.get(1));
        assertEquals("external", tokens.get(2));
    }

    @Test
    public void testNetworkGetName() {

        // Mock DataDirectory
        DataDirectory dataDirectory = Mockito.mock(DataDirectory.class);
        InputStream addressBookInputStream = getClass().getResourceAsStream("/addressbook.json");

        when(dataDirectory.networkGetName(addressBookInputStream)).thenReturn("aspen");

        AddressBook addressBook = AddressBook.init();
        addressBook.setDataDirectory(dataDirectory); // only using this for tests, to set the mock dataDirectory

        String networkName = dataDirectory.networkGetName(addressBookInputStream);
        System.out.println(networkName);
        assertEquals("aspen", networkName);
    }
}
