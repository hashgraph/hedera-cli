package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.services.HederaGrpc;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountUpdateTest {

    @InjectMocks
    private AccountUpdate accountUpdate;

    @Mock
    private InputReader inputReader;

    @Mock
    private HederaGrpc hederaGrpc;

    @Mock
    private ShellHelper shellHelper;

    @Test
    public void runWithoutPreview() {

        String accountId = "0.0.1001";
        accountUpdate.setAccountIdInString(accountId);
        Ed25519PrivateKey originalKey = Ed25519PrivateKey.generate();
        Ed25519PrivateKey newKey = Ed25519PrivateKey.generate();
        accountUpdate.setNewKey(newKey);
        accountUpdate.setOriginalKey(originalKey);
        when(inputReader.prompt("Enter the NEW private key that will be used to update " + accountId, "secret", false)).thenReturn(accountUpdate.getNewKey().toString());
        when(inputReader.prompt("Enter the ORIGINAL private key of " + accountId + " that will be changed", "secret", false)).thenReturn(accountUpdate.getOriginalKey().toString());
        accountUpdate.setSkipPreview(true);
        accountUpdate.run();
        verify(hederaGrpc, times(1)).executeAccountUpdate(any(), any(), any());
    }

    @Test
    public void runWithPreviewCorrectInfo() {

        String accountId = "0.0.1001";
        accountUpdate.setAccountIdInString(accountId);
        Ed25519PrivateKey originalKey = Ed25519PrivateKey.generate();
        Ed25519PrivateKey newKey = Ed25519PrivateKey.generate();
        accountUpdate.setNewKey(newKey);
        accountUpdate.setOriginalKey(originalKey);
        when(inputReader.prompt("Enter the NEW private key that will be used to update " + accountId, "secret", false)).thenReturn(accountUpdate.getNewKey().toString());
        when(inputReader.prompt("Enter the ORIGINAL private key of " + accountId + " that will be changed", "secret", false)).thenReturn(accountUpdate.getOriginalKey().toString());

        String prompt = "\nAccount to be updated: " + accountId
                + "\n\nPublic key of account will be updated from: "
                + "\nPublic key in Encoded form: " + originalKey.getPublicKey()
                + "\nPublic key in HEX: " + originalKey.getPublicKey().toString().substring(24)
                + "\n\nTo new public key: "
                + "\nNEW Public key in Encoded form: " + newKey.getPublicKey()
                + "\nNEW Public key in HEX: " + newKey.getPublicKey().toString().substring(24)
                + "\n\nIs this correct?" + "\nyes/no";
        when(inputReader.prompt(prompt)).thenReturn("yes");
        accountUpdate.setSkipPreview(false);
        accountUpdate.run();
        verify(hederaGrpc, times(1)).executeAccountUpdate(any(), any(), any());
        verify(shellHelper, times(1)).print("Info is correct, let's go!");
    }

    @Test
    public void runWithPreviewIncorrectInfo() {

        String accountId = "0.0.1001";
        accountUpdate.setAccountIdInString(accountId);
        Ed25519PrivateKey originalKey = Ed25519PrivateKey.generate();
        Ed25519PrivateKey newKey = Ed25519PrivateKey.generate();
        accountUpdate.setNewKey(newKey);
        accountUpdate.setOriginalKey(originalKey);
        when(inputReader.prompt("Enter the NEW private key that will be used to update " + accountId, "secret", false)).thenReturn(accountUpdate.getNewKey().toString());
        when(inputReader.prompt("Enter the ORIGINAL private key of " + accountId + " that will be changed", "secret", false)).thenReturn(accountUpdate.getOriginalKey().toString());

        String prompt = "\nAccount to be updated: " + accountId
                + "\n\nPublic key of account will be updated from: "
                + "\nPublic key in Encoded form: " + originalKey.getPublicKey()
                + "\nPublic key in HEX: " + originalKey.getPublicKey().toString().substring(24)
                + "\n\nTo new public key: "
                + "\nNEW Public key in Encoded form: " + newKey.getPublicKey()
                + "\nNEW Public key in HEX: " + newKey.getPublicKey().toString().substring(24)
                + "\n\nIs this correct?" + "\nyes/no";
        when(inputReader.prompt(prompt)).thenReturn("no");
        accountUpdate.setSkipPreview(false);
        accountUpdate.run();
        verify(shellHelper, times(1)).printError("Nope, incorrect, let's make some changes");
    }

    @Test
    public void runWithAccountError() {
        accountUpdate.setAccountIdInString("1001");
        accountUpdate.run();
        verify(shellHelper, times(1)).printError("Invalid account id provided");
    }

    @Test
    public void runWhenNewKeysAreEmpty() {
        String accountId = "0.0.1001";
        accountUpdate.setAccountIdInString(accountId);
        Ed25519PrivateKey newKey = Ed25519PrivateKey.generate();
        accountUpdate.setNewKey(newKey);
        when(inputReader.prompt("Enter the NEW private key that will be used to update " + accountId, "secret", false)).thenReturn("");
        accountUpdate.run();
        verify(shellHelper, times(1)).printError("Enter the new public key to update the current account keys");
    }

    @Test
    public void runWhenNewKeysAreNotInEd25519Format() {
        String accountId = "0.0.1001";
        accountUpdate.setAccountIdInString(accountId);
        Ed25519PrivateKey newKey = Ed25519PrivateKey.generate();
        accountUpdate.setNewKey(newKey);
        when(inputReader.prompt("Enter the NEW private key that will be used to update " + accountId, "secret", false)).thenReturn("hello");
        accountUpdate.run();
        verify(shellHelper, times(1)).printError("Private key is not in the right ED25519 string format");
    }

    @Test
    public void runWhenOrigKeysAreEmpty() {
        String accountId = "0.0.1001";
        accountUpdate.setAccountIdInString(accountId);
        Ed25519PrivateKey newKey = Ed25519PrivateKey.generate();
        accountUpdate.setNewKey(newKey);
        when(inputReader.prompt("Enter the NEW private key that will be used to update " + accountId, "secret", false)).thenReturn(accountUpdate.getNewKey().toString());
        when(inputReader.prompt("Enter the ORIGINAL private key of " + accountId + " that will be changed", "secret", false)).thenReturn("");

        accountUpdate.run();
        verify(shellHelper, times(1)).printError("Enter the original public key to update the current account keys");
    }

    @Test
    public void runWhenOrigKeysAreNotInEd25519Format() {
        String accountId = "0.0.1001";
        accountUpdate.setAccountIdInString(accountId);
        Ed25519PrivateKey newKey = Ed25519PrivateKey.generate();
        accountUpdate.setNewKey(newKey);
        when(inputReader.prompt("Enter the NEW private key that will be used to update " + accountId, "secret", false)).thenReturn(accountUpdate.getNewKey().toString());
        when(inputReader.prompt("Enter the ORIGINAL private key of " + accountId + " that will be changed", "secret", false)).thenReturn("whatever");

        accountUpdate.run();
        verify(shellHelper, times(1)).printError("Private key is not in the right ED25519 string format");
    }
}
