package com.hedera.cli.hedera.crypto;

import picocli.CommandLine.Option;

public class AccountRecovery implements Runnable {

  @Option(names = { "-a", "--account-id" }, description = "")
  private String accountId;

  @Override
  public void run() {
    System.out.println("Recovering account id " + accountId);
  }

}