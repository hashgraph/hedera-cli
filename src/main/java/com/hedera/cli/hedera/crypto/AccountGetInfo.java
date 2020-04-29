package com.hedera.cli.hedera.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.AccountInfo;
import com.hedera.hashgraph.sdk.account.AccountInfoQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.TimeoutException;

@NoArgsConstructor
@Getter
@Setter
@Component
@Command(name = "info",
        description = "@|fg(225) Gets the information of the paying/operator account"
                + " returns a stateproof if requested|@")
public class AccountGetInfo implements Runnable, Operation {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    // @Autowired
    // private AccountInfoSerializer accountInfoSerializer;

    @Parameters(index = "0", description = "Hedera account in the format shardNum.realmNum.accountNum"
            + "%n@|bold,underline Usage:|@%n"
            + "@|fg(yellow) account info 0.0.1003|@")
    private String accountIDInString;

    @Override
    public void run() {
        if (StringUtil.isNullOrEmpty(accountIDInString)) {
            accountIDInString = hedera.getOperatorId().toString();
        }
        getAccountInfo(accountIDInString);
    }

    public void printAccountInfo(AccountInfo accountInfo) {
        if (accountInfo != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                // SimpleModule module = new SimpleModule();
                // module.addSerializer(AccountInfo.class, accountInfoSerializer);
                // mapper.registerModule(module);
                ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
                // TODO: print public key here rather than just "ED25519".
                shellHelper.printSuccess(ow.writeValueAsString(accountInfo));
            } catch (Exception e) {
                shellHelper.printError(e.getMessage());
            }
        }
    }

    public void getAccountInfo(String accountIDInString) {
        try (Client client = hedera.createHederaClient()) {
            final AccountInfo accountInfo = new AccountInfoQuery()
                .setAccountId(AccountId.fromString(accountIDInString))
                .setQueryPayment(25)
                .execute(client);
            printAccountInfo(accountInfo);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            // do nothing
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
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
