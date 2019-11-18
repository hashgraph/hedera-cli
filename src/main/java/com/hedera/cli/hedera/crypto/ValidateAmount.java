package com.hedera.cli.hedera.crypto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ValidateAmount {

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private ValidateAccounts validateAccounts;

    private CryptoTransferOptions cryptoTransferOptions;

    private String tinybarListArgs;
    private String hbarListArgs;
    private String amountListArgs;
    private List<String> amountList;
    private boolean tiny;

    public void setCryptoTransferOptions(CryptoTransferOptions cryptoTransferOptions) {
        this.cryptoTransferOptions = cryptoTransferOptions;
        setHbarListArgs();
        setTinyBarListArgs();
        setTiny();
    }

    private void setHbarListArgs() {
        if (!StringUtil.isNullOrEmpty(this.cryptoTransferOptions.exclusive.transferListAmtHBars) &&
                StringUtil.isNullOrEmpty(this.cryptoTransferOptions.exclusive.transferListAmtTinyBars)) {
            hbarListArgs = this.cryptoTransferOptions.exclusive.transferListAmtHBars;
        }
    }

    private void setTinyBarListArgs() {
        if (!StringUtil.isNullOrEmpty(this.cryptoTransferOptions.exclusive.transferListAmtTinyBars) &&
                StringUtil.isNullOrEmpty(this.cryptoTransferOptions.exclusive.transferListAmtHBars)) {
            tinybarListArgs = this.cryptoTransferOptions.exclusive.transferListAmtTinyBars;
        }
    }

    public List<String> getAmountList(CryptoTransferOptions cryptoTransferOptions) {
        setCryptoTransferOptions(cryptoTransferOptions);
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

    public boolean transactionAmountNotValid() {
        if (StringUtil.isNullOrEmpty(getHbarListArgs()) && StringUtil.isNullOrEmpty(getTinybarListArgs())) {
            shellHelper.printError("You have to provide transfer amounts either in hbars or tinybars");
            return true;
        }
        if (!StringUtil.isNullOrEmpty(getHbarListArgs()) && !StringUtil.isNullOrEmpty(getTinybarListArgs())) {
            shellHelper.printError("Transfer amounts must either be in hbars or tinybars, not both");
            return true;
        }
        return false;
    }

    public void setTiny() {
        tiny = StringUtil.isNullOrEmpty(getHbarListArgs()) && !StringUtil.isNullOrEmpty(getTinybarListArgs());
    }

    public boolean isTiny(CryptoTransferOptions cryptoTransferOptions) {
        setCryptoTransferOptions(cryptoTransferOptions);
        return tiny;
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
                return -1;
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

    public String convertLongToHbar(String amt) {
        BigDecimal bd = new BigDecimal(amt);
        BigDecimal convertedAmt = bd.divide(new BigDecimal("100000000"), 8, RoundingMode.HALF_DOWN);
        return convertedAmt.toPlainString();
    }

    public boolean check(CryptoTransferOptions cryptoTransferOptions) {
        setCryptoTransferOptions(cryptoTransferOptions);
        return !transactionAmountNotValid();
    }
}
