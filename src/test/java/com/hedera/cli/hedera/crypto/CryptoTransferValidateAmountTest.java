package com.hedera.cli.hedera.crypto;

import com.hedera.cli.shell.ShellHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class CryptoTransferValidateAmountTest {

    @InjectMocks
    private CryptoTransferValidateAmount cryptoTransferValidateAmount;

    @Mock
    private ShellHelper shellHelper;

    @Test
    public void transactionAmountNotValid() {
        cryptoTransferValidateAmount.transactionAmountNotValid();
        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).printError(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "You have to provide transfer amounts either in hbars or tinybars";
        assertEquals(expected, actual);
    }

    @Test
    public void transactionAmountNotValidBoth() {
        cryptoTransferValidateAmount.setTinybarListArgs("100");
        cryptoTransferValidateAmount.setHbarListArgs("0.1");
        when(cryptoTransferValidateAmount.tinybarListArgs()).thenReturn(cryptoTransferValidateAmount.getTinybarListArgs());
        when(cryptoTransferValidateAmount.hbarListArgs()).thenReturn(cryptoTransferValidateAmount.getHbarListArgs());
        cryptoTransferValidateAmount.transactionAmountNotValid();
        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).printError(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "Transfer amounts must either be in hbars or tinybars, not both";
        assertEquals(expected, actual);
    }
}
