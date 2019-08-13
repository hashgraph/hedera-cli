package com.hedera.cli.hedera.setup;

import java.util.Arrays;
import java.util.List;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.bip39.Mnemonic;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicChecksumException;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicLengthException;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicWordException;
import com.hedera.cli.hedera.keygen.CryptoUtils;
import com.hedera.cli.hedera.keygen.EDKeyPair;

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
    // System.out.println("Recovering account id " + accountId);
    // TODO: prompt user to input the accountId

    String phrase = inputReader.prompt("24 words phrase", "secret", false);
    List<String> phraseList = Arrays.asList(phrase.split(" "));
    System.out.println(phraseList);

    // recover key from phrase
    Mnemonic mnemonic = new Mnemonic();
    try {
      byte[] entropy = mnemonic.toEntropy(phraseList);
      byte[] seed = CryptoUtils.deriveKey(entropy, index, 32);
      EDKeyPair keyPair = new EDKeyPair(seed);
      System.out.println("priv key encoded: " + keyPair.getPrivateKeyEncodedHex());
      System.out.println("pub key encoded: " + keyPair.getPublicKeyEncodedHex());
      System.out.println("priv key hex legacy: " + keyPair.getSeedAndPublicKeyHex().substring(0, 64));
      System.out.println("pub key hex: " + keyPair.getPublicKeyHex());
      System.out.println("seed(priv) + pub key hex: " + keyPair.getSeedAndPublicKeyHex());
    } catch (MnemonicLengthException | MnemonicWordException | MnemonicChecksumException e) {
      e.printStackTrace();
    }
  }

  // TODO: once done, we write it as an "account_name.json" file and mark the account id in default.txt

}