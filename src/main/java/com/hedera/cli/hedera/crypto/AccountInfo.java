package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
import com.hedera.hashgraph.sdk.account.AccountInfoQuery;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import java.util.Arrays;

@Command(name = "info",
        description = "@|fg(magenta) Gets the information of a specific account. Requires key for account "
        + "modification returns a stateproof if requested|@")
public class AccountInfo implements Runnable {

    @Option(names = {"-k, --key"} , description = "The key associated with the account which must sign for any modification"
            + "%n@|bold,underline Usage:|@%n"
            + "@|fg(yellow) account info -k=abcd123|@")
    private Ed25519PrivateKey privateKey;

    @Option(names = {"-rq", "--request"}, description = "Type of request: cost, state proof, both, or neither")
    private String request;

    @Override
    public void run() {
        try {
            System.out.println("AccountInfo commands");
//            System.out.println(request);
//            System.out.println(privateKey);
            var operatorId = Hedera.getOperatorId();
            var client = Hedera.createHederaClient();
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
