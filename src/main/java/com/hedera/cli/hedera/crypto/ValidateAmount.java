package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.shell.ShellHelper;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.ArgGroup;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Component
public class ValidateAmount {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private KryptoKransfer kryptoKransfer;

    @Autowired
    private ValidateAccounts validateAccounts;

    private String tinybarListArgs;
    private String hbarListArgs;
    private String amountListArgs;
    private List<String> amountList;

//    public String hbarListArgs() {
//        for (CryptoTransferOptions cryptoTransferOption : cryptoTransferOptionsList) {
//            if (StringUtil.isNullOrEmpty(cryptoTransferOption.exclusive.transferListAmtHBars)) {
//                shellHelper.printError("Amount in hbars must not be empty");
//                hbarListArgs = null;
//            } else {
//                hbarListArgs = cryptoTransferOption.exclusive.transferListAmtHBars;
//            }
//        }
//        return hbarListArgs;
//    }
//
//    public String tinybarListArgs() {
//        System.out.println(cryptoTransferOptionsList);
//        for (CryptoTransferOptions cryptoTransferOption : cryptoTransferOptionsList) {
//            if (StringUtil.isNullOrEmpty(cryptoTransferOption.exclusive.transferListAmtTinyBars)) {
//                shellHelper.printError("Amount in tinybars must not be empty");
//                tinybarListArgs = null;
//            } else {
//                tinybarListArgs = cryptoTransferOption.exclusive.transferListAmtTinyBars;
//            }
//        }
//        return tinybarListArgs;
//    }

    public List<String> amountList(boolean isTiny) {
        if (isTiny) {
            amountListArgs = kryptoKransfer.tinybarListArgs();
        } else {
            amountListArgs = kryptoKransfer.hbarListArgs();
        }
        if (!StringUtil.isNullOrEmpty(amountListArgs)) {
            amountList = Arrays.asList(amountListArgs.split(","));
        }
        return amountList;
    }

    public void transactionAmountNotValid(String tinybarListArgs, String hbarListArgs) {
        if (StringUtil.isNullOrEmpty(hbarListArgs) && StringUtil.isNullOrEmpty(tinybarListArgs)) {
            shellHelper.printError("You have to provide transfer amounts either in hbars or tinybars");
            return;
        }

        if (!StringUtil.isNullOrEmpty(hbarListArgs) && !StringUtil.isNullOrEmpty(tinybarListArgs)) {
            shellHelper.printError("Transfer amounts must either be in hbars or tinybars, not both");
            return;
        }
    }

    public boolean isTiny() {
        tinybarListArgs = kryptoKransfer.tinybarListArgs();
        hbarListArgs = kryptoKransfer.hbarListArgs();
        return StringUtil.isNullOrEmpty(hbarListArgs) && !StringUtil.isNullOrEmpty(tinybarListArgs);
    }

    public long sumOfTinybarsInLong(List<String> amountList) {
        long sum = 0;
        long tinyBarsVerified;

        for (int i = 0; i < amountList.size(); i++) {
            if ("0".equals(amountList.get(i))) {
                shellHelper.printError("Tinybars must be more or less than 0");
                return -1;
            }
            try {
                tinyBarsVerified = convertTinybarToLong(amountList.get(i));
            } catch (Exception e) {
                shellHelper.printError("Tinybars must not be a decimal");
                return -1;
            }
            sum += tinyBarsVerified;
        }
        return sum;
    }

    public boolean verifyZeroSum(long sum) {
        boolean zeroSum = false;
        if (sum == 0L) {
            zeroSum = true;
        } else {
            shellHelper.printError("Invalid transfer list. Your transfer list must sum up to 0");
        }
        return zeroSum;
    }

    public long convertTinybarToLong(String amountInTinybar) {
        return Long.parseLong(amountInTinybar);
    }

    public long sumOfHbarsInLong(List<String> amountList) {
        long sum = 0;
        long hbarsToTiny;

        for (int i = 0; i < amountList.size(); i++) {
            if ("0".equals(amountList.get(i))) {
                shellHelper.printError("Hbars must be more or less than 0");
                return 0;
            }
            hbarsToTiny = convertHbarToLong(amountList.get(i));
            sum += hbarsToTiny;
        }
        return sum;
    }

    public long convertHbarToLong(String amountInHbar) {
        long hbarsToTiny;
        try {
            long amountInHbars = Long.parseLong(amountInHbar);
            long tinyConversion = 100000000L;
            hbarsToTiny = amountInHbars * tinyConversion;
        } catch (Exception e) {
            BigDecimal bd = new BigDecimal(amountInHbar);
            BigDecimal bdConvertTiny = bd.multiply(new BigDecimal("100000000"));
            hbarsToTiny = Long.parseLong(bdConvertTiny.toPlainString().split("\\.")[0]);
        }
        return hbarsToTiny;
    }
}
