package com.hedera.cli.hedera.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.utils.AccountInfoSerializer;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.AccountInfo;
import com.hedera.hashgraph.sdk.account.AccountInfoQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@NoArgsConstructor
@Getter
@Setter
@Component
@Command(name = "info",
        description = "@|fg(225) Gets the information of the paying/operator account"
                + " returns a stateproof if requested|@")
public class AccountGetInfo implements Runnable {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    @Parameters(index = "0", description = "Hedera account in the format shardNum.realmNum.accountNum"
            + "%n@|bold,underline Usage:|@%n"
            + "@|fg(yellow) account info 0.0.1003|@")
    private String accountIDInString;

    @Override
    public void run() {
        if (StringUtil.isNullOrEmpty(accountIDInString)) {
            accountIDInString = hedera.getOperatorId().toString();
        }
        AccountInfo accountInfo = getAccountInfo(hedera, accountIDInString);
        if (accountInfo != null) {

            try {
                ObjectMapper mapper = new ObjectMapper();
                SimpleModule module = new SimpleModule();
                module.addSerializer(AccountInfo.class, new AccountInfoSerializer());
                mapper.registerModule(module);
                ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
                shellHelper.printSuccess(ow.writeValueAsString(accountInfo));
            } catch (Exception e) {
                shellHelper.printError(e.getMessage());
            }
        }
        // do nothing
    }

    public AccountInfo getAccountInfo(Hedera hedera, String accountIDInString) {
        AccountInfo accountInfo = null;
        try {
            var operatorId = hedera.getOperatorId();
            var client = hedera.createHederaClient()
                    .setOperator(operatorId, hedera.getOperatorKey());
            AccountInfoQuery q;
            q = new AccountInfoQuery(client)
                    .setAccountId(AccountId.fromString(accountIDInString));
            accountInfo = q.execute();
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
        return accountInfo;
    }
}
