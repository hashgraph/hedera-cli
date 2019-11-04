package com.hedera.cli.hedera.crypto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CryptoTransferOptionsTest {

    @Test
    public void nestedClassesExist() {
        CryptoTransferOptions cryptoTransferOptions = new CryptoTransferOptions();
        CryptoTransferOptions.Dependent dependent = new CryptoTransferOptions.Dependent();
        CryptoTransferOptions.Exclusive exclusive = new CryptoTransferOptions.Exclusive();

        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        assertEquals(dependent, cryptoTransferOptions.getDependent());
        assertEquals(exclusive, cryptoTransferOptions.getExclusive());
    }

    @Test
    public void nestedDependent() {
        CryptoTransferOptions.Dependent dependent = new CryptoTransferOptions.Dependent();
        dependent.setMPreview("no");
        dependent.setRecipientList("0.0.114152,0.0.11667");
        dependent.setSenderList("0.0.116681,0.0.117813");
        assertEquals("no", dependent.getMPreview());
        assertEquals("0.0.114152,0.0.11667", dependent.getRecipientList());
        assertEquals("0.0.116681,0.0.117813", dependent.getSenderList());
}

    @Test
    public void nestedExclusive() {
        CryptoTransferOptions.Exclusive exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtHBars("0.4,0.01,1.33");
        exclusive.setTransferListAmtTinyBars("400,1000,10030");
        assertEquals("0.4,0.01,1.33" , exclusive.getTransferListAmtHBars());
        assertEquals("400,1000,10030" , exclusive.getTransferListAmtTinyBars());
    }
}
