package com.hedera.cli.services;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.DataDirectory;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.account.AccountDeleteTransaction;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Hapi {

  @Autowired
  private Hedera hedera;

  @Autowired
  private ShellHelper shellHelper;

  @Autowired
  private DataDirectory dataDirectory;

  @Autowired
  private AccountManager accountManager;

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