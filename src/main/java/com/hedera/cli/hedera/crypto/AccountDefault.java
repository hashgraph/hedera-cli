package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.services.HederaGrpc;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Command;

@Getter
@Setter
@Component
@Command(name = "default",
        description = "@|fg(225) Sets a new default operator account.|@")
public class AccountDefault implements Runnable, Operation {

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private InputReader inputReader;

    @Autowired
    private HederaGrpc hederaGrpc;

    @Parameters(index = "0", description = "Hedera account in the format shardNum.realmNum.accountNum"
            + "%n@|bold,underline Usage:|@%n"
            + "@|fg(yellow) account info 0.0.1003|@")
    private String accountIDInString;

    @Override
    public void run() {
        AccountId accountId;
        try {
            accountId = AccountId.fromString(accountIDInString);
        } catch (Exception e) {
            shellHelper.printError("Invalid account id provided");
            return;
        }
        boolean defaultFileUpdated = hederaGrpc.updateDefaultAccountInDisk(accountId);
        if (defaultFileUpdated) {
            shellHelper.printSuccess("Default operator updated " + defaultFileUpdated);
        } else {
            shellHelper.printError("Account chosen does not exist in index. Please use `account recovery` first.");
        }
    }

    @Override
    public void executeSubCommand(InputReader inputReader, String... args) {
        if (args.length == 0) {
            CommandLine.usage(this, System.out);
        } else {
            try {
                new CommandLine(this).execute(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}