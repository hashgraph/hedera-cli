package com.hedera.cli.hedera.utils;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CryptoTransferUtils {

    public long verifyTransferInHbars(String hBars) {
        long hbarsToTiny;
        try {
            long amountInHbars = Long.parseLong(hBars);
            long tinyConversion = 100000000L;
            hbarsToTiny = amountInHbars * tinyConversion;
        } catch (Exception e) {
            BigDecimal bd = new BigDecimal(hBars);
            BigDecimal bdConvertTiny = bd.multiply(new BigDecimal("100000000"));
            hbarsToTiny = Long.parseLong(bdConvertTiny.toPlainString().split("\\.")[0]);
        }
        return hbarsToTiny;
    }


    public long verifyTransferInTinyBars(String tinyBars) {
        long tinyBarsVerified;
        try {
            tinyBarsVerified = Long.parseLong(tinyBars);
        } catch (Exception e) {
            tinyBarsVerified = Long.parseLong("0");
        }
        return tinyBarsVerified;
    }
}
