package com.hedera.cli.hedera.bip39;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicLengthException;
import com.hedera.cli.hedera.bip39.MnemonicException.MnemonicWordException;

import org.junit.jupiter.api.Test;

public class MnemonicTest {

  @SuppressWarnings("serial")
  @Test
  public void testToEntropy() {
    List<String> words = Arrays.asList(EnglishHelper.words);
    List<String> wordsNotMultipleOf3 = words.subList(0, 1);

    // if we only have two words, an exception will be thrown
    Mnemonic m = new Mnemonic();
    assertThrows(MnemonicLengthException.class, () -> {
      m.toEntropy(wordsNotMultipleOf3);
    });

    // if we have no words at all, an exception will be thrown
    assertThrows(MnemonicLengthException.class, () -> {
      m.toEntropy(new ArrayList<String>());
    });

    // if we have words that is not in EnglishHelper.words at all, an exception will be thrown
    assertThrows(MnemonicWordException.class, () -> {
      m.toEntropy(new ArrayList<String>() {{
        add("A");
        add("B");
        add("C");
      }});
    });
  }

}