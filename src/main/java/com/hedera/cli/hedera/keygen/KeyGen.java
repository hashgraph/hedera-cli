package com.hedera.cli.hedera.keygen;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.util.encoders.Hex;

import com.google.common.io.BaseEncoding;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

public final class KeyGen {

  public static void main(String[] args) {
    String seed = "";
    int index = -1;
    String wordList = "";
    String password = "";
    String fileName = "";
    boolean createStore = false;
    boolean load = false;
    KeyPair keyPair = null;
    Reference referenceSeed = null;

    for (int i = 0; i < args.length; i++) {
      // remove all spaces from parameter
      String input = args[i]; // .replaceAll(" ", "");
      // get parameter and value
      String[] paramValue = input.split("=");

      String value = "";
      String param = paramValue[0];
      if (paramValue.length == 2) {
        value = paramValue[1];
      }

      switch (param) {
      case "-save":
        createStore = true;
        break;
      case "-load":
        load = true;
        break;
      case "-password":
        validateValue(value, args[i]);
        password = value;
        break;
      case "-file":
        validateValue(value, args[i]);
        fileName = value;
        break;
      case "-index":
        validateValue(value, args[i]);
        try {
          index = Integer.parseInt(value);
        } catch (NumberFormatException e) {
          System.out.println("Argument" + index + " must be an integer.");
          System.exit(3);
        }
        break;
      case "-seed":
        validateValue(value, args[i]);
        seed = value;
        if (seed.length() != 64) {
          System.out.println("Seed length must be 64 hex encoded bytes for ED25519");
          System.exit(3);
        }
        break;
      case "-words":
        validateValue(value, args[i]);
        String[] array = value.split(",");
        if (array.length != 22) {
          array = value.split(" ");
        }
        if (array.length != 22) {
          System.out.println("Invalid recovery word count - should be 22, got " + array.length);
          System.exit(3);
        }
        wordList = value.replaceAll(",", " ");
        break;
      default:
        System.out.println("Invalid input parameter(s) - " + param);
        System.out.println("Should be");
        System.out.println("* no parameters - generates an ED25519 key at index -1");
        System.out.println(
            "* -index=indexvalue - generates an ED25519 key at index indexvalue, must be greater than or equal to -1");
        System.out.println("* -seed=seedvalue - 64 hex encoded bytes to seed the key generation with");
        System.out.println("* -words=22 recovery words separated by commas or spaces (if surrounded by double quotes)");
        System.out.println("Example: -index=-1 -words=word1,word2,word3...,word22");
        System.out.println("or     : -index=-1 -words=\"word1 word2 word3 ... word22\"");
        System.out.println("Note");
        System.out.println(
            "* The -index parameter is optional, it defaults to -1, for Hedera Wallet Compliant key recovery use index 0");
        System.out.println("* The -seed parameter is optional, a seed will be generated automatically if not supplied");
        System.out.println(
            "* The -words parameter is optional, it is only required if recovering an existing key pair from a word list");
        System.out.println(
            "* Finally, the list of recovery words overrides the seed parameter value (e.g. seed will be ignored if words supplied");

        System.exit(3);
      }
    }

    // validate inputs
    if ((createStore) && (password.equals(""))) {
      System.out.println("A password must be supplied to create a keystore");
      System.exit(3);
    }
    if (load && createStore) {
      System.out.println("Cannot load and create at the same time");
      System.exit(3);
    }
    if ((load) && (password.equals(""))) {
      System.out.println("A password must be supplied to load a keystore");
      System.exit(3);
    }

    if (!load) {
      referenceSeed = new Reference(CryptoUtils.getSecureRandomData(32));

      if (!wordList.equals("")) {
        if (!seed.equals("")) {
          System.out.println("*** Recovery words provided, ignoring seed parameter.");
        }
        // recover key from words
        referenceSeed = new Reference(wordList);
        System.out.println(String.format("Your recovered key pair for index %d is:", index));
      } else if (!seed.equals("")) {
        // recover key from seed
        byte[] seedBytes = Hex.decode(seed);
        referenceSeed = new Reference(seedBytes);
        System.out.println(String.format("Your generated key pair for index %d and own seed is:", index));

      } else {
        System.out.println(String.format("Your generated key pair for index %d is:", index));
      }

      KeyChain keyChain = new EDKeyChain(referenceSeed);
      keyPair = keyChain.keyAtIndex(index);
    } else {
      keyPair = KeyStoreGen.loadKey(password.toCharArray(), fileName);
    }

    printStars();
    System.out.println(String.format("Public key (hex)     : %s", keyPair.getPublicKeyHex()));
    System.out.println(String.format("Public key (enc hex) : %s", keyPair.getPublicKeyEncodedHex()));
    System.out.println("");
    // System.out.println(String.format("Secret key (hex) : %s",
    // keyPair.getPrivateKeyHex()));
    System.out.println(String.format("Secret key (hex)     : %s", keyPair.getPrivateKeySeedHex()));
    System.out.println(String.format("Secret key (enc hex) : %s", keyPair.getPrivateKeyEncodedHex()));
    System.out.println("");
    // System.out.println(String.format("Secret Seed (hex) : %s",
    // keyPair.getPrivateKeySeedHex()));
    System.out.println(String.format("Seed+PubKey (hex)    : %s", keyPair.getSeedAndPublicKeyHex()));
    if (wordList.equals("")) {
      // not recovering, show recovery word list
      if (referenceSeed != null) {
        System.out.println("");
        System.out.println(referenceSeed.toWords("Recovery words  : ", ",", ",", ",", ",", ",", ""));
      }
    }
    printStars();

    if (createStore) {

      try {
        final AsymmetricKeyParameter privateKeyAsymKeyParam = PrivateKeyFactory.createKey(keyPair.getPrivateKeySeed());
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      try {
        final AsymmetricKeyParameter privateKeyAsymKeyParam = PrivateKeyFactory
            .createKey(keyPair.getPrivateKey().getEncoded());
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      KeyStoreGen.createKeyStore(password.toCharArray(), fileName, keyPair);
    }
  }

  private static void validateValue(String paramValue, String arguments) {
    if (paramValue.equals("")) {
      System.out.println("Parameter and value must be separated by an equal (=) sign");
      System.out.println("Invalid Input detected at:" + arguments);
      System.exit(3);
    }
  }

  private final static void printStars() {
    System.out.println("************************************************************************");
  }
}
