package com.hedera.cli.hedera.crypto;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class AccountDelete {

    @Test
    public void testDeletingAFile() throws IOException {
        new File("src/test/resources/fileToDelete.txt").createNewFile();
        File fileToDelete = new File("src/test/resources/fileToDelete.txt");
        boolean success = fileToDelete.delete();
        assertTrue(success);
    }
}
