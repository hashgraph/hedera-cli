package com.hedera.cli.hedera.setup;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.stream.JsonWriter;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.bip39.Mnemonic;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicChecksumException;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicLengthException;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicWordException;
import com.hedera.cli.hedera.botany.BotanyWordList;
import com.hedera.cli.hedera.keygen.CryptoUtils;
import com.hedera.cli.hedera.keygen.EDKeyPair;
import com.hedera.cli.hedera.utils.DataDirectory;
import com.hedera.cli.models.HederaAccount;

import org.apache.logging.log4j.core.util.JsonUtils;
import org.hjson.JsonObject;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Component
@Command(name = "setup", description = "")
public class Setup implements Runnable {

  // default index 0 is compatible with Hedera wallet apps
  private int index = 0;

  @Override
  public void run() {
    CommandLine.usage(this, System.out);
  }

  public void handle(InputReader inputReader) {
    System.out.println("Start the setup process");
    String accountId = inputReader.prompt("account id that we will use as default operator");
    String phrase = inputReader.prompt("24 words phrase", "secret", false);
    saveToJson(accountId, phrase);
  }

  public void saveToJson(String accountId, String phrase) {
    List<String> phraseList = Arrays.asList(phrase.split(" "));
    System.out.println(phraseList);

    JsonObject account = new JsonObject();

    // recover key from phrase
    Mnemonic mnemonic = new Mnemonic();
    try {
      byte[] entropy = mnemonic.toEntropy(phraseList);
      byte[] seed = CryptoUtils.deriveKey(entropy, index, 32);
      EDKeyPair keyPair = new EDKeyPair(seed);
      System.out.println("priv key encoded: " + keyPair.getPrivateKeyEncodedHex());
      System.out.println("pub key encoded: " + keyPair.getPublicKeyEncodedHex());
      System.out.println("priv key hex: " + keyPair.getPrivateKeyHex());
      System.out.println("pub key hex: " + keyPair.getPublicKeyHex());
      System.out.println("seed(priv) + pub key hex: " + keyPair.getSeedAndPublicKeyHex());
      account.add("accountId", accountId);
      account.add("privateKey", keyPair.getPrivateKeyHex());
      account.add("publicKey", keyPair.getPublicKeyHex());

    } catch (MnemonicLengthException | MnemonicWordException | MnemonicChecksumException e) {
      e.printStackTrace();
    }

    // ~/.hedera/[network_name]/accounts/[account_name].json
    // TODO: once done, we write it as an "account_name.json" file and mark the account id in default.txt
    DataDirectory dataDirectory = new DataDirectory();
    String fileName = getRandomName();
    String fileNameWithExt = fileName + ".json";
    String networkName = dataDirectory.readFile("network.txt");

    String pathToAccountsFolder = networkName + File.separator + "accounts" + File.separator;
    String pathToAccountFile =  pathToAccountsFolder +  fileNameWithExt;
    String pathToDefaultTxt = pathToAccountsFolder +  "default.txt";

    ObjectMapper mapper = new ObjectMapper();
    
    try {
      // create the account json and write it to disk
      Object jsonObject = mapper.readValue(account.toString(), HederaAccount.class);
      String accountValue = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
      System.out.println(accountValue);
      dataDirectory.writeFile(pathToAccountFile, accountValue);
      // mark this account as the default
      dataDirectory.writeFile(pathToDefaultTxt, fileName + ":" + accountId);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getRandomName() {
    Random rand = new Random();
    List<String> botanyNames = BotanyWordList.words;
    return botanyNames.get(rand.nextInt(botanyNames.size()));
  }
}