package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.shell.ShellHelper;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.List;

@Getter
@Setter
@Component
public class CryptoTransferValidateAmount {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private CryptoTransferOptions cryptoTransferOptions;

    @Autowired
    private CryptoTransferValidateAccounts cryptoTransferValidateAccounts;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1")
    private List<CryptoTransferOptions> cryptoTransferOptionsList;

    private String tinybarListArgs;
    private String hbarListArgs;

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

    public void transactionAmountNotValid() {
        tinybarListArgs = tinybarListArgs();
        hbarListArgs = hbarListArgs();

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
