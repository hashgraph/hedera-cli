package com.hedera.cli.hedera.crypto;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.HederaAccount;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.AccountInfoQuery;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

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

    private Ed25519PrivateKey accPrivKey;
    private InputReader inputReader;

    @Option(names = {"-a", "--accountId"}, description = "Account ID in %nshardNum.realmNum.accountNum format")
    private String accountIDInString;

    @Override
    public void run() {
        String accPrivKeyInString = inputReader.prompt("Input account's private key", "secret", false);
        accPrivKey = Ed25519PrivateKey.fromString(accPrivKeyInString);
        Hedera hedera = new Hedera(context);
        com.hedera.hashgraph.sdk.account.AccountInfo accountRes = getAccountInfo(hedera, accountIDInString, accPrivKey);

        JsonObject accountInfo = new JsonObject();
        accountInfo.add("accountId", accountRes.getAccountId().toString());
        accountInfo.add("contractId", accountRes.getContractAccountId());
        accountInfo.add("balance", accountRes.getBalance());
        accountInfo.add("claim", String.valueOf(accountRes.getClaims()));
        accountInfo.add("autoRenewPeriod", accountRes.getAutoRenewPeriod().toMillis());
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

    public com.hedera.hashgraph.sdk.account.AccountInfo getAccountInfo(Hedera hedera, String accountIDInString, Ed25519PrivateKey accPrivKey) {
        com.hedera.hashgraph.sdk.account.AccountInfo accountRes = null;
        try {
            var accountId = AccountId.fromString(accountIDInString);
            var client = hedera.createHederaClient()
                    .setOperator(accountId, accPrivKey);
            AccountInfoQuery q;
            q = new AccountInfoQuery(client)
                    .setAccountId(accountId);
            accountRes = q.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountRes;
    }
}
