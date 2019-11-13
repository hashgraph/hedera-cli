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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Component
public class CryptoTransferValidateAccounts {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    @ArgGroup(exclusive = false, multiplicity = "1")
    private List<CryptoTransferOptions> cryptoTransferOptionsList;

    private String senderListArgs;
    private String recipientListArgs;
    private List<String> senderList;
    private List<String> recipientList;

    public String senderListArgs() {
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

    public List<String> senderList() {
        senderListArgs = senderListArgs();
        if (!StringUtil.isNullOrEmpty(senderListArgs)) {
            senderList = Arrays.asList(senderListArgs.split(","));
        }
        return senderList;
    }

    public List<String> recipientList() {
        recipientListArgs = recipientListArgs();
        if (!StringUtil.isNullOrEmpty(recipientListArgs)) {
            recipientList = Arrays.asList(recipientListArgs.split(","));
        }
        return recipientList;
    }

    public List<String> createTransferList(List<String> senderList, List<String> recipientList) {
        return Stream.of(senderList, recipientList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
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
