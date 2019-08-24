package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
import com.hedera.hashgraph.sdk.account.AccountInfoQuery;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import java.util.Arrays;

@Command(name = "info",
        description = "@|fg(225) Gets the information of the paying/operator account"
        + "modification returns a stateproof if requested|@")
public class AccountInfo implements Runnable {

    @Option(names = {"-o", "--operator"}, description = "Get current paying/operator account")
    private String operator;

    @Override
    public void run() {
        try {
            Hedera hedera = new Hedera();
            var operatorId = hedera.getOperatorId();
            var client = hedera.createHederaClient();
            AccountInfoQuery q = null;
            q = new AccountInfoQuery(client)
                    .setAccountId(operatorId);
            var accountRes = q.execute();
            String[] accountInfo =
                    {
                            "accountId: " + accountRes.getAccountId() +
                                    "\n contractId: " + accountRes.getContractAccountId() +
                                    "\n balance: " + accountRes.getBalance() +
                                    "\n claim: " + accountRes.getClaims() +
                                    "\n autoRenewPeriod: " + accountRes.getAutoRenewPeriod() +
                                    "\n expirationTime: " + accountRes.getExpirationTime() +
                                    "\n receivedRecordThreshold: " +accountRes.getGenerateReceiveRecordThreshold() +
                                    "\n proxyAccountId: " +accountRes.getProxyAccountId()
                    };
            System.out.println(Arrays.asList(accountInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
