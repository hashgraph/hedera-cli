package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

public class AccountDeleteTest {

    @Test
    public void testDeletingAFile() throws IOException {
        new File("src/test/resources/fileToDelete.txt").createNewFile();
        File fileToDelete = new File("src/test/resources/fileToDelete.txt");
        boolean success = fileToDelete.delete();
        assertTrue(success);
    }
}