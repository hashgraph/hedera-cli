package com.hedera.cli.hedera.keygen;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.EdDSASecurityProvider;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.bc.BcECContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.util.io.pem.PemObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.DrbgParameters;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;

public class KeyStoreGen {

	private static final String DEFAULT_KEY_STORE_FILE_NAME = "KeyStore.pfx";

	private static final String PRIVATE_KEY_ALIAS = "private.key";

	private static final String DEFAULT_KEY_STORE_TYPE = "PKCS12";

	private static final String DEFAULT_PROTECTION_ALGORITHM = "PBEWithHmacSHA384AndAES_256";

	private static final int ONE_HUNDRED_YEARS_IN_DAYS = 24 * 365 * 100;

	public static KeyPair createKeyStore(final char[] passphrase, final KeyPair keyPair) {
		return createKeyStore(passphrase, DEFAULT_KEY_STORE_FILE_NAME, keyPair);
	}

	public static KeyPair createKeyStore(final char[] passphrase, String filename, final KeyPair keyPair) {
		if (filename.equals("")) {
			filename = DEFAULT_KEY_STORE_FILE_NAME;
		}
		try (FileOutputStream fos = new FileOutputStream(filename)){
			final Certificate[] certificates = new Certificate[]{ createCertificate(keyPair.getPublicKey(), keyPair.getPrivateKey()) };
			final PrivateKeyEntry privateKeyEntry = new PrivateKeyEntry(keyPair.getPrivateKey(), certificates);
			final PasswordProtection passwordProtection = new PasswordProtection(passphrase, DEFAULT_PROTECTION_ALGORITHM, null);
			final KeyStore keyStore = KeyStore.getInstance(DEFAULT_KEY_STORE_TYPE);
			keyStore.setEntry(PRIVATE_KEY_ALIAS, privateKeyEntry, passwordProtection);
			keyStore.store(fos, passphrase);
			return keyPair;
		} catch (final Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public static KeyPair loadKey(final char[] passphrase) {
		return loadKey(passphrase, DEFAULT_KEY_STORE_FILE_NAME);
	}

	public static KeyPair loadKey(final char[] passphrase, String filename) {
		if (filename.equals("")) {
			filename = DEFAULT_KEY_STORE_FILE_NAME;
		}
		try (FileInputStream fis = new FileInputStream(filename)) {
			final PasswordProtection passwordProtection = new PasswordProtection(passphrase,
					DEFAULT_PROTECTION_ALGORITHM, null);
			final KeyStore keyStore = KeyStore.getInstance(DEFAULT_KEY_STORE_TYPE);
			keyStore.load(fis, passphrase);
			final PrivateKeyEntry entry = (PrivateKeyEntry) keyStore.getEntry(PRIVATE_KEY_ALIAS, passwordProtection);
			return EDKeyPair.buildFromPrivateKey(entry.getPrivateKey().getEncoded());
		} catch (final Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private static Certificate createCertificate(PublicKey publicKey, PrivateKey privateKey) throws IOException, OperatorCreationException, CertificateException {
		final X500Name dn = new X500Name("CN=" + PRIVATE_KEY_ALIAS);
		System.out.println(publicKey.getAlgorithm());
		System.out.println(privateKey.getAlgorithm());
		final AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA1WITHDSA");
		final AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);
		final AsymmetricKeyParameter privateKeyAsymKeyParam = PrivateKeyFactory.createKey(privateKey.getEncoded());
		final SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
		final ContentSigner sigGen = new BcECContentSignerBuilder(sigAlgId, digAlgId).build(privateKeyAsymKeyParam);
		final Date from = new Date();
		final Date to = new Date(from.getTime() + ONE_HUNDRED_YEARS_IN_DAYS * 86400000L);
		final BigInteger sn = new BigInteger(64, new SecureRandom());

		X509v1CertificateBuilder v1CertGen = new X509v1CertificateBuilder(dn, sn, from, to, dn, subPubKeyInfo);
		X509CertificateHolder certificateHolder = v1CertGen.build(sigGen);
		return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certificateHolder);
	}

	public static void writeKey(final PrivateKey privateKey, final OutputStream ostream, final char[] password) throws NoSuchAlgorithmException, OperatorCreationException, IOException {

		final Provider bcProvider = new BouncyCastleProvider();

		final SecureRandom random = SecureRandom.getInstance("DRBG",
				DrbgParameters.instantiation(256, DrbgParameters.Capability.RESEED_ONLY, null));

		final OutputEncryptor encryptor = (new JceOpenSSLPKCS8EncryptorBuilder(PKCS8Generator.AES_256_CBC))
				.setPRF(PKCS8Generator.PRF_HMACSHA384)
				.setIterationCount(10000)
				.setRandom(random)
				.setPasssword(password)
				.setProvider(bcProvider)
				.build();

		final JcaPKCS8Generator generator = new JcaPKCS8Generator(privateKey, encryptor);
		final PemObject pemObject = generator.generate();

		final JcaPEMWriter pemWriter = new JcaPEMWriter(new OutputStreamWriter(ostream));

		pemWriter.writeObject(pemObject);
		pemWriter.flush();
		pemWriter.close();

	}

	public static java.security.KeyPair loadKey(final InputStream istream, final char[] password) throws IOException,
			OperatorCreationException, PKCSException {
		final Provider bcProvider = new BouncyCastleProvider();
		final Provider edProvider = new EdDSASecurityProvider();
		final PEMParser parser = new PEMParser(new InputStreamReader(istream));
		final Object rawObject = parser.readObject();
		final JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(edProvider);

		java.security.KeyPair kp;

		if (rawObject instanceof PEMEncryptedKeyPair) {
			final PEMEncryptedKeyPair ekp = (PEMEncryptedKeyPair)rawObject;
			final PEMDecryptorProvider decryptor = new JcePEMDecryptorProviderBuilder().setProvider(bcProvider).build(password);
			kp = converter.getKeyPair(ekp.decryptKeyPair(decryptor));
		} else if (rawObject instanceof PKCS8EncryptedPrivateKeyInfo) {
			final PKCS8EncryptedPrivateKeyInfo ekpi = (PKCS8EncryptedPrivateKeyInfo)rawObject;
			final InputDecryptorProvider decryptor = new JceOpenSSLPKCS8DecryptorProviderBuilder()
					.setProvider(bcProvider)
					.build(password);

			final PrivateKeyInfo pki = ekpi.decryptPrivateKeyInfo(decryptor);
			final EdDSAPrivateKey sk = (EdDSAPrivateKey)converter.getPrivateKey(pki);
			final EdDSAPublicKey pk = new EdDSAPublicKey(new EdDSAPublicKeySpec(sk.getA(), EdDSANamedCurveTable.ED_25519_CURVE_SPEC));
			kp = new java.security.KeyPair(pk, sk);
		} else {
			final PEMKeyPair ukp = (PEMKeyPair)rawObject;
			kp = converter.getKeyPair(ukp);
		}

		parser.close();
		return kp;
	}
}