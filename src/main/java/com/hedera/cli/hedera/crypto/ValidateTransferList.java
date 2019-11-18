package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
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
    private Hedera hedera;

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
            amountList = convertAmountListToTinybar(amountList);
        }
        finalAmountList = new ArrayList<>(amountList);
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

    public boolean verifyAmountList(CryptoTransferOptions o) {
        setCryptoTransferOptions(o);
        boolean amountListVerified = false;
        amountList = validateAmount.getAmountList(o);
        int amountSize = amountList.size();
        senderList = validateAccounts.getSenderList(o);
        recipientList = validateAccounts.getRecipientList(o);
        int transferSize = senderList.size() + recipientList.size();
        isTiny = validateAmount.isTiny(o);
        switch (senderList.size()) {
            case 1:
                if (validateAccounts.senderListHasOperator(o)) {
                    if (amountSize != transferSize) {
                        // add recipients amount and add to amount list
                        long sumOfRecipientAmount = sumOfAmountList();
                        if (sumOfRecipientAmount == -1L) return false;
                        updateAmountList(sumOfRecipientAmount);
                        long sumOfTransferAmount = sumOfAmountList();
                        if (validateAmount.verifyZeroSum(sumOfTransferAmount)) {
                            amountListVerified = true;
                        }
                    } else {
                        // assume amount already contains sender's amount
                        long sumOfTransferAmount = sumOfAmountList();
                        if (sumOfTransferAmount == -1L) return false;
                        if (validateAmount.verifyZeroSum(sumOfTransferAmount)) {
                            amountListVerified = true;
                        }
                    }
                } else {
                    if (amountSize != transferSize) {
                        shellHelper.printError("Invalid transfer list. Your transfer list must sum up to 0");
                    } else {
                        // assume amount already contains sender's amount
                        long sumOfTransferAmount = sumOfAmountList();
                        if (sumOfTransferAmount == -1L) return false;
                        setFinalAmountList(amountList);
                        if (validateAmount.verifyZeroSum(sumOfTransferAmount)) {
                            amountListVerified = true;
                        }
                    }
                }
                break;
            default:
                shellHelper.printWarning("More than 2 senders not supported");
//                if (validateAccounts.senderListHasOperator(o)) {
//                    if (amountSize != transferSize) {
//                        // add recipients amount and add to amount list
//                        long sumOfRecipientAmount = sumOfAmountList();
//                        if (sumOfRecipientAmount == -1L) return false;
//                        updateAmountList(sumOfRecipientAmount);
//                        long sumOfTransferAmount = sumOfAmountList();
//                        if (validateAmount.verifyZeroSum(sumOfTransferAmount)) {
//                            amountListVerified = true;
//                        }
//                    } else {
//                        // assume amount already contains sender's amount
//                        long sumOfTransferAmount = sumOfAmountList();
//                        if (sumOfTransferAmount == -1L) return false;
//                        if (validateAmount.verifyZeroSum(sumOfTransferAmount)) {
//                            amountListVerified = true;
//                        }
//                    }
//                } else {
//                    if (amountSize != transferSize) {
//                        shellHelper.printError("Invalid transfer list. Your transfer list must sum up to 0");
//                    } else {
//                        // assume amount already contains sender's amount
//                        long sumOfTransferAmount = sumOfAmountList();
//                        if (sumOfTransferAmount == -1L) return false;
//                        setFinalAmountList(amountList);
//                        if (validateAmount.verifyZeroSum(sumOfTransferAmount)) {
//                            amountListVerified = true;
//                        }
//                    }
//                }
                break;
        }
        return amountListVerified;
    }
}
