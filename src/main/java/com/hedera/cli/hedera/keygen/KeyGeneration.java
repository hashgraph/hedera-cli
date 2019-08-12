package com.hedera.cli.hedera.keygen;

import com.hedera.cli.hedera.bip39.Mnemonic;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import java.util.ArrayList;
import java.util.List;

@Component
@Command(name = "generate",
         description = "@|fg(magenta) Transfer hbars to a single account|@%n",
         helpCommand = true)
public class KeyGeneration implements Runnable {

  private List<String> mnemonic;
  private HGCSeed hgcSeed;

  @Override
  public void run() {
    System.out.println("KeyGeneration");
    hgcSeed = new HGCSeed(CryptoUtils.getSecureRandomData(32));
    mnemonic = generateMnemonic(hgcSeed);
    generateKeysAndWords(hgcSeed);
  }

  public List<String> generateMnemonic(HGCSeed hgcSeed) {
    mnemonic = hgcSeed.toWordsList();
    System.out.println("Your recovery words (store it safely): " + mnemonic);
    return mnemonic;
  }

  public KeyPair generateKeysAndWords(HGCSeed hgcSeed) {
    KeyChain keyChain = new EDKeyChain(hgcSeed);
    int index = 0;
    KeyPair keyPair = keyChain.keyAtIndex(index);
    System.out.println("Private key ASN.1 encoded: " + keyPair.getPrivateKeyEncodedHex()); // encoded works with index 0
    System.out.println("Public key ASN.1 encoded: " + keyPair.getPublicKeyEncodedHex()); // encoded works with index 0
    System.out.println("Private key HEX: " + keyPair.getSeedAndPublicKeyHex().substring(0, 64));
    System.out.println("Public key HEX: " + keyPair.getPublicKeyHex());
    System.out.println("Private key wallet/extension: " + keyPair.getSeedAndPublicKeyHex());
    return keyPair;
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
}