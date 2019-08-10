package com.hedera.cli.hedera.utils;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class DataDirectoryTest {

    @Test
    public void testReadJsonToMap() throws IOException {
        String addressBookJson = File.separator + "addressbook.json";
        InputStream addressBookInputStream = getClass().getResourceAsStream(addressBookJson);
        DataDirectory dataDirectory = new DataDirectory();
        dataDirectory.readJsonToMap(addressBookInputStream);
    }


}
