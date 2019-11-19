package com.hedera.cli.hedera.crypto;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;

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
public class ValidateAccounts {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    // setter, when invoked, should set the values for senderListArgs,
    // recipientListArgs, senderList and recipientList
    private CryptoTransferOptions cryptoTransferOptions;

    // values are populated when setCryptotransferOptionsList is invoked
    private String senderListArgs;
    private String recipientListArgs;
    private List<String> senderList;
    private List<String> recipientList;

    public void setCryptoTransferOptions(CryptoTransferOptions o) {
        this.cryptoTransferOptions = o;
        setSenderListArgs();
        setRecipientListArgs();
        setSenderList();
        setRecipientList();
    }

    private void setSenderListArgs() {
        if (StringUtil.isNullOrEmpty(this.cryptoTransferOptions.dependent.senderList)) {
            senderListArgs = hedera.getOperatorId().toString();
        } else {
            senderListArgs = this.cryptoTransferOptions.dependent.senderList;
        }
    }

    private void setRecipientListArgs() {
        recipientListArgs = this.cryptoTransferOptions.dependent.recipientList;
    }

    private void setSenderList() {
        if (!StringUtil.isNullOrEmpty(senderListArgs)) {
            senderList = Arrays.asList(senderListArgs.split(","));
        }
    }

    public List<String> getSenderList(CryptoTransferOptions o) {
        setCryptoTransferOptions(o);
        return senderList;
    }

    public List<String> getRecipientList(CryptoTransferOptions o) {
        setCryptoTransferOptions(o);
        return recipientList;
    }

    private void setRecipientList() {
        if (!StringUtil.isNullOrEmpty(recipientListArgs)) {
            recipientList = Arrays.asList(recipientListArgs.split(","));
        }
    }

    public List<String> getTransferList(CryptoTransferOptions o) {
        setCryptoTransferOptions(o);
        return Stream.of(senderList, recipientList).flatMap(Collection::stream).collect(Collectors.toList());
    }

    private boolean verifyListHasAccountIdFormat(List<String> list) {
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

    public boolean check(CryptoTransferOptions o) {
        setCryptoTransferOptions(o);
        return (verifyListHasAccountIdFormat(senderList) && verifyListHasAccountIdFormat(recipientList));
    }

    public boolean senderListHasOperator(CryptoTransferOptions o) {
        setCryptoTransferOptions(o);
        for (String s : senderList) {
            if (s.contains(hedera.getOperatorId().toString())) {
                return true;
            }
        }
        return false;
    }
}
