package com.hedera.cli.hedera.crypto;

import java.util.Arrays;
import java.util.List;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.bip39.Mnemonic;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicChecksumException;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicLengthException;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicWordException;
import com.hedera.cli.hedera.keygen.CryptoUtils;
import com.hedera.cli.hedera.keygen.EDBip32KeyChain;
import com.hedera.cli.hedera.keygen.EDKeyPair;

import com.hedera.cli.hedera.keygen.KeyPair;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

@NoArgsConstructor
@Setter
@Component
@Command(name = "recovery", description = "@|fg(225) Recovers a Hedera account via the 24 recovery words.|@", helpCommand = true)
public class AccountRecovery implements Runnable {

  @Spec
  CommandSpec spec;

  @Autowired
  ApplicationContext context;

  @Option(names = { "-a", "--account-id" }, description = "Account ID in %nshardNum.realmNum.accountNum format")
  private String accountId;

  @Option(names = {"-m", "--method"}, arity= "1", description = "Recovers keypair from recovery phrase")
  private String strMethod = "bip";

  private int index = 0;
  private InputReader inputReader;

  @Override
  public void run() {
    System.out.println("Recovering accountID in the format of 0.0.xxxx" + accountId);
    strMethod = inputReader.prompt("Have you updated your account on Hedera wallet? If updated, enter `bip`, else enter `hgc`");
    String phrase = inputReader.prompt("24 words phrase", "secret", false);
    List<String> phraseList = Arrays.asList(phrase.split(" "));
    System.out.println(phraseList);
    // recover key from phrase
    KeyPair keyPair;
    if (strMethod.equals("bip")) {
      keyPair =  recoverEDKeypairPostBipMigration(phraseList);
      printKeyPair(keyPair);
    } else if (strMethod.equals("hgc")) {
      keyPair = recoverEd25519AccountKeypair(phraseList);
      printKeyPair(keyPair);
    } else {
      throw new ParameterException(spec.commandLine(), "Method must either been hgc or bip");
    }
  }

  public KeyPair recoverEd25519AccountKeypair(List<String> phraseList) {
    KeyPair keyPair = null;
    Mnemonic mnemonic = new Mnemonic();
    try {
      byte[] entropy = mnemonic.toEntropy(phraseList);
      byte[] seed = CryptoUtils.deriveKey(entropy, index, 32);
      keyPair = new EDKeyPair(seed);
      printKeyPair(keyPair);
    } catch (MnemonicLengthException | MnemonicWordException | MnemonicChecksumException e) {
      e.printStackTrace();
    }
    return keyPair;
  }

  public KeyPair recoverEDKeypairPostBipMigration(List<String> phraseList) {
    EDBip32KeyChain edBip32KeyChain = new EDBip32KeyChain();
    KeyPair keyPair = edBip32KeyChain.keyPairFromWordList(0, phraseList);
    return keyPair;
  }

  public void printKeyPair(KeyPair keyPair) {
    System.out.println("priv key encoded: " + keyPair.getPrivateKeyEncodedHex());
    System.out.println("pub key encoded: " + keyPair.getPublicKeyEncodedHex());
    System.out.println("priv key hex legacy: " + keyPair.getSeedAndPublicKeyHex().substring(0, 64));
    System.out.println("pub key hex: " + keyPair.getPublicKeyHex());
    System.out.println("seed(priv) + pub key hex: " + keyPair.getSeedAndPublicKeyHex());
  }
}