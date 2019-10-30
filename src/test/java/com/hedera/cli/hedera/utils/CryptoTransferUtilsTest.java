package com.hedera.cli.hedera.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CryptoTransferUtilsTest {

    @InjectMocks
    private CryptoTransferUtils cryptoTransferUtils;

    @Test
    public void verifyTransferInHbarsNotDeci() {
        String hbars = "110";
        long hbarsToTiny = cryptoTransferUtils.verifyTransferInHbars(hbars);
        assertEquals(11000000000L, hbarsToTiny);
    }

    @Test
    public void verifyTransferInHbarsResolveDecimals() {
        String hbars = "2.14";
        long hbarsToTiny = cryptoTransferUtils.verifyTransferInHbars(hbars);
        assertEquals(214000000L, hbarsToTiny);
    }

    @Test
    public void verifyTransferInTinybarsNotDeci() {
        String tiny = "1";
        long tinyVerified = cryptoTransferUtils.verifyTransferInTinyBars(tiny);
        assertEquals( 1L, tinyVerified);
    }

    @Test
    public void verifyTransferInTinybarsResolveDecimals() {
        String tiny = "0.7";
        long tinyVerified = cryptoTransferUtils.verifyTransferInTinyBars(tiny);
        assertEquals( 0L, tinyVerified);
    }
}
