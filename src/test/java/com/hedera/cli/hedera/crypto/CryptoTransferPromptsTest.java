package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.models.PreviewTransferList;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CryptoTransferPromptsTest {

    @InjectMocks
    private CryptoTransferPrompts cryptoTransferPrompts;

    @Mock
    private ShellHelper shellHelper;

    @Mock
    private InputReader inputReader;

    @Test
    public void testPromptPreview() {
        AccountId operatorId = AccountId.fromString("0.0.1001");
        Map<Integer, PreviewTransferList> map = new HashMap<>();
        String result = cryptoTransferPrompts.promptPreview(operatorId, map);
        assertEquals(result, null);
        verify(shellHelper, times(0)).printError(any());

        // ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        // String actual = valueCapture.getValue();
        // String expected = "Balance: " + Long.toString(balance, 10);
        // assertEquals(expected, actual);
        // System.out.println(actual);
        assertNotNull(shellHelper);
        assertNotNull(inputReader);
    }

}