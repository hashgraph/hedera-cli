package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
import com.hedera.hashgraph.sdk.account.AccountId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class CryptoTransferValidateAccountsTest {

    @InjectMocks
    private CryptoTransferValidateAccounts cryptoTransferValidateAccounts;

    @Mock
    private Hedera hedera;

    @Test
    public void senderListHasNoOperator() {
        List<String> senderList = new ArrayList<>();
        senderList.add("0.0.1002");
        when(hedera.getOperatorId()).thenReturn(AccountId.fromString("0.0.1001"));
        assertFalse(cryptoTransferValidateAccounts.senderListHasOperator(senderList));
    }

    @Test
    public void senderListHasOperator() {
        List<String> senderList = new ArrayList<>();
        senderList.add("0.0.1001");
        when(hedera.getOperatorId()).thenReturn(AccountId.fromString("0.0.1001"));
        assertTrue(cryptoTransferValidateAccounts.senderListHasOperator(senderList));
    }

}
