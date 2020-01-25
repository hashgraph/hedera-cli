package com.hedera.cli.hedera.hcs;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.HederaNetworkException;
import com.hedera.hashgraph.sdk.HederaStatusException;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicCreateTransaction;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Getter
@Setter
@Component
@Command(name = "create")
public class CreateTopic implements Runnable {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private InputReader inputReader;

    @Autowired
    private AccountManager accountManager;

    @Option(names = {"-y", "--yes"}, description = "Yes, use submit key, a submitKey limits who can submit messages on the topic")
    private boolean useSubmitKey;

    private Ed25519PublicKey submitKey;

    // @enerestar this should be an option with name -m / --memo
    private String topicMemoString = "";

    @Override
    public void run() {
        Client client = hedera.createHederaClient();
        topicMemoString = accountManager.promptMemoString(inputReader);

        if (useSubmitKey) {
             String submitKeyString = inputReader.prompt("Enter the submit key (public key)");
            if (StringUtil.isNullOrEmpty(submitKeyString)) {
                shellHelper.printError("Enter a submit key (public key) to limit who can submit messages on the topic");
                return;
            }
            try {
                submitKey = Ed25519PublicKey.fromString(submitKeyString);
            } catch (Exception e) {
                shellHelper.printError("Private key is not in the right ED25519 string format");
                return;
            }
            try {
                final TransactionId transactionId = new ConsensusTopicCreateTransaction()
                        .setMaxTransactionFee(1_000_000_000)
                        .setSubmitKey(submitKey)
                        .setTopicMemo(topicMemoString)
                        .execute(client);

                shellHelper.printSuccess("TransactionId: " + transactionId.toString());
                final ConsensusTopicId topicId = transactionId.getReceipt(client).getConsensusTopicId();
                shellHelper.printSuccess("TopicId: " + topicId.toString());
            } catch (HederaNetworkException | HederaStatusException e) {
                shellHelper.printError(e.getMessage());
            }
        }

        // write our HCS gRPC call here, which can be abstracted into a different
        // class/function
        try {
            final TransactionId transactionId = new ConsensusTopicCreateTransaction()
                    .setMaxTransactionFee(1_000_000_000)
                    .execute(client);

            shellHelper.printSuccess("TransactionId: " + transactionId.toString());
            final ConsensusTopicId topicId = transactionId.getReceipt(client).getConsensusTopicId();
            shellHelper.printSuccess("TopicId: " + topicId.toString());
        } catch (HederaNetworkException | HederaStatusException e) {
            shellHelper.printError(e.getMessage());
        }
    }

    public void handle(String subCommand, String... args) {
        new CommandLine(this).execute(args);
    }

}