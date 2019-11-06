package com.hedera.cli.services;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.DataDirectory;
import com.hedera.cli.models.HederaAccount;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.account.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.account.AccountDeleteTransaction;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;

import org.hjson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HederaGrpc {

  @Autowired
  private Hedera hedera;

  @Autowired
  private ShellHelper shellHelper;

  @Autowired
  private DataDirectory dataDirectory;

  @Autowired
  private AccountManager accountManager;

  public AccountId createNewAccount(Ed25519PublicKey publicKey, AccountId operatorId, long initBal) {
    AccountId accountId = null;
    var client = hedera.createHederaClient();
    TransactionId transactionId = new TransactionId(operatorId);
    var tx = new AccountCreateTransaction(client)
        // The only _required_ property here is `key`
        .setTransactionId(transactionId).setKey(publicKey).setInitialBalance(initBal)
        .setAutoRenewPeriod(Duration.ofSeconds(7890000));

    // This will wait for the receipt to become available
    TransactionReceipt receipt;
    try {
      receipt = tx.executeForReceipt();
      if (receipt != null) {
        accountId = receipt.getAccountId();
      } else {
        shellHelper.printError("Receipt is null");
      }
    } catch (Exception e) {
      shellHelper.printError(e.getMessage());
    }
    return accountId;
  }

  public JsonObject printAccount(String accountId, String privateKey, String publicKey) {
    JsonObject account1 = new JsonObject();
    account1.add("accountId", accountId);
    account1.add("privateKey", privateKey);
    account1.add("publicKey", publicKey);
    try {
      ObjectMapper mapper = new ObjectMapper();
      Object jsonObject = mapper.readValue(account1.toString(), HederaAccount.class);
      String accountValue = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
      shellHelper.printSuccess(accountValue);
    } catch (Exception e) {
      shellHelper.printError(e.getMessage());
    }
    return account1;
  }

  public void executeAccountDelete(AccountId oldAccount, Ed25519PrivateKey oldAccountPrivKey, AccountId newAccount) {
    var client = hedera.createHederaClient();
    TransactionId transactionId = new TransactionId(hedera.getOperatorId());
    try {
      boolean privateKeyDuplicate = checkIfOperatorKeyIsTheSameAsAccountToBeDeleted(hedera, oldAccountPrivKey);
      // account that is to be deleted must sign its own transaction
      if (privateKeyDuplicate) {
        // operator already sign transaction
        TransactionReceipt receipt = new AccountDeleteTransaction(client).setTransactionId(transactionId)
            .setDeleteAccountId(oldAccount).setTransferAccountId(newAccount).executeForReceipt();
        receiptStatus(receipt, hedera, newAccount, oldAccount);
      } else {
        // sign the transaction
        TransactionReceipt receipt = new AccountDeleteTransaction(client).setTransactionId(transactionId)
            .setDeleteAccountId(oldAccount).setTransferAccountId(newAccount).sign(oldAccountPrivKey)
            .executeForReceipt();
        receiptStatus(receipt, hedera, newAccount, oldAccount);
      }

    } catch (Exception e) {
      shellHelper.printError(e.getMessage());
    }
  }

  private boolean checkIfOperatorKeyIsTheSameAsAccountToBeDeleted(Hedera hedera, Ed25519PrivateKey oldAccountPrivKey) {
    return hedera.getOperatorKey().toString().equals(oldAccountPrivKey.toString());
  }

  private void receiptStatus(TransactionReceipt receipt, Hedera hedera, AccountId newAccount, AccountId oldAccount) {
    if (receipt.getStatus().toString().equals("SUCCESS")) {
      shellHelper.printSuccess(receipt.getStatus().toString());
      getBalance(hedera, newAccount);
      boolean fileDeleted = deleteJsonAccountFromDisk(oldAccount);
      shellHelper.printSuccess("File deleted from disk " + fileDeleted);
    } else {
      shellHelper.printError("Some error, account not deleted");
    }
  }

  public void getBalance(Hedera hedera, AccountId newAccount) {
    try {
      // Set a sleep to wait for hedera to come to consensus for the funds of deleted
      // account
      // to be transferred to the new account
      Thread.sleep(4000);
      Client client = hedera.createHederaClient();
      client.setOperator(hedera.getOperatorId(), hedera.getOperatorKey());
      var newAccountBalance = client.getAccountBalance(newAccount);
      shellHelper.printSuccess("Account " + newAccount + " new balance is " + newAccountBalance);
    } catch (Exception e) {
      shellHelper.printError(e.getMessage());
    }
  }

  public boolean deleteJsonAccountFromDisk(AccountId oldAccount) {
    String pathToIndexTxt = accountManager.pathToIndexTxt();
    boolean fileDeleted = false;

    String pathToCurrentJsonAccount;
    Map<String, String> updatedMap;

    Map<String, String> readingIndexAccount = dataDirectory.readIndexToHashmap(pathToIndexTxt);

    Set<Map.Entry<String, String>> setOfEntries = readingIndexAccount.entrySet();
    Iterator<Map.Entry<String, String>> iterator = setOfEntries.iterator();

    while (iterator.hasNext()) {
      Map.Entry<String, String> entry = iterator.next();
      String accountId = entry.getKey(); // key refers to the account id
      String value = entry.getValue(); // value refers to the filename json

      if (accountId.equals(oldAccount.toString())) {
        // delete the associated json file in disk
        pathToCurrentJsonAccount = accountManager.pathToAccountsFolder() + value + ".json";
        Path filePathToJson = Paths.get(dataDirectory.getDataDir().toString(), pathToCurrentJsonAccount);
        File file = new File(filePathToJson.toString());
        fileDeleted = file.delete();
        iterator.remove();
      }
    }
    // write to file
    updatedMap = readingIndexAccount;
    dataDirectory.writeFile(pathToIndexTxt, dataDirectory.formatMapToIndex(updatedMap));
    return fileDeleted;
  }

}