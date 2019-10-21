package com.hedera.cli.hedera.crypto;

import picocli.CommandLine.Command;

@Command(name = "default", description = "@|fg(225) Sets the default operator account (i.e. the account that pays for transactions and queries)|@")
public class AccountDefault implements Runnable {

  // @Option(names = {"-a", "--accountId"})
  // private String accountId;

  @Override
  public void run() {

  }

}