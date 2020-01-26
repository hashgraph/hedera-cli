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

import java.util.Arrays;

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

    @Option(names = {"-m", "--memo"}, description = "Topic memo")
    private String topicMemoString = "";

    @Option(names = {"-k", "--submitKey"}, description = "a submit key (public key) to limits who can submit messages on the topic")
    private String submitKeyString;

    @Option(names = {"-y", "--yes"}, arity = "0..*", description = "Generate a submit key")
    private boolean generateSubmitKey;

    private Ed25519PublicKey submitKey;

    private ConsensusTopicCreateTransaction consensusTopicCreateTransaction;

    @Override
    public void run() {
//        System.out.println("topic memo" + topicMemo);
//        System.out.println("submit key string" + submitKeyString);
        System.out.println("generate submit key boolean" + generateSubmitKey);
        Client client = hedera.createHederaClient();

        System.out.println("topic memo " + topicMemoString);
        System.out.println("topic memo " + submitKeyString);
        // pseudocode
        if (!topicMemoString.isEmpty()) {
            consensusTopicCreateTransaction.setTopicMemo(topicMemoString);
        }
        if (!generateSubmitKey && verifySubmitKey(submitKeyString)) {
            submitKey = Ed25519PublicKey.fromString(submitKeyString);
            // right now assume a public key given and code from there
            consensusTopicCreateTransaction.setSubmitKey(submitKey);
        }
        if (generateSubmitKey && verifySubmitKey(submitKeyString)) {
            // Ed25519PublicKey newPublicKey = generatePublicKey();
            // consensusTopicCreateTransaction.setSubmitKey(newPublicKey);
        }
        // write our HCS gRPC call here, which can be abstracted into a different
        // class/function
        try {
            final TransactionId transactionId = consensusTopicCreateTransaction
                    .setMaxTransactionFee(1_000_000_000)
                    .execute(client);

            shellHelper.printSuccess("TransactionId: " + transactionId.toString());
            final ConsensusTopicId topicId = transactionId.getReceipt(client).getConsensusTopicId();
            shellHelper.printSuccess("TopicId: " + topicId.toString());
        } catch (HederaNetworkException | HederaStatusException e) {
            shellHelper.printError(e.getMessage());
        }
    }

    public void handle(String... args) {
        System.out.println("args here in create topic " + Arrays.asList(args));
        System.out.println(this);
        new CommandLine(this).execute(args);
    }

    private boolean verifySubmitKey(String submitKeyString) {
        if (StringUtil.isNullOrEmpty(submitKeyString)) {
            shellHelper.printError("Enter a submit key (public key) to limit who can submit messages on the topic");
            return false;
        }
        try {
            Ed25519PublicKey.fromString(submitKeyString);
        } catch (Exception e) {
            shellHelper.printError("Private key is not in the right ED25519 string format");
            return false;
        }
        return true;
    }

    private String generatePublicKey() {
        // KeyGeneration keyGeneration = new KeyGeneration(strMethod);
        // HGCSeed hgcSeed = new HGCSeed((CryptoUtils.getSecureRandomData(32)));
        // List<String> mnemonic = keyGeneration.generateMnemonic(hgcSeed);
        // KeyPair keypair = keyGeneration.generateKeysAndWords(hgcSeed, mnemonic);
        // Ed25519PublicKey newPublicKey = Ed25519PublicKey.fromString(keypair.getPublicKeyEncodedHex());
        
        // TODO
        // We should save the newly generated keys somewhere but 
        // 1) can a hcs submit key be associate with a keypair that does not have an Hedera Account?
        // 2) we will need to save the keys in a [network][hcs][submitKeys] folder
        // 3) while topics created to be save in [network][hcs][topics? id generated] folder
        return "newPublicKey";
    }
}