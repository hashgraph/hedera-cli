package com.hedera.cli.hedera.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.AccountInfoModel;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.AccountInfoQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

        AccountInfoModel accountInfo = new AccountInfoModel();
        accountInfo.setAccountId(accountRes.getAccountId());
        accountInfo.setContractId(accountRes.getContractAccountId());
        accountInfo.setBalance(accountRes.getBalance());
        accountInfo.setClaim(accountRes.getClaims());
        accountInfo.setAutoRenewPeriod(accountRes.getAutoRenewPeriod());
        accountInfo.setExpirationTime(accountRes.getExpirationTime());
        accountInfo.setReceivedRecordThreshold(accountRes.getGenerateReceiveRecordThreshold());
        // accountInfo.setKey(accountRes.getKey());

        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            shellHelper.printSuccess(ow.writeValueAsString(accountInfo));
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
