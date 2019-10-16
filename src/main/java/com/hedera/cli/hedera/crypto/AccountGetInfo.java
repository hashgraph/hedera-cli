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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@NoArgsConstructor
@Getter
@Setter
@Component
@Command(name = "info",
        description = "@|fg(225) Gets the information of the paying/operator account"
                + " returns a stateproof if requested|@")
public class AccountGetInfo implements Runnable {

    @Autowired
    ApplicationContext context;

    @Autowired
    ShellHelper shellHelper;

    @Option(names = {"-a", "--accountId"}, arity = "0..1", description = "Account ID in %nshardNum.realmNum.accountNum format")
    private String accountIDInString;

    @Override
    public void run() {
        Hedera hedera = new Hedera(context);

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
