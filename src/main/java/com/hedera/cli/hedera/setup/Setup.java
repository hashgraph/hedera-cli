package com.hedera.cli.hedera.setup;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.botany.AdjectivesWordList;
import com.hedera.cli.hedera.botany.BotanyWordList;
import com.hedera.cli.hedera.crypto.AccountRecovery;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.models.HederaAccount;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import org.hjson.JsonObject;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

@Component
@Command(name = "setup", description = "")
public class Setup implements Runnable {

  @Spec
  CommandSpec spec;

  @Option(names = {"-m", "--method"}, description = "Input -m=hedera if passphrases have not been migrated on wallet "
          + "%nor account creations are before 13 September 2019. Input -m=bip if passphrases have been migrated on the wallet,"
          + "%nor account creations are after 13 September 2019")
  private String strMethod = "bip";

  // default index 0 is compatible with Hedera wallet apps
  private int index = 0;

  @Override
  public void run() {
    CommandLine.usage(this, System.out);
  }

  public void handle(InputReader inputReader) {
    System.out.println("Start the setup process");
    strMethod = inputReader.prompt("Have you migrated your account on Hedera wallet? If migrated, enter bip, else enter hgc");
    String accountId = inputReader.prompt("account ID in the format of 0.0.xxxx that will be used as default operator");
    String phrase = inputReader.prompt("24 words phrase", "secret", false);
    List<String> phraseList = Arrays.asList(phrase.split(" "));
    System.out.println(phraseList);
    // recover key from phrase
    KeyPair keyPair;
    AccountRecovery ac = new AccountRecovery();
    if (strMethod.equals("bip")) {
      keyPair =  ac.recoverEDKeypairPostBipMigration(phraseList);
      ac.printKeyPair(keyPair);
      JsonObject account = addAccountToJson(accountId, keyPair);
      saveToJson(accountId, account);
    } else if (strMethod.equals("hgc")){
      keyPair = ac.recoverEd25519AccountKeypair(phraseList);
      ac.printKeyPair(keyPair);
      JsonObject account = addAccountToJson(accountId, keyPair);
      saveToJson(accountId, account);
    } else {
      throw new ParameterException(spec.commandLine(), "Method must either been bip or hgc");
    }
  }

  public JsonObject addAccountToJson(String accountId, KeyPair keyPair ) {
    JsonObject account = new JsonObject();
    account.add("accountId", accountId);
    account.add("privateKey", keyPair.getPrivateKeyHex());
    account.add("publicKey", keyPair.getPublicKeyHex());
//    account.add("privateKey_ASN1", keyPair.getPrivateKeyEncodedHex());
//    account.add("publicKey_ASN1", keyPair.getPublicKeyEncodedHex());
    return account;
  }

  public JsonObject addAccountToJsonWithPrivateKey(String accountId, Ed25519PrivateKey privateKey) {
    JsonObject account = new JsonObject();
    System.out.println("private key " + privateKey);
    System.out.println("public key " + privateKey.getPublicKey().toString());
    account.add("accountId", accountId);
    account.add("privateKey", privateKey.toString());
    account.add("publicKey", privateKey.getPublicKey().toString());
    return account;
  }

  public void saveToJson(String accountId, JsonObject account) {
    // ~/.hedera/[network_name]/accounts/[account_name].json
    DataDirectory dataDirectory = new DataDirectory();
    String fileName = getRandomName();
    String fileNameWithExt = fileName + ".json";
    String networkName = dataDirectory.readFile("network.txt");

    String pathToAccountsFolder = networkName + File.separator + "accounts" + File.separator;
    String pathToAccountFile =  pathToAccountsFolder +  fileNameWithExt;

    String pathToDefaultTxt = pathToAccountsFolder +  "default.txt";
    String pathToCurrentTxt = pathToAccountsFolder + "current.txt";
    String pathToIndexTxt = pathToAccountsFolder + "index.txt";

    HashMap<String, String> mHashMap = new HashMap<>();
    mHashMap.put(accountId, fileName);
    ObjectMapper mapper = new ObjectMapper();

    try {
      // create the account json and write it to disk
      Object jsonObject = mapper.readValue(account.toString(), HederaAccount.class);
      String accountValue = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
      System.out.println(accountValue);
      dataDirectory.writeFile(pathToAccountFile, accountValue);
      // default account
      dataDirectory.readFile(pathToDefaultTxt,fileName + ":" + accountId);
      // current account
      dataDirectory.readFile(pathToCurrentTxt, fileName + ":" + accountId);
      // write to index if account does not yet exist in index
      dataDirectory.readWriteFileHashmap(pathToIndexTxt, mHashMap);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getRandomName() {
    Random rand = new Random();
    List<String> botanyNames = BotanyWordList.words;
    List<String> adjectives = AdjectivesWordList.words;
    String randomBotanyName = botanyNames.get(rand.nextInt(botanyNames.size()));
    String randomAdjectives = adjectives.get(rand.nextInt(adjectives.size()));
    int randomNumber = rand.nextInt(10000);
    return randomAdjectives + "_" + randomBotanyName + "_" + randomNumber;
  }
}