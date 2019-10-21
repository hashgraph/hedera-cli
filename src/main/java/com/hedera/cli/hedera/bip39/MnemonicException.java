package com.hedera.cli.hedera.bip39;

/**
 * Exceptions thrown by the Mnemonic module.
 */
@SuppressWarnings("serial")
public class MnemonicException extends Exception {
  public MnemonicException() {
    super();
  }

  public MnemonicException(String msg) {
    super(msg);
  }

  /**
   * Thrown when an argument to Mnemonic is the wrong length.
   */
  public static class MnemonicLengthException extends MnemonicException {
    public MnemonicLengthException(String msg) {
      super(msg);
    }
  }

  /**
   * Thrown when a list of Mnemonic words fails the checksum check.
   */
  public static class MnemonicChecksumException extends MnemonicException {}

  /**
   * Thrown when a word is encountered which is not in the Mnemonic's word list.
   */
  public static class MnemonicWordException extends MnemonicException {
    /** Contains the word that was not found in the word list. */
    public final String badWord;

    public MnemonicWordException(String badWord) {
      super();
      this.badWord = badWord;
    }
  }
}
