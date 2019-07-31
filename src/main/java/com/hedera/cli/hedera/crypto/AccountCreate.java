
package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.Hedera;
import com.hedera.hashgraph.sdk.HederaException;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.account.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "create",
        description = "@|fg(magenta) Creates a new Hedera account and returns an accountID in the form of "
                + "%nshardNum.realmNum.accountNum.|@",
        helpCommand = true)
public class AccountCreate implements Runnable {

  @Option(names = { "-r", "--record"},
          description = "Generates a record that lasts 25hrs")
  private boolean generateRecord;

  @Option(names = {"-b", "--balance"},
          description = "Initial balance of new account created " +
          "%n@|bold,underline Usage:|@%n" +
          "@|fg(yellow) account create -b=100 OR%n" +
          "account create --balance=100|@")
  private int initBal;

  // KIV multisig

  @Override
  public void run() {

    System.out.println("AccountCreate subcommand");
    System.out.println(this.generateRecord);
    System.out.println(this.initBal);

      // Generate a Ed25519 private, public key pair
      var newKey = Ed25519PrivateKey.generate();
      var newPublicKey = newKey.getPublicKey();

      System.out.println("private key = " + newKey);
      System.out.println("public key = " + newPublicKey);

      var client = Hedera.createHederaClient();

      var tx = new AccountCreateTransaction(client)
              // The only _required_ property here is `key`
              .setKey(newKey.getPublicKey())
              .setInitialBalance(this.initBal);

      // This will wait for the receipt to become available
      TransactionReceipt receipt = null;
      try {
          receipt = tx.executeForReceipt();
      } catch (HederaException e) {
          e.printStackTrace();
      }
      assert receipt != null;
      var newAccountId = receipt.getAccountId();
      System.out.println("account = " + newAccountId);
  }
}