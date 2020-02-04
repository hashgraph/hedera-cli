package com.hedera.cli.commands;

import com.hedera.cli.shell.ShellHelper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@RequiredArgsConstructor
public class HederaConsensus extends CommandBase {

  private final ShellHelper shellHelper;

  @ShellMethod(value = "Create new topic")
  public void createTopic(
          @ShellOption(
                  value = { "-m", "--memo" },
                  help = "Memo for the new topic",
                  defaultValue = "") String memo,
          @ShellOption(
                  value = { "-ak", "--admin-key" },
                  help = "Hex-encoded public key which will have admin rights to the topic",
                  defaultValue = "no admin key") String adminKey,
          @ShellOption(
                  value = { "-sk", "--submit-key" },
                  help = "Hex-encoded public key which will be allowed to submit messages to the topic. " +
                          "If none is specified, anyone can submit messages to the topic",
                  defaultValue = "no submit key") String submitKey
  ) {
    shellHelper.printInfo("Topic Created");
  }

  @ShellMethod(value = "Submit message to a topic")
  public void submitMessage(
          @ShellOption(
                  value = { "-t", "--topic"},
                  help = "Topic id to which message will be submitted") String topic,
          @ShellOption(
                  value = { "-sk", "--submit-key"},
                  help = "If topic is protected by a submit key, then the hex-encoded private key corresponding to that submit key",
                  defaultValue = "no submit key") String submitKey
  ) {
    shellHelper.printInfo("Submitted message");
  }

  @ShellMethod(value = "Subscribe to a topic")
  public void subscribeTopic(
          @ShellOption(
                  value = {"-t", "--topic"},
                  help = "Topic id to subscribe") String topic,
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
