package com.hedera.cli.hedera.crypto;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "recovery",
         description = "@|fg(magenta) Recovers a Hedera account via the 24 recovery words.|@",
         helpCommand = true)
public class AccountRecovery implements Runnable {

  @Option(names = { "-a", "--account-id" }, 
          description = "Account ID in %nshardNum.realmNum.accountNum format")
  private String accountId;

  @Option(names = { "-p", "--phrase"},
          description = "24 words backup recovery phrase")
  private String phrase;

  @Override
  public void run() {
    System.out.println("Recovering account id " + accountId);
    System.out.println("Recovery phrase " + phrase);
  }

}