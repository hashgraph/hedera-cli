package com.hedera.cli.hedera.utils;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class DataDirectoryTest {

    @Test
    public void testReadJsonToMap() throws IOException {
        DataDirectory dataDirectory = new DataDirectory();
        System.out.println(getClass().getResourceAsStream("/addressbook.json"));
        InputStream addressBookInputStream = getClass().getResourceAsStream("/addressbook.json");
        dataDirectory.readJsonToMap(addressBookInputStream);
    }


}
