package com.hedera.cli.hedera.crypto;

import com.hedera.cli.shell.ShellHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class ValidateAmountTest {

    @InjectMocks
    private ValidateAmount validateAmount;

    @Mock
    private ShellHelper shellHelper;

    @Mock
    private CryptoTransferOptions cryptoTransferOptions;

    private CryptoTransferOptions.Exclusive exclusive;
    private CryptoTransferOptions.Dependent dependent;
    private List<CryptoTransferOptions> cryptoTransferOptionsList;
//
//    @BeforeEach
//    public void setUp() {
//        cryptoTransferOptions.setDependent(dependent);
//        cryptoTransferOptions.setExclusive(exclusive);
//        validateAmount.setCryptoTransferOptions(cryptoTransferOptions);
//        validateAmount.setCryptoTransferOptionsList(cryptoTransferOptionsList);
//    }
//
//    @Test
//    public void transactionAmountNotValid() {
//        validateAmount.transactionAmountNotValid();
//        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
//        verify(shellHelper).printError(valueCapture.capture());
//        String actual = valueCapture.getValue();
//        String expected = "You have to provide transfer amounts either in hbars or tinybars";
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void transactionAmountNotValidBoth() {
//        validateAmount.setCryptoTransferOptionsList(cryptoTransferOptionsList);
//        when(cryptoTransferOptionsList).thenReturn(cryptoTransferOptionsList);
//        validateAmount.setTinybarListArgs("100");
//        validateAmount.setHbarListArgs("0.1");
//        when(validateAmount.tinybarListArgs()).thenReturn(validateAmount.getTinybarListArgs());
//        when(validateAmount.hbarListArgs()).thenReturn(validateAmount.getHbarListArgs());
//        validateAmount.transactionAmountNotValid();
//        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
//        verify(shellHelper).printError(valueCapture.capture());
//        String actual = valueCapture.getValue();
//        String expected = "Transfer amounts must either be in hbars or tinybars, not both";
//        assertEquals(expected, actual);
//    }
}
