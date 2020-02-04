package com.hedera.cli.commands;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.consensus.CreateTopic;
import com.hedera.cli.shell.ShellHelper;

import com.hedera.hashgraph.proto.ConsensusCreateTopic;
import com.hedera.hashgraph.proto.ConsensusSubmitMessage;
import com.hedera.hashgraph.proto.ResponseCodeEnum;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.consensus.ConsensusMessageSubmitTransaction;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicCreateTransaction;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.PrivateKey;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;
import com.hedera.hashgraph.sdk.mirror.MirrorClient;
import com.hedera.hashgraph.sdk.mirror.MirrorConsensusTopicQuery;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class HederaConsensus extends CommandBase {

  private final ShellHelper shellHelper;
  private final CreateTopic createTopic;
  private final Hedera hedera;

  @ShellMethod(value = "Create new topic")
  public void createTopic(
          @ShellOption(
                  value = { "-m", "--memo" },
                  help = "Memo for the new topic",
                  defaultValue = ShellOption.NULL) String memo,
          @ShellOption(
                  value = { "-ak", "--admin-key" },
                  help = "Hex-encoded private key which will have admin rights to the topic. If unspecified, no admin key is set.",
                  defaultValue = ShellOption.NULL) Ed25519PrivateKey adminKey,
          @ShellOption(
                  value = { "-sk", "--submit-key" },
                  help = "Hex-encoded public key which will be allowed to submit messages to the topic. " +
                          "If unspecified, no submit is specified and anyone can submit messages to the topic",
                  defaultValue =  ShellOption.NULL) Ed25519PublicKey submitKey
  ) {
      ConsensusTopicCreateTransaction createTopicTransaction = new ConsensusTopicCreateTransaction();
      List<PrivateKey> signingKeys = new ArrayList<>();
      if (adminKey != null) {
        createTopicTransaction.setAdminKey(adminKey.publicKey);
        signingKeys.add(adminKey);
      }
      if (submitKey != null) {
        createTopicTransaction.setSubmitKey(submitKey);
      }
      if (memo != null) {
          createTopicTransaction.setTopicMemo(memo);
      }
      TransactionReceipt receipt = hedera.executeTransaction(createTopicTransaction, signingKeys);
      if (receipt != null && receipt.status == Status.Success) {
          shellHelper.printSuccess("New topic created : " + receipt.getConsensusTopicId());
      }
  }

  @ShellMethod(value = "Submit message to a topic")
  public void submitMessage(
          @ShellOption(
                  value = { "-t", "--topic"},
                  help = "Topic id to which message will be submitted") ConsensusTopicId topic,
          @ShellOption(
                  value = { "-sk", "--submit-key"},
                  help = "If topic is protected by a submit key, then the hex-encoded private key corresponding to that submit key.",
                  defaultValue = ShellOption.NULL) Ed25519PrivateKey submitKey,
          @ShellOption(
                  help = "Message to be submitted to the topic") String message
  ) {
      ConsensusMessageSubmitTransaction submitMessageTransaction = new ConsensusMessageSubmitTransaction()
              .setTopicId(topic)
              .setMessage(message);
      List<PrivateKey> signingKeys = new ArrayList<>();
      if (submitKey != null) {
          signingKeys.add(submitKey);
      }
      TransactionReceipt receipt = hedera.executeTransaction(submitMessageTransaction, signingKeys);
      if (receipt != null && receipt.status == Status.Success) {
          shellHelper.printSuccess("Message submitted\nNext sequence number : " + receipt.getConsensusTopicSequenceNumber()
                + " Running hash: " + HexUtils.toHexString(receipt.getConsensusTopicRunningHash()));
      }
  }

  @ShellMethod(value = "Subscribe to a topic")
  public void subscribeTopic(
          @ShellOption(
                  value = {"-t", "--topic"},
                  help = "Topic id to subscribe") ConsensusTopicId topic,
          @ShellOption(
                  value = {"--start-time"},
                  help = "Include messages which reached consensus on or after this time (in epoch seconds)",
                  defaultValue = "now") long consensusStartTime,
          @ShellOption(
                  value = {"--end-time"},
                  help = "Include messages which reached consensus before this time (in epoch seconds)",
                  defaultValue = "not set, receive indefinitely") long consensusEndTime
          // No param for limit
  ) {
    shellHelper.printInfo("Topic subscribed");
  }

  // TODO: add support to alternatively specify known account id in place of private/public keys
  // TODO: add delete, update and topicInfo.
  // TODO: add autoRenewAccount and autoRenewDuration params when that feature is complete.
}
