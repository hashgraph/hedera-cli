package com.hedera.cli.hedera.bip39;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.hedera.cli.hedera.keygen.CryptoUtils;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A Mnemonic object may be used to convert lists of words per
 * <a href="https://github.com/bitcoin/bips/blob/master/bip-0039.mediawiki">the
 * BIP 39 specification</a>
 */

public class Mnemonic {

  private List<String> wordList;
  private static final int SEED_ITERATIONS = 2048;
  private static final int SEED_KEY_SIZE = 512;

  public Mnemonic() {
    this.wordList = Arrays.asList(EnglishHelper.words);
  }

  /**
   * Convert mnemonic word list to original entropy value.
   */
  public byte[] toEntropy(List<String> words) throws MnemonicException.MnemonicLengthException,
      MnemonicException.MnemonicWordException, MnemonicException.MnemonicChecksumException {
    if (words.size() % 3 > 0)
      throw new MnemonicException.MnemonicLengthException("Word list size must be multiple of three words.");

    if (words.size() == 0)
      throw new MnemonicException.MnemonicLengthException("Word list is empty.");

    // Look up all the words in the list and construct the
    // concatenation of the original entropy and the checksum.
    //
    int concatLenBits = words.size() * 11;
    boolean[] concatBits = new boolean[concatLenBits];
    int wordindex = 0;
    for (String word : words) {
      // Find the words index in the wordlist.
      int ndx = Collections.binarySearch(this.wordList, word);
      if (ndx < 0)
        throw new MnemonicException.MnemonicWordException(word);

      // Set the next 11 bits to the value of the index.
      for (int ii = 0; ii < 11; ++ii)
        concatBits[(wordindex * 11) + ii] = (ndx & (1 << (10 - ii))) != 0;
      ++wordindex;
    }

    int checksumLengthBits = concatLenBits / 33;
    int entropyLengthBits = concatLenBits - checksumLengthBits;

    // Extract original entropy as bytes.
    byte[] entropy = new byte[entropyLengthBits / 8];
    for (int ii = 0; ii < entropy.length; ++ii)
      for (int jj = 0; jj < 8; ++jj)
        if (concatBits[(ii * 8) + jj])
          entropy[ii] |= 1 << (7 - jj);

    // Take the digest of the entropy.
    byte[] hash = CryptoUtils.sha256Digest(entropy);
    boolean[] hashBits = bytesToBits(hash);

    // Check all the checksum bits.
    for (int i = 0; i < checksumLengthBits; ++i)
      if (concatBits[entropyLengthBits + i] != hashBits[i])
        throw new MnemonicException.MnemonicChecksumException();

    return entropy;
  }

  /**
   * Convert entropy data to mnemonic word list.
   */
  public List<String> toMnemonic(byte[] entropy) throws MnemonicException.MnemonicLengthException {
    if (entropy.length % 4 > 0)
      throw new MnemonicException.MnemonicLengthException("Entropy length not multiple of 32 bits.");

    if (entropy.length == 0)
      throw new MnemonicException.MnemonicLengthException("Entropy is empty.");

    // We take initial entropy of ENT bits and compute its
    // checksum by taking first ENT / 32 bits of its SHA256 hash.

    byte[] hash = CryptoUtils.sha256Digest(entropy);
    boolean[] hashBits = bytesToBits(hash);

    boolean[] entropyBits = bytesToBits(entropy);
    int checksumLengthBits = entropyBits.length / 32;

    // We append these bits to the end of the initial entropy.
    boolean[] concatBits = new boolean[entropyBits.length + checksumLengthBits];
    System.arraycopy(entropyBits, 0, concatBits, 0, entropyBits.length);
    System.arraycopy(hashBits, 0, concatBits, entropyBits.length, checksumLengthBits);

    // Next we take these concatenated bits and split them into
    // groups of 11 bits. Each group encodes number from 0-2047
    // which is a position in a wordlist. We convert numbers into
    // words and use joined words as mnemonic sentence.

    ArrayList<String> words = new ArrayList<>();
    int nwords = concatBits.length / 11;
    for (int i = 0; i < nwords; ++i) {
      int index = 0;
      for (int j = 0; j < 11; ++j) {
        index <<= 1;
        if (concatBits[(i * 11) + j])
          index |= 0x1;
      }
      words.add(this.wordList.get(index));
    }

    return words;
  }

  /**
   * Check to see if a mnemonic word list is valid.
   */
  public void check(List<String> words) throws MnemonicException {
    toEntropy(words);
  }

  private static boolean[] bytesToBits(byte[] data) {
    boolean[] bits = new boolean[data.length * 8];
    for (int i = 0; i < data.length; ++i)
      for (int j = 0; j < 8; ++j)
        bits[(i * 8) + j] = (data[i] & (1 << (7 - j))) != 0;
    return bits;
  }

  /**
   * Convert mnemonic word list to seed.
   * https://github.com/web3j/web3j/blob/master/crypto/src/main/java/org/web3j/crypto/MnemonicUtils.java
   */
  public static byte[] generateBipSeed(String mnemonic, String passphrase) {
    if (isMnemonicEmpty(mnemonic)) {
      throw new IllegalArgumentException("Mnemonic is required to generate a seed");
    }
    // A user may decide to protect their mnemonic with a passphrase. If a passphrase is not present, an empty string "" is used instead.
    passphrase = passphrase == null ? "" : passphrase;

    String salt = String.format("mnemonic%s", passphrase);
    PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA512Digest());

    // gen.int(passwordInBytes, saltInBytes, iterationsInInt);
    gen.init(mnemonic.getBytes(UTF_8), salt.getBytes(UTF_8), SEED_ITERATIONS);

    byte[] bipSeed = ((KeyParameter) gen.generateDerivedParameters(SEED_KEY_SIZE)).getKey();
    return bipSeed;
  }

  private static boolean isMnemonicEmpty(String mnemonic) {
    return mnemonic == null || mnemonic.trim().isEmpty();
  }
}
