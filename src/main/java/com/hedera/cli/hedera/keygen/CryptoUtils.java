package com.hedera.cli.hedera.keygen;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

public class CryptoUtils {
  public static byte[] getSecureRandomData(int length) {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[length];
    random.nextBytes(bytes);
    return bytes;
  }

  public static byte[] shaDigest(byte[] message, String algo) throws NoSuchAlgorithmException {
    MessageDigest digest = null;
    try {
      digest = MessageDigest.getInstance(algo);
    } catch (NoSuchAlgorithmException e) {
      throw new NoSuchAlgorithmException(e.getMessage());
    }
    List<String> ourSupportedAlgo = Stream.of("SHA-256", "SHA-384")
      .collect(Collectors.toList());
    if (!ourSupportedAlgo.contains(algo)) {
      throw new NoSuchAlgorithmException("We only support SHA-256 and SHA-384");
    }
    byte[] hash = digest.digest(message);
    return hash;
  }

  public static byte[] deriveKey(byte[] seed, long index, int length) {

    byte[] password = new byte[seed.length + Long.BYTES];
    for (int i = 0; i < seed.length; i++) {
      password[i] = seed[i];
    }
    byte[] indexData = longToBytes(index);
    int c = 0;
    for (int i = indexData.length - 1; i >= 0; i--) {
      password[seed.length + c] = indexData[i];
      c++;
    }

    byte[] salt = null;
    salt = new byte[] { -1 };

    // String passwordStr = Hex.toHexString(password);
    PBKDF2Params params = new PBKDF2Params(salt, 2048, length * 8);

    PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA512Digest());
    gen.init(password, params.getSalt(), params.getIterationCount().intValue());

    byte[] derivedKey = ((KeyParameter) gen.generateDerivedParameters(length * 8)).getKey();

    return derivedKey;
  }

  public static byte[] longToBytes(long x) {
    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
    buffer.putLong(x);
    return buffer.array();
  }
}
