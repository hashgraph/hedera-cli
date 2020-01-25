package com.hedera.cli.hedera.hcs;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.HederaNetworkException;
import com.hedera.hashgraph.sdk.HederaStatusException;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicCreateTransaction;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Getter
@Setter
@Component
@Command(name = "create")
public class CreateTopic implements Runnable {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    @Override
    public void run() {
        System.out.println("running hcs create");
        Client client = hedera.createHederaClient();
        // write our HCS gRPC call here, which can be abstracted into a different
        // class/function
        try {
            final TransactionId transactionId = new ConsensusTopicCreateTransaction()
                    .setMaxTransactionFee(1_000_000_000)
                    .execute(client);

            shellHelper.printSuccess("Hello it came here");
            shellHelper.printSuccess(transactionId.toString());
            shellHelper.printSuccess("Hello it came here too");
            final ConsensusTopicId topicId = transactionId.getReceipt(client).getConsensusTopicId();
            shellHelper.printSuccess(topicId.toString());
        } catch (HederaNetworkException | HederaStatusException e) {
            shellHelper.printError(e.getMessage());
        }
    }

    public void handle(String subCommand, String... args) {
        new CommandLine(this).execute(args);
    }

}