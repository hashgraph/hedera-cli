package com.hedera.cli.hedera.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.AccountInfoQuery;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.hjson.JsonObject;
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

    private Ed25519PrivateKey accPrivKey;
    private InputReader inputReader;

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
        accountInfo.add("autoRenewPeriod", accountRes.getAutoRenewPeriod().toMillis() + "millisecond");
        accountInfo.add("autoRenewPeriod1", accountRes.getAutoRenewPeriod().toDays() + " days");
        accountInfo.add("expirationTime", String.valueOf(accountRes.getExpirationTime()));
        accountInfo.add("receivedRecordThreshold", accountRes.getGenerateReceiveRecordThreshold());
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            mapper.writeValueAsString(accountInfo);
            System.out.println(accountInfo);
        } catch (Exception e) {
            e.getMessage();
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
            e.printStackTrace();
        }
        return accountRes;
    }
}
