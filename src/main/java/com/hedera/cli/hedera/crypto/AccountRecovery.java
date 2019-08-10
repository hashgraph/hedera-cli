package com.hedera.cli.hedera.crypto;

import java.util.Arrays;
import java.util.List;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.bip39.Mnemonic;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicChecksumException;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicLengthException;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicWordException;
import com.hedera.cli.hedera.keygen.CryptoUtils;
import com.hedera.cli.hedera.keygen.EDKeyPair;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "recovery", description = "@|fg(magenta) Recovers a Hedera account via the 24 recovery words.|@", helpCommand = true)
public class AccountRecovery implements Runnable {

  private int index = 0;

  @Option(names = { "-a", "--account-id" }, description = "Account ID in %nshardNum.realmNum.accountNum format")
  private String accountId;

  private InputReader inputReader;

  public AccountRecovery() {
  }

  public AccountRecovery(InputReader inputReader) {
    this.inputReader = inputReader;
  }

  @Override
  public void run() {
    System.out.println("Recovering account id " + accountId);
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

}