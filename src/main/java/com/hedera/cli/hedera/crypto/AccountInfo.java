package com.hedera.cli.hedera.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.AccountInfoQuery;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Setter;
import org.hjson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@NoArgsConstructor
@Setter
@Component
@Command(name = "info",
        description = "@|fg(225) Gets the information of the paying/operator account"
                + " returns a stateproof if requested|@")
public class AccountInfo implements Runnable {

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
        com.hedera.hashgraph.sdk.account.AccountInfo accountRes = getAccountInfo(hedera, accountIDInString);

        JsonObject accountInfo = new JsonObject();
        accountInfo.add("accountId", accountRes.getAccountId().toString());
        accountInfo.add("contractId", accountRes.getContractAccountId());
        accountInfo.add("balance", accountRes.getBalance());
        accountInfo.add("claim", String.valueOf(accountRes.getClaims()));
        accountInfo.add("autoRenewPeriod(millisecond)", accountRes.getAutoRenewPeriod().toMillis() + "millisecond");
        accountInfo.add("autoRenewPeriod(days)", accountRes.getAutoRenewPeriod().toDays() + " days");
        accountInfo.add("expirationTime", String.valueOf(accountRes.getExpirationTime()));
        accountInfo.add("receivedRecordThreshold", accountRes.getGenerateReceiveRecordThreshold());
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            mapper.writeValueAsString(accountInfo);
            shellHelper.printSuccess(String.valueOf(accountInfo));
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    public com.hedera.hashgraph.sdk.account.AccountInfo getAccountInfo(Hedera hedera, String accountIDInString) {
        com.hedera.hashgraph.sdk.account.AccountInfo accountRes = null;
        try {
            var operatorId = hedera.getOperatorId();
            var client = hedera.createHederaClient()
                    .setOperator(operatorId, hedera.getOperatorKey());
            AccountInfoQuery q;
            q = new AccountInfoQuery(client)
                    .setAccountId(AccountId.fromString(accountIDInString));
            accountRes = q.execute();
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
        return accountRes;
    }
}
