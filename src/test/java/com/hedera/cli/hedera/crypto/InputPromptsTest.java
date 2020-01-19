package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.models.AccountManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InputPromptsTest {

    @InjectMocks
    private InputPrompts inputPrompts;

    @Mock
    private InputReader inputReader;

    @Mock
    private AccountManager accountManager;

    @Test
    public void passphrasePrompt() {
        when(inputReader.prompt("Recover account using 24 words or keys? Enter words/keys")).thenReturn("words");
        boolean wordsActual = inputPrompts.keysOrPassphrasePrompt(inputReader);
        assertTrue(wordsActual);
    }

    @Test
    public void keysPrompt() {
        when(inputReader.prompt("Recover account using 24 words or keys? Enter words/keys")).thenReturn("keys");
        boolean wordsActual = inputPrompts.keysOrPassphrasePrompt(inputReader);
        assertFalse(wordsActual);
    }

    @Test
    public void methodPromptBip() {
        String bip = "bip";
        when(inputReader
                .prompt("Have you migrated your account on Hedera wallet? If migrated, enter `bip`, else enter `hgc`"))
                        .thenReturn(bip);
        when(accountManager.verifyMethod(bip)).thenReturn(bip);
        String isBip = inputPrompts.methodPrompt(inputReader, accountManager);
        assertEquals(bip, isBip);
    }

    @Test
    public void methodPromptHgc() {
        String hgc = "hgc";
        when(inputReader
                .prompt("Have you migrated your account on Hedera wallet? If migrated, enter `bip`, else enter `hgc`"))
                        .thenReturn(hgc);
        when(accountManager.verifyMethod(hgc)).thenReturn(hgc);
        String isHgc = inputPrompts.methodPrompt(inputReader, accountManager);
        assertEquals(hgc, isHgc);
    }
}
