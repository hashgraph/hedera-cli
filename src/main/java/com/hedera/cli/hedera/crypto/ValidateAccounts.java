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

    public void cryptoTransferOptions(CryptoTransferOptions cryptoTransferOptions) {
        this.cryptoTransferOptions = cryptoTransferOptions;
        senderListArgs();
        recipientListArgs();
        senderList();
        recipientList();
    }

    private void senderListArgs() {
        if (StringUtil.isNullOrEmpty(cryptoTransferOptions.dependent.senderList)) {
            senderListArgs = hedera.getOperatorId().toString();
        } else {
            senderListArgs = cryptoTransferOptions.dependent.senderList;
        }
    }

    private void recipientListArgs() {
        recipientListArgs = cryptoTransferOptions.dependent.recipientList;
    }

    private void senderList() {
        if (!StringUtil.isNullOrEmpty(senderListArgs)) {
            senderList = Arrays.asList(senderListArgs.split(","));
        }
        System.out.println("senderList  " + senderList);
    }

    private void recipientList() {
        if (!StringUtil.isNullOrEmpty(recipientListArgs)) {
            recipientList = Arrays.asList(recipientListArgs.split(","));
        }
        System.out.println("recipientList " + recipientList);
    }

    public List<String> getTransferList() {
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

    public boolean check() {
        if (senderList == null) {
            return false;
        }
        if (recipientList == null) {
            return false;
        }
        if (!verifyListHasAccountIdFormat(senderList)) {
            return false;
        }
        if (!verifyListHasAccountIdFormat(recipientList)) {
            return false;
        }
        return true;
    }

    public boolean senderListHasOperator() {
        for (String s : senderList) {
            if (s.contains(hedera.getOperatorId().toString())) {
                return true;
            }
        }
        return false;
    }
}