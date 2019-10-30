package com.hedera.cli.hedera.crypto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.hedera.utils.AccountManager;
import com.hedera.cli.hedera.utils.Utils;
import com.hedera.cli.models.Recipient;
import com.hedera.cli.models.Sender;
import com.hedera.cli.models.TransactionObj;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Transaction;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionRecord;
import com.hedera.hashgraph.sdk.TransactionRecordQuery;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Spec;

@NoArgsConstructor
@Setter
@Getter
@Component
public class CryptoTransferMultiple implements Runnable {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private Utils utils;

    @Autowired
    private AccountManager accountManager;

    @Autowired
    private CryptoTransferOptions cryptoTransferOptions;

    @Spec
    private CommandSpec spec;

    @ArgGroup(exclusive = false, multiplicity = "1")
    private List<CryptoTransferOptions> cryptoTransferOptionsList;

    private String transferListArgs;
//    private String springSender;
//    private String springRecipient;
    private String tinybarAmtArgs;
    private String hbarAmtArgs;
    private String mPreview = "no";

    private List<String> transferList;
//    private String[] sender;
//    private String[] recipient;
    private String[] transferListArray;
    private String[] transferAmountList;
    private String senderAccountIDInString;
    private String memoString = "";

    private InputReader inputReader;
    private Ed25519PrivateKey senderPrivKey;

    private String isInfoCorrect;
    private long transferAmount;
    private boolean isTiny;

    @Override
    public void run() {
//        try {
//            // size 2
//            for (int i = 0; i < cryptoTransferOptionsList.size(); i++) {
//                // get the exclusive arg by user ie tinybars or hbars
//                cryptoTransferOptions = cryptoTransferOptionsList.get(i);
//            }
//
//            // populate the definitions
//            hbarAmtArgs = cryptoTransferOptions.exclusive.recipientAmtHBars;
//            tinybarAmtArgs = cryptoTransferOptions.exclusive.recipientAmtTinyBars;
//
//            transferListArgs = cryptoTransferOptions.dependent.senderList + "," + cryptoTransferOptions.dependent.recipientList;
//            mPreview = cryptoTransferOptions.dependent.mPreview;
//
//            if (!StringUtil.isNullOrEmpty(tinybarAmtArgs) && StringUtil.isNullOrEmpty(hbarAmtArgs)) {
//                // tinybar arg
//                transferListArray = transferListArgs.split(",");
//                transferAmountList = tinybarAmtArgs.split(",");
//                memoString = accountManager.promptMemoString(inputReader);
////                senderAccountIDInString = promptSenderId(inputReader);
////                String transferAmountInStr = promptTransferAmount(inputReader);
////                transferAmount = cryptoTransferUtils.verifyTransferInTinyBars(transferAmountInStr);
//                // Sender and recipient's total must always be zero
//                isTiny = true;
//                long senderTotal = sumOfTransfer(transferAmountList, isTiny) - transferAmount;
//                if (senderTotal != 0) {
//                    shellHelper.printError("Transaction total amount must add up to a zero sum!");
//                    return;
//                }
//            } else if (StringUtil.isNullOrEmpty(tinybarAmtArgs) && !StringUtil.isNullOrEmpty(hbarAmtArgs)) {
//                // hbar arg
//                transferListArray = transferListArgs.split(",");
//                transferAmountList = hbarAmtArgs.split(",");
//                memoString = accountManager.promptMemoString(inputReader);
////                senderAccountIDInString = promptSenderId(inputReader);
////                String transferAmountInStr = promptTransferAmount(inputReader);
////                transferAmount = cryptoTransferUtils.verifyTransferInHbars(transferAmountInStr);
//                // Sender and recipient's total must always be zero
//                isTiny = false;
//                long senderTotal = sumOfTransfer(transferAmountList, isTiny) - transferAmount;
//                if (senderTotal != 0) {
//                    shellHelper.printError("Transaction total amount must add up to a zero sum!");
//                    return;
//                }
//            } else if (StringUtil.isNullOrEmpty(tinybarAmtArgs) && StringUtil.isNullOrEmpty(hbarAmtArgs)) {
//                shellHelper.printError("You have to provide a transaction amount in hbars or tinybars");
//            } else {
//                shellHelper.printError("Error in commandline");
//            }
//
//            transferList = Arrays.asList(transferListArray);
//            List<String> amountList = Arrays.asList(transferAmountList);
//            // Operator is the current default account user
//            AccountId operatorId = hedera.getOperatorId();
//            Client client = hedera.createHederaClient();
//
//            // Create a multi-sender crypto transfer where sender does not have to pay
//            // transaction fees = network fee + node fee
//            AccountId senderAccountID = AccountId.fromString(senderAccountIDInString);
//
//            // Simple check, can be more comprehensive
//            Map<Integer, Recipient> map = verifiedRecipientMap(transferList, amountList, isTiny);
//            if (map == null) {
//                return;
//            }
//            // Create a crypto transfer transaction
//            CryptoTransferTransaction cryptoTransferTransaction = new CryptoTransferTransaction(client);
//
//            // .addTransfer can be called as many times as you want as long as
//            // the total sum of all transfers adds up to zero
//            cryptoTransferTransaction.addTransfer(senderAccountID, -transferAmount);
//
//            // Dynamic population of recipient List
//            map.forEach((key, value) -> {
//                AccountId account = value.getAccountId();
//                long amount = value.getAmount();
//                cryptoTransferTransaction.addTransfer(account, amount);
//            });
//
//            // Sets the memo that is required in some transactions
//            cryptoTransferTransaction.setMemo(memoString);
//
//            // Save info
////            Sender sender = new Sender();
////            sender.setAccountId(senderAccountID);
////            sender.setAmount(transferAmount);
//            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
////            String jsonStringSender = ow.writeValueAsString(sender);
////            String jsonStringRecipient = ow.writeValueAsString(map);
//            String jsonStringTransferList = ow.writeValueAsString(map);
//
//            // handle preview error gracefully here
//            if (noPreview(mPreview).equals("no")) {
//                // do not show preview
//                executeCryptoTransferMultiple(hedera, client, senderAccountID, operatorId, cryptoTransferTransaction);
//            } else if (noPreview(mPreview).equals("yes")) {
//                // show preview and execute cryptotransfer
////                isInfoCorrect = promptPreview(operatorId, jsonStringSender, jsonStringRecipient);
//                isInfoCorrect = promptPreview(operatorId, jsonStringTransferList);
//                if ("yes".equals(isInfoCorrect)) {
//                    shellHelper.print("Info is correct, let's go!");
//                    executeCryptoTransferMultiple(hedera, client, senderAccountID, operatorId, cryptoTransferTransaction);
//                } else if ("no".equals(isInfoCorrect)) {
//                    shellHelper.print("Nope, incorrect, let's make some changes");
//                } else {
//                    shellHelper.printError("Input must either been yes or no");
//                }
//            } else {
//                shellHelper.printError("Error in commandline");
//            }
//
//        } catch (Exception e) {
//            shellHelper.printError(e.getMessage());
//        }
//    }
//
//    public static String convertArrayToStringMethod(String[] senderArray, String[] recipientArray) {
//        StringBuilder stringBuilder = new StringBuilder();
//        for (int i = 0; i < senderArray.length; i++) {
//            stringBuilder.append(senderArray[i]);
//        }
//        for (int i = 0; i < recipientArray.length; i++) {
//            stringBuilder.append(recipientArray[i]);
//        }
//        System.out.println(stringBuilder);
//        return stringBuilder.toString();
//    }
//
////    public String promptSenderId(InputReader inputReader) {
////        return inputReader.prompt("Input sender accountID in the format 0.0.xxxx");
////    }
//
////    public String promptTransferAmount(InputReader inputReader) {
////        return inputReader.prompt("Input transfer amount");
////    }
//
//    private String promptPreview(AccountId operatorId, String jsonStringTransferList) {
//        return inputReader.prompt("\nOperator\n" + operatorId + "\nTransfer List\n"
//                + jsonStringTransferList + "\n\nIs this correct?" + "\nyes/no");
//    }
//
////    private String promptPreview(AccountId operatorId, String jsonStringSender, String jsonStringRecipient) {
////        return inputReader.prompt("\nOperator\n" + operatorId + "\nSender\n" + jsonStringSender + "\nRecipient\n"
////                + jsonStringRecipient + "\n\nIs this correct?" + "\nyes/no");
////    }
//
//    private void executeCryptoTransferMultiple(Hedera hedera, Client client, AccountId senderAccountID, AccountId operatorId,
//                                               CryptoTransferTransaction cryptoTransferTransaction) {
//
//        try {
//
//            long senderBalanceBefore = client.getAccountBalance(senderAccountID);
//            long operatorBalanceBefore = client.getAccountBalance(operatorId);
//            shellHelper.print(senderAccountID + " sender balance BEFORE = " + senderBalanceBefore);
//            shellHelper.print(operatorId + " operator balance BEFORE = " + operatorBalanceBefore);
//            TransactionReceipt transactionReceipt;
//            TransactionRecord record;
//
//            TransactionId transactionId = new TransactionId(operatorId);
//            cryptoTransferTransaction.setTransactionId(transactionId);
//
//            // if accountId of sender is the same as the operatorId, only sign once
//            if (senderAccountID.toString().equals(hedera.getOperatorId().toString())) {
//                transactionReceipt = Transaction.fromBytes(client, cryptoTransferTransaction.toBytes()).executeForReceipt();
//                if (transactionReceipt.getStatus().toString().equals("SUCCESS")) {
//                    record = new TransactionRecordQuery(client).setTransactionId(transactionId)
//                            .execute();
//                    printBalance(client, operatorId, senderAccountID);
//                    // save all transaction record into ~/.hedera/[network_name]/transaction/[file_name].json
//                    saveTransactionToJson(record);
//                }
//            } else {
//                // Since there is more than 1 sender in this multi-sender transaction example
//                // ie operator and sender are different,
//                // Sender must obviously sign to allow funds to be transferred out
//                String senderPrivKeyInString = inputReader.prompt("Input sender private key", "secret", false);
//                senderPrivKey = Ed25519PrivateKey.fromString(senderPrivKeyInString);
//                var signedTxnBytes = senderSignsTransaction(client, senderPrivKey, cryptoTransferTransaction.toBytes());
//                transactionReceipt = Transaction.fromBytes(client, signedTxnBytes).executeForReceipt();
//                if (transactionReceipt.getStatus().toString().equals("SUCCESS")) {
//                    record = new TransactionRecordQuery(client).setTransactionId(transactionId)
//                            .execute();
//                    printBalance(client, operatorId, senderAccountID);
//                    // save all transaction record into ~/.hedera/[network_name]/transaction/[file_name].json
//                    saveTransactionToJson(record);
//                }
//            }
//        } catch (Exception e) {
//            shellHelper.printError(e.getMessage());
//        }
//    }
//
//    private void saveTransactionToJson(TransactionRecord record) {
//        shellHelper.printSuccess("Success!");
//        shellHelper.printSuccess("Transfer tx fee: " + record.getTransactionFee());
//        shellHelper.printSuccess("Transfer consensus timestamp: " + record.getConsensusTimestamp());
//        shellHelper.printSuccess("Transfer receipt status: " + record.getReceipt().getStatus());
//        shellHelper.printSuccess("Transfer memo: " + record.getMemo());
//
//        String txID = printTransactionId(record.getTransactionId());
//
//        TransactionObj txObj = new TransactionObj();
//        txObj.setTxID(txID);
//        txObj.setTxMemo(record.getMemo());
//        txObj.setTxFee(record.getTransactionFee());
//        txObj.setTxConsensusTimestamp(record.getConsensusTimestamp());
//        txObj.setTxValidStart(record.getTransactionId().getValidStart().getEpochSecond() + "-"
//                + record.getTransactionId().getValidStart().getNano());
//
//        utils.saveTransactionsToJson(txID, txObj);
//    }
//
//    private void printBalance(Client client, AccountId operatorId, AccountId senderAccountID) {
//        try {
//            shellHelper.printInfo("transferring...");
//            long operatorBalanceAfter = client.getAccountBalance(operatorId);
//            long senderBalanceAfter = client.getAccountBalance(senderAccountID);
//            // Get balance is always free, does not require any keys
//            shellHelper.print(senderAccountID + " sender balance AFTER = " + senderBalanceAfter);
//            shellHelper.print(operatorId + " operator balance AFTER = " + operatorBalanceAfter);
//        } catch (Exception e) {
//            shellHelper.printError(e.getMessage());
//        }
//    }
//
//    private String printTransactionId(TransactionId transactionId) {
//        String txTimestamp = transactionId.getValidStart().getEpochSecond() + "-"
//                + transactionId.getValidStart().getNano();
//        String txID = transactionId.getAccountId().toString() + "-" + txTimestamp;
//        shellHelper.printSuccess("TransactionID : " + txID);
//        return txID;
//    }
//
//    private byte[] senderSignsTransaction(Client client, Ed25519PrivateKey senderPrivKey, byte[] transactionData) throws InvalidProtocolBufferException {
//        return Transaction.fromBytes(client, transactionData)
//                .sign(senderPrivKey)
//                .toBytes();
//    }
//
//    /**
//     * verifiedRecipientMap verifies that for every account there is an amount.
//     * It then verifies if
//     * @param transferList List of recipient accounts
//     * @param amountList List of recipient amounts
//     * @param isTiny Return true if arguments passed from CLI is -tb, else false if arguments passed from CLI is -hb
//     * @returns null if error, else returns a Map of recipients
//     */
//    public Map<Integer, Recipient> verifiedRecipientMap(List<String> transferList, List<String> amountList, boolean isTiny) {
//        AccountId accountId;
//        String acc;
//        String amt;
//        long amountInTiny;
//        Map<Integer, Recipient> map = new HashMap<>();
//
//        try {
//            if (transferList.size() != amountList.size())
//                shellHelper.printError("Lists aren't the same size");
//            else {
//                for (int i = 0; i < transferList.size(); ++i) {
//                    acc = transferList.get(i);
//                    amt = amountList.get(i);
//                    if (isTiny && accountManager.isAccountId(acc)) {
//                        amountInTiny = cryptoTransferUtils.verifyTransferInTinyBars(amt);
//                        if (amountInTiny == 0L) {
//                            return null;
//                        }
//                        accountId = AccountId.fromString(acc);
//                        Recipient verifiedRecipient = new Recipient(accountId, amountInTiny);
//                        map.put(i, verifiedRecipient);
//                    } else if (!isTiny && accountManager.isAccountId(acc)) {
//                        amountInTiny = cryptoTransferUtils.verifyTransferInHbars(amt);
//                        if (amountInTiny == 0L) {
//                            return null;
//                        }
//                        accountId = AccountId.fromString(acc);
//                        Recipient verifiedRecipient = new Recipient(accountId, amountInTiny);
//                        map.put(i, verifiedRecipient);
//                    }
//                    else {
//                        shellHelper.printError("Some error occurred");
//                        return null;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            shellHelper.printError(e.getMessage());
//        }
//        return map;
//    }
//
//    /**
//     * sumOfTransfer verifies whether the amount in the recipient amount string array contains tinybars or hbars
//     * verifies accordingly and adds the total sum.
//     * @param recipientAmtStrArray
//     * @param isTiny return true if arguments passed from CLI is -tb, else false if arguments passed from CLI is -hb
//     * @returns 0L if error, else returns total sum of transfer in long
//     */
//    public long sumOfTransfer(String[] transferAmountList, boolean isTiny) {
//        long sum = 0;
//        long amountInTiny;
//        if (isTiny) {
//            // Sum in tiny from tiny
//            for (String amt : transferAmountList) {
//                amountInTiny = cryptoTransferUtils.verifyTransferInTinyBars(amt);
//                if (amountInTiny == 0L) {
//                    shellHelper.printError("Tinybars must be whole numbers");
//                    return 0L;
//                }
//                sum += amountInTiny;
//            }
//        } else {
//            // Sum in tiny from hbar
//            for (String amt : transferAmountList) {
//                amountInTiny = cryptoTransferUtils.verifyTransferInHbars(amt);
//                if (amountInTiny == 0L) {
//                    shellHelper.printError("Hbar must be > 0");
//                    return 0L;
//                }
//                sum += amountInTiny;
//            }
//        }
//        return sum;
//    }
//
//    public boolean isNumeric(final String str) {
//        // checks null or empty
//        if (StringUtil.isNullOrEmpty(str)) {
//            return false;
//        }
//        for (char c : str.toCharArray()) {
//            if (!Character.isDigit(c)) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//
//    private String noPreview(String preview) {
//        if ("no".equals(preview)) {
//            mPreview = preview;
//        } else if ("yes".equals(preview)) {
//            mPreview = preview;
//        } else {
//            throw new CommandLine.ParameterException(spec.commandLine(), "Option -y removes preview");
//        }
//        return mPreview;
    }
}
