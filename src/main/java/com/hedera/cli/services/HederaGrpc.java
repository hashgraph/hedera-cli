package com.hedera.cli.services;

import java.io.File;
import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.AddressBookManager;
import com.hedera.cli.models.DataDirectory;
import com.hedera.cli.models.HederaAccount;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.proto.ResponseCodeEnum;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.HederaStatusException;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.account.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.AccountInfo;
import com.hedera.hashgraph.sdk.account.AccountInfoQuery;
import com.hedera.hashgraph.sdk.account.AccountUpdateTransaction;
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

    public AccountId createNewAccount(Ed25519PublicKey publicKey, AccountId operatorId, long initialBalance) {
        AccountId accountId = null;
        try (Client client = hedera.createHederaClient()) {
            TransactionId transactionId = new TransactionId(operatorId);

            TransactionId txId = new AccountCreateTransaction()
                    // The only _required_ property here is `key`
                    .setTransactionId(transactionId).setKey(publicKey).setInitialBalance(initialBalance)
                    .setAutoRenewPeriod(Duration.ofSeconds(7890000)).execute(client);

            // This will wait for the receipt to become available
            TransactionReceipt receipt = txId.getReceipt(client);
            accountId = receipt.getAccountId();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            // do nothing
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
        return accountId;
    }

    public JsonObject printAccount(String accountId, String privateKey, String publicKey) {
        JsonObject account = new JsonObject();
        account.add("accountId", accountId);
        account.add("privateKey", privateKey);
        account.add("publicKey", publicKey);
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object jsonObject = mapper.readValue(account.toString(), HederaAccount.class);
            String accountValue = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
            shellHelper.printSuccess(accountValue);
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
        return account;
    }

    public void executeAccountUpdate(AccountId accountId, Ed25519PrivateKey newKey, Ed25519PrivateKey originalKey) {
        try (Client client = hedera.createHederaClient()) {
            TransactionId transactionId = new AccountUpdateTransaction().setAccountId(accountId)
                    .setKey(newKey.publicKey).build(client)
                    // Sign with the previous key and the new key
                    .sign(originalKey).sign(newKey).execute(client);

            TransactionReceipt receipt = transactionId.getReceipt(client);
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

    private void retrieveAccountInfoForKeyVerification(AccountId accountId, Ed25519PrivateKey newKey, Client client,
            TransactionReceipt receipt) throws HederaStatusException {

        if (receipt.status.code == ResponseCodeEnum.SUCCESS_VALUE) {
            shellHelper.printSuccess("Account updated: " + receipt.status.toString());
            shellHelper.printInfo("Retrieving account info to verify the current key..");
            AccountInfo info = new AccountInfoQuery().setAccountId(accountId).execute(client);
            shellHelper.printInfo("\nPublic key in Encoded form: " + info.key.toString());
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
            shellHelper.printWarning(
                    "AccountId does not exist locally, no file was updated. Use `account recovery` to save to local disk.");
        }
    }

    public boolean updateJsonAccountInDisk(AccountId accountId, Ed25519PrivateKey newKey) {
        boolean fileUpdated = false;
        String pathToIndexTxt = accountManager.pathToIndexTxt();
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
                    String pathToAccountFile = accountManager.pathToAccountsFolder() + valueIndex + ".json";
                    JsonObject account = hedera.getAccountManager()
                            .createAccountJsonWithPrivateKey(accountId.toString(), newKey);
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