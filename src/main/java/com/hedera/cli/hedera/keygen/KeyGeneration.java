package com.hedera.cli.hedera.keygen;

import com.hedera.cli.hedera.bip39.Mnemonic;
import com.hedera.cli.hedera.bip39.MnemonicException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import javax.crypto.ShortBufferException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Component
@Command(name = "generate",
         description = "@|fg(225) Transfer hbars to a single account|@%n",
         helpCommand = true)
public class KeyGeneration implements Runnable {

  private List<String> mnemonic;
  private HGCSeed hgcSeed;

  @CommandLine.Spec
  private CommandLine.Model.CommandSpec spec;

  @CommandLine.Option(names = {"-m", "--method"}, description = "Input -m=hgc if passphrases have not been migrated on wallet "
          + "%nor account creations are before 13 September 2019. Input -m=bip if passphrases have been migrated on the wallet,"
          + "%nor account creations are after 13 September 2019")
  private String strMethod = "bip";

  public KeyGeneration(String strMethod) {
    setMethod(strMethod);
  }
  
  public String setMethod(String method) {
    if ("bip".equals(method)) {
      strMethod = method;
    } else if ("hgc".equals(method)) {
      strMethod = method;
    } else {
      throw new CommandLine.ParameterException(spec.commandLine(), "Method must either been hgc or bip");
    }
    return strMethod;
  }

  @Override
  public void run() {
    System.out.println("KeyGeneration");
    hgcSeed = new HGCSeed(CryptoUtils.getSecureRandomData(32));
    mnemonic = generateMnemonic(hgcSeed);
    generateKeysAndWords(hgcSeed, mnemonic);
  }

  public List<String> generateMnemonic(HGCSeed hgcSeed) {
    mnemonic = hgcSeed.toWordsList();
    // print
    String result = mnemonic.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(" ", "", ""));
    System.out.println("Your recovery words (store it safely): " + result);
    return mnemonic;
  }

  public KeyPair generateKeysAndWords(HGCSeed hgcSeed, List<String> wordList) {
    KeyPair keyPair;
    if (strMethod.contains("bip")) {
      keyPair = keyPairPostBipMigration(wordList);
    } else {
      keyPair = keyPairPriorToBipMigration(hgcSeed);
    }
    return keyPair;
  }

  /**
   * This keypair is compatible with most industry keys, use this if account creation is done
   * via Hedera Wallet app after 13 September 2019, or if your keys have already been migrated
   * using the Hedera Wallet app
   * @return
   */
  public KeyPair keyPairPostBipMigration(List<String> wordList) {
    EDBip32KeyChain keyChain = new EDBip32KeyChain();
    return keyChain.keyPairFromWordList(0, wordList);
  }

  /**
   * This keypair is only relevant if account was created, and NOT MIGRATED using Hedera wallet app
   * prior to 13 September 2019.
   * @return
   */
  public KeyPair keyPairPriorToBipMigration(HGCSeed hgcSeed) {
    KeyChain keyChain = new EDKeyChain(hgcSeed);
    int index = 0;
    return keyChain.keyAtIndex(index);
  }

  public EDKeyPair compareKeyGenWithEntropy(byte[] entropy, int index) {
    byte[] seed;
    seed = CryptoUtils.deriveKey(entropy, index, 32);
    return new EDKeyPair(seed);
  }

  public byte[] entropyFromMnemonic(List<String> mnemonic) {
    byte[] entropy = null;
    try {
      // Mnemonic returns an entropy
      entropy = new Mnemonic().toEntropy(mnemonic);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return entropy;
  }

  public List<String> compareMnemonicFromEntropy(byte[] entropy) {
    List<String> compareMnemonic = new ArrayList<>();
    try {
      compareMnemonic = new Mnemonic().toMnemonic(entropy);
      System.out.println(compareMnemonic);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return compareMnemonic;
  }

  public EDKeyPair compareKeyGenWithBipMnemonicFromHGCSeed(List<String> wordList, HGCSeed hgcSeed, int index) throws NoSuchAlgorithmException, InvalidKeyException, ShortBufferException {
    byte[] seed;
    String words = StringUtils.join(wordList, " ");
    EDBip32KeyChain kc = new EDBip32KeyChain(hgcSeed);
    byte[] bipSeed = kc.bipSeed(words);
    seed = Slip10Utils.deriveEd25519PrivateKey(bipSeed, 44, 3030, 0, 0, index);
    return new EDKeyPair(seed);
  }

  public List<String> compareMnemonicFromBipEntropy(byte[] entropy)
      throws MnemonicException.MnemonicLengthException, NoSuchAlgorithmException {
    List<String> mnemonic = new Mnemonic().toMnemonic(entropy);
    return mnemonic;
  }
}