package com.hedera.cli.hedera.setup;

import java.io.File;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.botany.AdjectivesWordList;
import com.hedera.cli.hedera.botany.BotanyWordList;
import com.hedera.cli.hedera.crypto.AccountRecovery;
import com.hedera.cli.hedera.keygen.KeyPair;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.models.HederaAccount;

import org.hjson.JsonObject;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;

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
    if (strMethod.contains("bip")) {
      keyPair =  ac.recoverEDKeypairPostBipMigration(phraseList);
    } else {
      keyPair = ac.recoverEd25519AccountKeypair(phraseList);
    }
    ac.printKeyPair(keyPair);
    JsonObject account = addAccountToJson(accountId, keyPair);
    saveToJson(accountId, account);
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

  public void saveToJson(String accountId, JsonObject account) {
    // ~/.hedera/[network_name]/accounts/[account_name].json
    DataDirectory dataDirectory = new DataDirectory();
    String fileName = getRandomName();
    String fileNameWithExt = fileName + ".json";
    String networkName = dataDirectory.readFile("network.txt");

    String pathToAccountsFolder = networkName + File.separator + "accounts" + File.separator;
    String pathToAccountFile =  pathToAccountsFolder +  fileNameWithExt;
    String pathToDefaultTxt = pathToAccountsFolder +  "default.txt";

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
      dataDirectory.readFile(pathToDefaultTxt,fileName + ":" + accountId);
      // mark this account as the default
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