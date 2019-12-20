package com.hedera.cli.services;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.*;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.proto.ResponseCodeEnum;
import com.hedera.hashgraph.sdk.*;
import com.hedera.hashgraph.sdk.account.AccountInfo;
import com.hedera.hashgraph.sdk.account.AccountUpdateTransaction;
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

    @Autowired
    private AddressBookManager addressBookManager;

    public AccountId createNewAccount(Ed25519PublicKey publicKey, AccountId operatorId, long initBal) {
        AccountId accountId = null;
        try (Client client = hedera.createHederaClient()) {
            TransactionId transactionId = new TransactionId(operatorId);
            var tx = new AccountCreateTransaction()
                    // The only _required_ property here is `key`
                    .setTransactionId(transactionId).setKey(publicKey).setInitialBalance(initBal)
                    .setAutoRenewPeriod(Duration.ofSeconds(7890000));

            // This will wait for the receipt to become available
            TransactionReceipt receipt;
            receipt = tx.execute(client)
                    .getReceipt(client);
            accountId = retrieveAccountIdFromReceipt(receipt);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            // do nothing
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
        return accountId;
    }

    private AccountId retrieveAccountIdFromReceipt(TransactionReceipt receipt) {
        AccountId accountId;
        if (ResponseCodeEnum.SUCCESS.equals(receipt.status)) {
            accountId = receipt.getAccountId();
        } else if (receipt.status.toString().contains("INVALID_SIGNATURE")) {
            shellHelper.printError("Seems like your current operator's key does not match");
            accountId = null;
        } else {
            shellHelper.printError(receipt.status.toString());
            accountId = null;
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
        try (Client client = hedera.createHederaClient()) {
            TransactionId transactionId = new TransactionId(hedera.getOperatorId());
            boolean privateKeyDuplicate = checkIfOperatorKeyIsTheSameAsAccountToBeDeleted(hedera, oldAccountPrivKey);
            // account that is to be deleted must sign its own transaction
            if (privateKeyDuplicate) {
                // operator already sign transaction
                TransactionReceipt receipt = new AccountDeleteTransaction().setTransactionId(transactionId)
                        .setDeleteAccountId(oldAccount).setTransferAccountId(newAccount).execute(client)
                        .getReceipt(client);
                receiptStatus(receipt, hedera, newAccount, oldAccount);
            } else {
                // sign the transaction
                String randomNode = addressBookManager.getCurrentNetwork().getRandomNode().getAccount();
                String nodeId = randomNode.split("\\.")[2];
                System.out.println("helllloooo");
                System.out.println(nodeId);
                Transaction transaction = new AccountDeleteTransaction()
                        .setNodeAccountId(new AccountId(Integer.parseInt(nodeId)))
                        .setTransactionId(transactionId)
                        .setDeleteAccountId(oldAccount)
                        .setTransferAccountId(newAccount)
                        .build();
                TransactionReceipt receipt = transaction
                        .sign(oldAccountPrivKey)
                        .execute(client)
                        .getReceipt(client);
                receiptStatus(receipt, hedera, newAccount, oldAccount);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            // do nothing
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    private boolean checkIfOperatorKeyIsTheSameAsAccountToBeDeleted(Hedera hedera, Ed25519PrivateKey oldAccountPrivKey) {
        return hedera.getOperatorKey().toString().equals(oldAccountPrivKey.toString());
    }

    private void receiptStatus(TransactionReceipt receipt, Hedera hedera, AccountId newAccount, AccountId oldAccount) {
        if (receipt.status.equals(ResponseCodeEnum.SUCCESS)) {
            shellHelper.printSuccess(receipt.status.toString());
            getBalance(hedera, newAccount);
            boolean fileDeleted = deleteJsonAccountFromDisk(oldAccount);
            shellHelper.printSuccess("File deleted from disk " + fileDeleted);
        } else if (receipt.status.toString().contains("INVALID_SIGNATURE")) {
            shellHelper.printError("Seems like your current operator's key does not match");
        } else {
            shellHelper.printError(receipt.status.toString());
        }
    }

    public void getBalance(Hedera hedera, AccountId newAccount) {
        try (Client client = hedera.createHederaClient()) {
            // Set a sleep to wait for hedera to come to consensus for the funds of deleted
            // account
            // to be transferred to the new account
            Thread.sleep(4000);
            client.setOperator(hedera.getOperatorId(), hedera.getOperatorKey());
            var newAccountBalance = client.getAccountBalance(newAccount);
            shellHelper.printSuccess("Account " + newAccount + " new balance is " + newAccountBalance);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            // do nothing
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

    public void executeAccountUpdate(AccountId accountId, Ed25519PrivateKey newKey, Ed25519PrivateKey originalKey) {
        try (Client client = hedera.createHederaClient()) {
            TransactionId transactionId = new TransactionId(hedera.getOperatorId());
            Transaction transaction = new AccountUpdateTransaction()
                    .setNodeAccountId(AccountId.fromString(addressBookManager.getCurrentNetwork().getRandomNode().getAccount()))
                    .setAccountForUpdate(accountId)
                    .setTransactionId(transactionId)
                    .setKey(newKey.getPublicKey())
                    .build();

            TransactionReceipt receipt = transaction
                    // Sign with the previous key and the new key
                    .sign(originalKey).sign(newKey)
                    .execute(client)
                    .getReceipt(client);
            // Now we fetch the account information to check if the key was changed
            retrieveAccountInfoForKeyVerification(accountId, newKey, client, receipt);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            // do nothing
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    private void retrieveAccountInfoForKeyVerification(AccountId accountId, Ed25519PrivateKey newKey, Client client, TransactionReceipt receipt)
            throws HederaException {
        if (receipt.status.equals(ResponseCodeEnum.SUCCESS)) {
            shellHelper.printSuccess("Account updated: " + receipt.status.toString());
            shellHelper.printInfo("Retrieving account info to verify the current key..");
            AccountInfo info = client.getAccount(accountId);
            shellHelper.printInfo("\nPublic key in Encoded form: " + info.key);
            shellHelper.printInfo("\nPublic key in HEX: " + info.key.toString().substring(24));
            updateFileAndPrintResults(accountId, newKey);
            return;
        }
        
        if (receipt.status.toString().contains("INVALID_SIGNATURE")) {
            shellHelper.printError("Seems like your current operator's key does not match");
            return;
        }
        
        shellHelper.printError(receipt.status.toString());
    }

    private void updateFileAndPrintResults(AccountId accountId, Ed25519PrivateKey newKey) {
        boolean fileUpdated = updateJsonAccountInDisk(accountId, newKey);
        if (fileUpdated) {
            shellHelper.printSuccess("File updated in disk " + fileUpdated);
        } else {
            shellHelper.printWarning("AccountId does not exist locally, no file was updated. Use `account recovery` to save to local disk.");
        }
    }

    public boolean updateJsonAccountInDisk(AccountId accountId, Ed25519PrivateKey newKey) {
        boolean fileUpdated = false;
        String pathToIndexTxt = accountManager.pathToIndexTxt();
        String pathToAccountFile;
        Map<String, String> readingIndexAccount = dataDirectory.readIndexToHashmap(pathToIndexTxt);

        Set<Map.Entry<String, String>> setOfEntries = readingIndexAccount.entrySet();
        Iterator<Map.Entry<String, String>> iterator = setOfEntries.iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String accountIdIndex = entry.getKey(); // key refers to the account id
            String valueIndex = entry.getValue(); // value refers to the filename json
            try {
                if (accountIdIndex.equals(accountId.toString())) {
                    // update the associated json file in disk
                    pathToAccountFile = accountManager.pathToAccountsFolder() + valueIndex + ".json";
                    JsonObject account = hedera.getAccountManager().createAccountJsonWithPrivateKey(accountId.toString(), newKey);
                    ObjectMapper mapper = new ObjectMapper();
                    Object jsonObject = mapper.readValue(account.toString(), HederaAccount.class);
                    String accountValue = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
                    dataDirectory.writeFile(pathToAccountFile, accountValue);
                    fileUpdated = true;
                }
            } catch (Exception e) {
                shellHelper.printError(e.getMessage());
            }
        }
        return fileUpdated;
    }

    public boolean updateDefaultAccountInDisk(AccountId accountId) {
        String pathToIndexTxt = accountManager.pathToIndexTxt();
        String currentNetwork = addressBookManager.getCurrentNetworkAsString();
        String pathToDefaultAccount = currentNetwork + File.separator + "accounts" + File.separator
                + AddressBookManager.ACCOUNT_DEFAULT_FILE;

        // Check if the account chosen as default exists in index.txt
        Map<String, String> readingIndexAccount = dataDirectory.readIndexToHashmap(pathToIndexTxt);

        Set<Map.Entry<String, String>> setOfEntries = readingIndexAccount.entrySet();
        Iterator<Map.Entry<String, String>> iterator = setOfEntries.iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String accountIdIndex = entry.getKey(); // key refers to the account id
            String valueIndex = entry.getValue(); // value refers to the filename json
            // If account chosen exists, update default.txt with new operator's filename
            if (AccountId.fromString(accountIdIndex).equals(accountId)) {
                dataDirectory.writeFile(pathToDefaultAccount, valueIndex + ":" + accountId);
                return true;
            }
        }
        return false;
    }
}