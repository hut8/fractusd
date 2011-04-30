package fractus.crypto;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.log4j.Logger;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.JCEECPrivateKey;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.jce.provider.asymmetric.ec.KeyPairGenerator;

import fractus.main.BinaryUtil;

public class ServerKeyManager {
	private static Logger log;
	static {
		log = Logger.getLogger(ServerKeyManager.class.getName());
	}

	public static final byte[] SALT = "ycgT4QT7ZZCFAR51m500C4AiCqkVMnhKRdq2Nte2QKictVvqFhC03oHEu2Lw".getBytes();
	public static final int ITERATION_COUNT = 1000000;

	public static KeyPair generateKey()
	throws GeneralSecurityException {
		log.info("Initializing keypair generator for EC secp521r1");
		ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp521r1");
		KeyPairGenerator g = new org.bouncycastle.jce.provider.asymmetric.ec.KeyPairGenerator.ECDH();
		g.initialize(ecSpec, new SecureRandom());
		log.info("Generating EC secp521r1 Key Pair");
		KeyPair pair = g.generateKeyPair();
		log.info("ECDH KeyPair generated with public key:" + BinaryUtil.encodeData(pair.getPublic().getEncoded()));
		return pair;
	}

	public static void encryptKey(PrivateKey key, char[] password, String filename)
	throws IOException, IllegalStateException, InvalidCipherTextException {
		log.info("Encrypting private key with PBKDF 2 (PKCS5v2) and writing to " + filename);
		log.debug("Encoding key as PKCS8");
		// Extract encoded format of key
		byte[] PKCS8Key = key.getEncoded();
		
		log.debug("Generating parameters from password");
		// Generate a secret and IV based on PKCS5S2 (PBKDF v2)
		PBEParametersGenerator generator = new PKCS5S2ParametersGenerator();
		byte[] passwordBytes = PBEParametersGenerator.PKCS5PasswordToBytes(password);
		generator.init(passwordBytes, SALT, ITERATION_COUNT);
		ParametersWithIV cp = (ParametersWithIV)generator.generateDerivedParameters(256, 96);
	
		log.debug("Encrypting key with AES-GCM");
		// Encrypt the private key according to the secret
		GCMBlockCipher cipher = new GCMBlockCipher(new AESEngine());
		cipher.init(true, cp);
		int outputSize = cipher.getOutputSize(PKCS8Key.length);
		byte[] encryptedPKCS8Key = new byte[outputSize];
		int offset = cipher.processBytes(PKCS8Key, 0, PKCS8Key.length, encryptedPKCS8Key, 0);
		offset += cipher.doFinal(encryptedPKCS8Key, offset);
		
		log.debug("Writing encrypted key [" + offset + "B] to file " + filename);
		// Store the key in the file specified
		FileOutputStream fileOutputStream = new FileOutputStream(new File(filename));
		fileOutputStream.write(encryptedPKCS8Key);
		fileOutputStream.flush();
		fileOutputStream.close();
	}
	
	public static KeyPair decryptKey(char[] password, String filename)
	throws IOException, IllegalStateException, InvalidCipherTextException,
	NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		log.debug("Reading in the entire ciphertext from input file");
		// Read in the entire ciphertext
		File inputFile = new File(filename);
		DataInputStream input = new DataInputStream(new FileInputStream(inputFile));
		byte[] encryptedPKCS8Key = new byte[(int) inputFile.length()];
		input.readFully(encryptedPKCS8Key);
		input.close();
		
		log.debug("Generating parameters from password");
		// Generate a secret and IV based on PKCS5S2 (PBKDF v2)
		PBEParametersGenerator generator = new PKCS5S2ParametersGenerator();
		byte[] passwordBytes = PBEParametersGenerator.PKCS5PasswordToBytes(password);
		generator.init(passwordBytes, SALT, ITERATION_COUNT);
		ParametersWithIV cp = (ParametersWithIV)generator.generateDerivedParameters(256, 96);
		
		log.debug("Decrypting key with AES-GCM");
		// Encrypt the private key according to the secret
		GCMBlockCipher cipher = new GCMBlockCipher(new AESEngine());
		cipher.init(false, cp);
		int outputSize = cipher.getOutputSize(encryptedPKCS8Key.length);
		byte[] PKCS8Key = new byte[outputSize];
		int offset = cipher.processBytes(encryptedPKCS8Key, 0, encryptedPKCS8Key.length, PKCS8Key, 0);
		offset += cipher.doFinal(encryptedPKCS8Key, offset);
	
		log.debug("Decoding private key");
		KeyFactory keyFactory = KeyFactory.getInstance("ECDH", "BC");
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(PKCS8Key);
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		
		log.debug("Computing public key");
		if (!(privateKey instanceof JCEECPrivateKey)) {
			throw new InvalidKeySpecException("Found key incompatible with JCEECPrivateKey");
		}
		JCEECPrivateKey ecPrivateKey = (JCEECPrivateKey)privateKey;
		ECParameterSpec parameterSpec = ecPrivateKey.getParameters();
		ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(
				parameterSpec.getG().multiply(ecPrivateKey.getD()),
				parameterSpec);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		
		log.debug("Generated key pair");
		return new KeyPair(publicKey, privateKey);
	}
}
