package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.account.AccountId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@NoArgsConstructor
@Getter
@Setter
@Component
@Command(name = "balance", description = "@|fg(225) Gets the balance of the requested account|@")
public class AccountBalance implements Runnable, Operation {

    @Autowired
    private Hedera hedera;

    @Autowired
    private AccountManager accountManager;

    @Autowired
    private ShellHelper shellHelper;

    @Parameters(index = "0", description = "Hedera account in the format shardNum.realmNum.accountNum"
            + "%n@|bold,underline Usage:|@%n" + "@|fg(yellow) account balance 0.0.1003|@")
    private String accountIdInString;

    @Override
    public void run() {
        String accountId = accountManager.verifyAccountId(accountIdInString, shellHelper);
        if (accountId == null)
            return;
        getBalance();
    }

    public long getBalance() {
        long balance = 0;
        try {
            Client client = hedera.createHederaClient();
            balance = client.getAccountBalance(AccountId.fromString(accountIdInString));
            shellHelper.printSuccess("Balance: " + balance);
            client.close();
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
        return balance;
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