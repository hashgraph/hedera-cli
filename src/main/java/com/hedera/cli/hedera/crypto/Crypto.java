package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.utils.AccountManager;
import com.hedera.cli.shell.ShellHelper;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Spec;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Component
public class Crypto implements Runnable {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private InputReader inputReader;

    @Autowired
    private AccountManager accountManager;

    @Autowired
    private CryptoTransferOptions cryptoTransferOptions;

    @Spec
    private CommandSpec spec;

    @ArgGroup(exclusive = false, multiplicity = "1")
    private List<CryptoTransferOptions> cryptoTransferOptionsList;

    private String transferListArgs;
    private String tinybarAmtArgs;
    private String hbarAmtArgs;
    private String mPreview = "no";
    private boolean isTiny;

    private List<String> transferList;
    private List<String> amountList;

    @Override
    public void run() {

        for (int i = 0; i < cryptoTransferOptionsList.size(); i++) {
            // get the exclusive arg by user ie tinybars or hbars
            cryptoTransferOptions = cryptoTransferOptionsList.get(i);
        }

        hbarAmtArgs = cryptoTransferOptions.exclusive.recipientAmtHBars;
        tinybarAmtArgs = cryptoTransferOptions.exclusive.recipientAmtTinyBars;
        transferListArgs = cryptoTransferOptions.dependent.senderList + "," + cryptoTransferOptions.dependent.recipientList;
        mPreview = cryptoTransferOptions.dependent.mPreview;

        if (StringUtil.isNullOrEmpty(tinybarAmtArgs) && StringUtil.isNullOrEmpty(hbarAmtArgs)
                || (!StringUtil.isNullOrEmpty(tinybarAmtArgs) && !StringUtil.isNullOrEmpty(hbarAmtArgs))) {
            shellHelper.printError("You have to provide a transaction amount in hbars or tinybars");
            return;
        }

        if (!StringUtil.isNullOrEmpty(tinybarAmtArgs)) {
            // tinybars not empty
            shellHelper.printInfo("here in tiny loop");

            // Verify transferlist and amountlist are equal
            transferList = Arrays.asList(transferListArgs.split(","));
            amountList = Arrays.asList(tinybarAmtArgs.split(","));
            boolean listAreEqual = verifyEqualList(transferList, amountList);
            if (!listAreEqual) return;

            // Verify list of senders and recipients
            boolean transferListVerified = verifyTransferList(transferList);
            if (!transferListVerified) return;

            // Check sum of transfer is zero
            isTiny = true;
            boolean isZeroSum = verifySumOfTransfer(amountList, isTiny);
            if (!isZeroSum) return;
        }

        if (!StringUtil.isNullOrEmpty(hbarAmtArgs)) {
            //hbars not empty
            shellHelper.printInfo("here in hbar loop");

            // Verify transferlist and amountlist are equal
            transferList = Arrays.asList(transferListArgs.split(","));
            amountList = Arrays.asList(hbarAmtArgs.split(","));
            boolean listAreEqual = verifyEqualList(transferList, amountList);
            if (!listAreEqual) return;

            // Verify list of senders and recipients
            boolean transferListVerified = verifyTransferList(transferList);
            if (!transferListVerified) return;

            // Check sum of transfer is zero
            isTiny = false;
            boolean isZeroSum = verifySumOfTransfer(amountList, isTiny);
            if (!isZeroSum) return;

        } else {
            shellHelper.printError("Error in commandline");
        }
    }

    public boolean verifyEqualList(List<String> transferList, List<String> amountList) {
        if (transferList.size() != amountList.size()) {
            shellHelper.printError("Lists aren't the same size");
            return false;
        }
        return true;
    }

    public boolean verifyTransferList(List<String> transferList) {
        String accountId;
        for (int i = 0; i < transferList.size(); i++) {
            accountId = transferList.get(i);
            if (!accountManager.isAccountId(accountId)) {
                shellHelper.printError("Please check that accountId is in the right format");
                return false;
            }
        }
        return true;
    }

    public boolean verifySumOfTransfer(List<String> amountList, boolean isTiny) {
        boolean verifyZeroSum;
        if (isTiny) {
            verifyZeroSum = verifyTinybarsInLong(amountList);
            if (!verifyZeroSum) {
                shellHelper.printError("Tinybars must be in whole numbers");
                verifyZeroSum = false;
            }
        } else {
            verifyZeroSum = verifyHbarsInLong(amountList);
            if (!verifyZeroSum) {
                shellHelper.printError("Total sum must add up to zero");
                verifyZeroSum = false;
            }
        }
        System.out.println("zero sum game? " + verifyZeroSum);
        return verifyZeroSum;
    }

    public boolean verifyHbarsInLong(List<String> amountList) {
        long sum = 0;
        long hbarsToTiny;

        for (int i = 0; i < amountList.size(); i++) {
            if ("0".equals(amountList.get(i))) {
                shellHelper.printError("Hbars must be more or less than 0");
                return false;
            }
            try {
                long amountInHbars = Long.parseLong(amountList.get(i));
                long tinyConversion = 100000000L;
                hbarsToTiny = amountInHbars * tinyConversion;
            } catch (Exception e) {
                BigDecimal bd = new BigDecimal(amountList.get(i));
                BigDecimal bdConvertTiny = bd.multiply(new BigDecimal("100000000"));
                hbarsToTiny = Long.parseLong(bdConvertTiny.toPlainString().split("\\.")[0]);
            }
            sum += hbarsToTiny;
        }
        System.out.println("sum here in hbar is " + sum);
        return verifyZeroSum(sum);
    }

    public boolean verifyZeroSum(long sum) {
        return sum == 0L;
    }

    public boolean verifyTinybarsInLong(List<String> amountList) {
        long sum = 0;
        long tinyBarsVerified;

        for (int i = 0; i < amountList.size(); i++) {
            if ("0".equals(amountList.get(i))) {
                shellHelper.printError("Tinybars must be more or less than 0");
                return false;
            }
            try {
                tinyBarsVerified = Long.parseLong(amountList.get(i));
            } catch (Exception e) {
                shellHelper.printError("Tinybars must not be a decimal");
                return false;
            }
            sum += tinyBarsVerified;
        }
        System.out.println("sum here in tiny is " + sum);
        return verifyZeroSum(sum);
    }
}
