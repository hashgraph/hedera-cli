package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.services.HederaGrpc;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// @ExtendWith(MockitoExtension.class)
public class AccountDeleteTest {

    // @InjectMocks
    // private AccountDelete accountDelete;

    // @Mock
    // private ShellHelper shellHelper;

    // @Mock
    // private InputReader inputReader;

    // @Mock
    // private HederaGrpc hederaGrpc;

    @Test
    public void randomTest() {
        assertNotNull(1);
    }

    // @Test
    // public void testDeletingAFile() throws IOException {
    //     new File("src/test/resources/fileToDelete.txt").createNewFile();
    //     File f = new File("src/test/resources/fileToDelete.txt");
    //     boolean success = f.delete();
    //     assertTrue(success);
    // }

    // @Test
    // public void runFailsInvalidAccountId() {
    //     accountDelete.setOldAccountInString("0.0.1001");
    //     accountDelete.setNewAccountInString("0.1002"); // deliberate invalid account id
    //     accountDelete.run();

    //     ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    //     verify(shellHelper).printError(valueCapture.capture());
    //     String actual = valueCapture.getValue();
    //     String expected = "Invalid account id provided";
    //     assertEquals(expected, actual);
    // }

    // @Test
    // public void runWithoutPreview() {
    //     accountDelete.setOldAccountInString("0.0.1001");
    //     accountDelete.setNewAccountInString("0.0.1002");
    //     // generate an Ed25519 private key for testing
    //     Ed25519PrivateKey oldAccountPrivKey = Ed25519PrivateKey.generate();
    //     when(inputReader.prompt(anyString(), anyString(), anyBoolean())).thenReturn(oldAccountPrivKey.toString());
    //     when(inputReader.prompt(
    //             "\nAccount to be deleted: " + accountDelete.getOldAccountInString() + "\nFunds from deleted account to be transferred to: "
    //                     + accountDelete.getNewAccountInString() + "\n\nIs this correct?" + "\nyes/no")).thenReturn(oldAccountPrivKey.toString());

    //     accountDelete.run();

    //     ArgumentCaptor<String> v1 = ArgumentCaptor.forClass(String.class);
    //     ArgumentCaptor<String> v2 = ArgumentCaptor.forClass(String.class);
    //     ArgumentCaptor<Boolean> v3 = ArgumentCaptor.forClass(Boolean.class);
    //     verify(inputReader).prompt(v1.capture(), v2.capture(), v3.capture());
    //     String actual1 = v1.getValue();
    //     String expected1 = "Enter the private key of the account to be deleted";
    //     assertEquals(expected1, actual1);
    //     String actual2 = v2.getValue();
    //     String expected2 = "secret";
    //     assertEquals(expected2, actual2);
    //     boolean actual3 = v3.getValue();
    //     boolean expected3 = false;
    //     assertEquals(expected3, actual3);

    //     ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    //     verify(shellHelper).printError(valueCapture.capture());
    //     String actual4 = valueCapture.getValue();
    //     String expected4 = "Nope, incorrect, let's make some changes";
    //     assertEquals(expected4, actual4);

    //     accountDelete.setSkipPreview(true);
    //     accountDelete.run();

    //     verify(hederaGrpc, times(1)).executeAccountDelete(any(), any(), any());
    // }

    // @Test
    // public void runWithPreviewCorrectInfo() {
    //     accountDelete.setOldAccountInString("0.0.1001");
    //     accountDelete.setNewAccountInString("0.0.1002");
    //     // generate an Ed25519 private key for testing
    //     Ed25519PrivateKey oldAccountPrivKey = Ed25519PrivateKey.generate();
    //     when(inputReader.prompt(anyString(), anyString(), anyBoolean())).thenReturn(oldAccountPrivKey.toString());
    //     when(inputReader.prompt(
    //             "\nAccount to be deleted: " + accountDelete.getOldAccountInString() + "\nFunds from deleted account to be transferred to: "
    //                     + accountDelete.getNewAccountInString() + "\n\nIs this correct?" + "\nyes/no")).thenReturn("yes");
    //     accountDelete.setSkipPreview(false);
    //     accountDelete.run();
    //     verify(hederaGrpc, times(1)).executeAccountDelete(any(), any(), any());
    //     verify(shellHelper, times(1)).print("Info is correct, let's go!");
    // }
}