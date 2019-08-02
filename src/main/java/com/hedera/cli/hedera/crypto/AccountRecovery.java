package com.hedera.cli.hedera.crypto;

import com.hedera.cli.config.InputReader;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "recovery",
         description = "@|fg(magenta) Recovers a Hedera account via the 24 recovery words.|@",
         helpCommand = true)
public class AccountRecovery implements Runnable {

  @Option(names = { "-a", "--account-id" }, 
          description = "Account ID in %nshardNum.realmNum.accountNum format")
  private String accountId;

  private InputReader inputReader;

  public AccountRecovery() {}

  public AccountRecovery(InputReader inputReader) {
    this.inputReader = inputReader;
  }

  // @Option(names = { "-p", "--phrase" },
  //         description = "24 words backup recovery phrase")
  // private String phrase;

  @Override
  public void run() {
    System.out.println("Recovering account id " + accountId);

    String phrase = inputReader.prompt("24 words phrase", "secret", false);

    // TOOD: implement recovery function
  }

}