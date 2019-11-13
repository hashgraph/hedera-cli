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

//    @Autowired
//    private InputReader inputReader;
//
//    @Autowired
//    private AccountManager accountManager;
//
//    @Autowired
//    private TransactionManager transactionManager;

    @Autowired
    private CryptoTransferOptions cryptoTransferOptions;

    @Autowired
    private CryptoTransferValidateAccounts cryptoTransferValidateAccounts;

    @Autowired
    private CryptoTransferValidateAmount cryptoTransferValidateAmount;

//    @Spec
//    private CommandSpec spec;
//
//    @ArgGroup(exclusive = false, multiplicity = "1")
//    private List<CryptoTransferOptions> cryptoTransferOptionsList;

//    private boolean skipPreview;
//    private boolean isTiny = true;
//    private String isInfoCorrect;
//    private String memoString = "";

    private List<String> transferList;
    private List<String> amountList;
    private List<String> senderList;
    private List<String> recipientList;

//    private AccountId account;
//    private long amountInTiny;
//    private Client client;
//    private TransactionId transactionId;
//    private CryptoTransferTransaction cryptoTransferTransaction;


    @Override
    public void run() {

        cryptoTransferValidateAmount.transactionAmountNotValid();

        if (cryptoTransferValidateAccounts.senderList() == null) return;
        senderList = cryptoTransferValidateAccounts.senderList();
        if (cryptoTransferValidateAccounts.recipientList() == null) return;
        recipientList = cryptoTransferValidateAccounts.recipientList();

        if (!cryptoTransferValidateAccounts.verifyListHasAccountIdFormat(senderList)) return;
        if (!cryptoTransferValidateAccounts.verifyListHasAccountIdFormat(recipientList)) return;

        transferList = cryptoTransferValidateAccounts.createTransferList(senderList, recipientList);

    }

    public boolean createAmountList(List<String> senderList, List<String> recipientList) {
        boolean hasOperator = false;
        switch (senderList.size()) {
            case 1:
                System.out.println("case 1");
                if (cryptoTransferValidateAccounts.senderListHasOperator(senderList)) {
                    System.out.println("Sender list contains operator");
                } else {
                    System.out.println("Sender list does not contain operator");
                }
                break;
            case 2:
                System.out.println("case 2");
                if (cryptoTransferValidateAccounts.senderListHasOperator(senderList)) {
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
}
