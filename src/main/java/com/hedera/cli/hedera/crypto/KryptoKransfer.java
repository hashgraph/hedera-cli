package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.TransactionManager;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor
@Getter
@Setter
@Component
public class KryptoKransfer implements Runnable {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private InputReader inputReader;

    @Autowired
    private AccountManager accountManager;

    @Autowired
    private TransactionManager transactionManager;

    @Autowired
    private CryptoTransferOptions cryptoTransferOptions;

    @Autowired
    private CryptoTransferValidation cryptoTransferValidation;

    @Spec
    private CommandSpec spec;

    @ArgGroup(exclusive = false, multiplicity = "1")
    private List<CryptoTransferOptions> cryptoTransferOptionsList;

    private String tinybarListArgs;
    private String hbarListArgs;
    private String senderListArgs;
    private String recipientListArgs;
    private String transferListArgs;
    private boolean skipPreview;
    private boolean isTiny = true;
    private String isInfoCorrect;
    private String memoString = "";

    private List<String> transferList;
    private List<String> amountList;
    private List<String> senderList;
    private List<String> recipientList;
    private AccountId account;
    private long amountInTiny;
    private Client client;
    private TransactionId transactionId;
    private CryptoTransferTransaction cryptoTransferTransaction;


    @Override
    public void run() {

        transactionAmountNotValid();

        System.out.println("will it still pass");

        if (senderList() == null) return;
        senderList = senderList();
        if (recipientList() == null) return;
        recipientList = recipientList();

        if (!cryptoTransferValidation.verifyListHasAccountIdFormat(senderList)) return;
        if (!cryptoTransferValidation.verifyListHasAccountIdFormat(recipientList)) return;

        transferList = appendTransferList(senderList, recipientList);

    }

    public boolean createTransferList(List<String> senderList, List<String> recipientList) {
        boolean hasOperator = false;
        switch (senderList.size()) {
            case 1:
                System.out.println("case 1");
                if (cryptoTransferValidation.senderListHasOperator(senderList)) {
                    System.out.println("Sender list contains operator");
                } else {
                    System.out.println("Sender list does not contain operator");
                }
                break;
            case 2:
                System.out.println("case 2");
                if (cryptoTransferValidation.senderListHasOperator(senderList)) {
                    System.out.println("Sender list contains operator");
                } else {
                    System.out.println("Sender list does not contain operator");
                }
                break;
            default:
                break;
        }
        return hasOperator;
    }

    public List<String> appendTransferList(List<String> senderList, List<String> recipientList) {
        return Stream.of(senderList, recipientList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<String> senderList() {
        senderListArgs = cryptoTransferValidation.senderListArgs();
        if (!StringUtil.isNullOrEmpty(senderListArgs)) {
            senderList = Arrays.asList(senderListArgs.split(","));
        }
        return senderList;
    }

    public List<String> recipientList() {
        recipientListArgs = cryptoTransferValidation.recipientListArgs();
        if (!StringUtil.isNullOrEmpty(recipientListArgs)) {
            recipientList = Arrays.asList(recipientListArgs.split(","));
        }
        return recipientList;
    }

    public void transactionAmountNotValid() {
        tinybarListArgs = cryptoTransferValidation.tinybarListArgs();
        hbarListArgs = cryptoTransferValidation.hbarListArgs();

        if (StringUtil.isNullOrEmpty(hbarListArgs) && StringUtil.isNullOrEmpty(tinybarListArgs)) {
            shellHelper.printError("You have to provide transfer amounts either in hbars or tinybars");
            return;
        }

        if (!StringUtil.isNullOrEmpty(hbarListArgs) && !StringUtil.isNullOrEmpty(tinybarListArgs)) {
            shellHelper.printError("Transfer amounts must either be in hbars or tinybars, not both");
            return;
        }
    }
}
