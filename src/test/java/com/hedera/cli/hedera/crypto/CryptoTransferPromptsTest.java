package com.hedera.cli.hedera.crypto;

import java.util.HashMap;
import java.util.Map;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.cli.models.PreviewTransferList;

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
        cryptoTransferPrompts.promptPreview(operatorId, map);

        // check with some assertions
    }


}