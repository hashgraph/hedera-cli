package com.hedera.cli.hedera;

import com.hedera.cli.models.HederaNode;
import org.junit.Test;

import java.io.InputStream;

public class TestHedera {

    @Test
    public void testSingleNode() {
        InputStream addressBookInputStream = getClass().getResourceAsStream("/addressbook.json");
        Hedera h = new Hedera();
        HederaNode n = h.getSingleNode(addressBookInputStream);

    }
}
