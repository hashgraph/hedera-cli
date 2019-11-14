package com.hedera.cli.hedera.crypto;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class ValidateAmount {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private ValidateAccounts validateAccounts;

    private List<CryptoTransferOptions> cryptoTransferOptionsList;

    private String tinybarListArgs;
    private String hbarListArgs;
    private String amountListArgs;
    private List<String> amountList;
    private boolean tiny;

    public void setCryptoTransferOptionsList(List<CryptoTransferOptions> cryptoTransferOptionsList) {
        this.cryptoTransferOptionsList = cryptoTransferOptionsList;
        setHbarListArgs();
        setTinyBarListArgs();
        setTiny();
    }

    private void setHbarListArgs() {
        for (CryptoTransferOptions cryptoTransferOption : cryptoTransferOptionsList) {
            if (StringUtil.isNullOrEmpty(cryptoTransferOption.exclusive.transferListAmtHBars)) {
                shellHelper.printError("Amount in hbars must not be empty");
                hbarListArgs = null;
            } else {
                hbarListArgs = cryptoTransferOption.exclusive.transferListAmtHBars;
            }
        }
    }

    private void setTinyBarListArgs() {
        System.out.println(cryptoTransferOptionsList);
        for (CryptoTransferOptions cryptoTransferOption : cryptoTransferOptionsList) {
            if (StringUtil.isNullOrEmpty(cryptoTransferOption.exclusive.transferListAmtTinyBars)) {
                shellHelper.printError("Amount in tinybars must not be empty");
                tinybarListArgs = null;
            } else {
                tinybarListArgs = cryptoTransferOption.exclusive.transferListAmtTinyBars;
            }
        }
    }

    public List<String> getAmountList() {
        if (isTiny()) {
            amountListArgs = getTinybarListArgs();
        } else {
            amountListArgs = getHbarListArgs();
        }
        if (!StringUtil.isNullOrEmpty(amountListArgs)) {
            amountList = Arrays.asList(amountListArgs.split(","));
        }
        return amountList;
    }

    public boolean transactionAmountNotValid(String tinybarListArgs, String hbarListArgs) {
        if (StringUtil.isNullOrEmpty(hbarListArgs) && StringUtil.isNullOrEmpty(tinybarListArgs)) {
            shellHelper.printError("You have to provide transfer amounts either in hbars or tinybars");
            return true;
        }

        if (!StringUtil.isNullOrEmpty(hbarListArgs) && !StringUtil.isNullOrEmpty(tinybarListArgs)) {
            shellHelper.printError("Transfer amounts must either be in hbars or tinybars, not both");
            return true;
        }
        return false;
    }

    public void setTiny() {
        tinybarListArgs = getTinybarListArgs();
        hbarListArgs = getHbarListArgs();
        if (StringUtil.isNullOrEmpty(hbarListArgs) && !StringUtil.isNullOrEmpty(tinybarListArgs)) {
            tiny = true;
        } else {
            tiny = false;
        }
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

    public boolean check() {
        if (transactionAmountNotValid(tinybarListArgs, hbarListArgs)) {
            return false;
        }
        if (amountList == null) {
            return false;
        }
        return true;
    }
}
