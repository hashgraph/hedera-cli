package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine.ArgGroup;

import java.util.List;

@Getter
@Setter
@Component
public class CryptoTransferValidation {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private CryptoTransferOptions cryptoTransferOptions;

    @Autowired
    private CryptoTransferValidation cryptoTransferValidation;

    @ArgGroup(exclusive = false, multiplicity = "1")
    private List<CryptoTransferOptions> cryptoTransferOptionsList;

    public String senderListArgs() {
        String senderListArgs = null;
        for (CryptoTransferOptions cryptoTransferOption : cryptoTransferOptionsList) {
            if (StringUtil.isNullOrEmpty(cryptoTransferOption.dependent.senderList)) {
                senderListArgs = hedera.getOperatorId().toString();
            } else {
                senderListArgs = cryptoTransferOption.dependent.senderList;
            }
        }
        return senderListArgs;
    }

    public String recipientListArgs() {
        String recipientListArgs = null;
        for (CryptoTransferOptions cryptoTransferOption : cryptoTransferOptionsList) {
            if (StringUtil.isNullOrEmpty(cryptoTransferOption.dependent.recipientList)) {
                shellHelper.printError("Recipient list must not be empty");
                recipientListArgs = null;
            } else {
                recipientListArgs = cryptoTransferOption.dependent.recipientList;
            }
        }
        return recipientListArgs;
    }

    public String hbarListArgs() {
        String hbarListArgs = null;
        for (CryptoTransferOptions cryptoTransferOption : cryptoTransferOptionsList) {
            if (StringUtil.isNullOrEmpty(cryptoTransferOption.exclusive.transferListAmtHBars)) {
                shellHelper.printError("Amount in hbars must not be empty");
                hbarListArgs = null;
            } else {
                hbarListArgs = cryptoTransferOption.exclusive.transferListAmtHBars;
            }
        }
        return hbarListArgs;
    }

    public String tinybarListArgs() {
        String tinybarListArgs = null;
        for (CryptoTransferOptions cryptoTransferOption : cryptoTransferOptionsList) {
            if (StringUtil.isNullOrEmpty(cryptoTransferOption.exclusive.transferListAmtTinyBars)) {
                shellHelper.printError("Amount in tinybars must not be empty");
                tinybarListArgs = null;
            } else {
                tinybarListArgs = cryptoTransferOption.exclusive.transferListAmtTinyBars;
            }
        }
        return tinybarListArgs;
    }
    
    public boolean skipPreviewArgs() {
        boolean skipPreview = false;
        for (CryptoTransferOptions cryptoTransferOption : cryptoTransferOptionsList) {
            skipPreview = cryptoTransferOption.dependent.skipPreview;
        }
        return skipPreview;
    }

    public boolean senderListHasOperator(List<String> senderList) {
        for (String s : senderList) {
            if (s.contains(hedera.getOperatorId().toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean verifyListHasAccountIdFormat(List<String> list) {
        for (String accountIdInString : list) {
            try {
                AccountId.fromString(accountIdInString);
            } catch (Exception e) {
                shellHelper.printError("Invalid account id in list");
                return false;
            }
        }
        return true;
    }
}
