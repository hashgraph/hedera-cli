package com.hedera.cli.hedera.crypto;

import com.hedera.cli.shell.ShellHelper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
public class ValidateTransferList {

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private ValidateAccounts validateAccounts;

    @Autowired
    private ValidateAmount validateAmount;

    private List<String> amountList;
    private List<String> senderList;
    private List<String> recipientList;
    private List<String> finalAmountList;
    private boolean isTiny;
    private CryptoTransferOptions cryptoTransferOptions;

    public void setCryptoTransferOptions(CryptoTransferOptions cryptoTransferOptions) {
        this.cryptoTransferOptions = cryptoTransferOptions;
    }

    public void updateAmountList(long sumOfRecipientAmount) {
        this.amountList = finalAmountList(amountList, sumOfRecipientAmount);
        setFinalAmountList(this.amountList);
    }

    public void setFinalAmountList(List<String> finalAmountList) {
        this.finalAmountList = finalAmountList;
    }

    public List<String> getFinalAmountList(CryptoTransferOptions cryptoTransferOptions) {
        setCryptoTransferOptions(cryptoTransferOptions);
        return this.finalAmountList;
    }

    public long sumOfAmountList() {
        long sumOfAmount;
        if (isTiny) {
            sumOfAmount = validateAmount.sumOfTinybarsInLong(amountList);
        } else {
            sumOfAmount = validateAmount.sumOfHbarsInLong(amountList);
        }
        return sumOfAmount;
    }

    public List<String> finalAmountList(List<String> amountList, long sumOfRecipientsAmount) {
        if (!isTiny) {
            finalAmountList = new ArrayList<>(convertAmountListToTinybar(amountList));
        } else {
            finalAmountList = new ArrayList<>(amountList);
        }
        String amount = "-" + sumOfRecipientsAmount;
        finalAmountList.add(0, amount);
        return finalAmountList;
    }

    public List<String> convertAmountListToTinybar(List<String> amountList) {
        List<String> convertedAmountList = new ArrayList<>();
        long hbarsToTiny = 0;
        for (int i = 0; i < amountList.size(); i++) {
            hbarsToTiny = validateAmount.convertHbarToLong(amountList.get(i));
            convertedAmountList.add(String.valueOf(hbarsToTiny));
        }
        return convertedAmountList;
    }

    private List<String> getAmountList(CryptoTransferOptions o) {
        return validateAmount.getAmountList(o);
    }

    private List<String> getSenderList(CryptoTransferOptions o) {
        return validateAccounts.getSenderList(o);
    }

    private List<String> getRecipientList(CryptoTransferOptions o) {
        return validateAccounts.getRecipientList(o);
    }

    private boolean isTiny(CryptoTransferOptions o) {
        return validateAmount.isTiny(o);
    }

    private boolean senderListHasOperator(CryptoTransferOptions o) {
        return validateAccounts.senderListHasOperator(o);
    }

    private boolean verifyZeroSum(long sumOfTransferAmount) {
        return validateAmount.verifyZeroSum(sumOfTransferAmount);
    }

    private boolean checkSum(CryptoTransferOptions o, List<String> amountList,
                          List<String> senderList, List<String> recipientList) {
        int amountSize = amountList.size();
        int transferSize = senderList.size() + recipientList.size();
        boolean amountListVerified = false;
        if (senderListHasOperator(o)) {
            if (amountSize != transferSize) {
                // add recipients amount and add to amount list
                long sumOfRecipientAmount = sumOfAmountList();
                if (sumOfRecipientAmount == -1L) return false;
                updateAmountList(sumOfRecipientAmount);
                long sumOfTransferAmount = sumOfAmountList();
                if (verifyZeroSum(sumOfTransferAmount)) {
                    amountListVerified = true;
                }
            } else {
                // assume amount already contains sender's amount
                amountListVerified = verifyCleanedAmountList();
            }
        } else {
            if (amountSize != transferSize) {
                shellHelper.printError("Invalid transfer list. Your transfer list must sum up to 0");
            } else {
                // assume amount already contains sender's amount
                amountListVerified = verifyCleanedAmountList();
            }
        }
        return amountListVerified;
    }

    public boolean verifyAmountList(CryptoTransferOptions o) {
        setCryptoTransferOptions(o);
        amountList = getAmountList(o);
        senderList = getSenderList(o);
        recipientList = getRecipientList(o);
        isTiny = isTiny(o);
        boolean amountListVerified = false;
        if (senderList.size() == 1) {
            amountListVerified = checkSum(o, amountList, senderList, recipientList);
        } else {
            shellHelper.printWarning("More than 2 senders not supported");
        }
        return amountListVerified;
    }

    public boolean verifyCleanedAmountList() {
        long sumOfTransferAmount = sumOfAmountList();
        if (sumOfTransferAmount == -1L) return false;
        if (!isTiny) {
            amountList = convertAmountListToTinybar(amountList);
        }
        setFinalAmountList(amountList);
        return validateAmount.verifyZeroSum(sumOfTransferAmount);
    }
}
