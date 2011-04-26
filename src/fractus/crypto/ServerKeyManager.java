package fractus.crypto;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.spec.PBEKeySpec;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;

public class ServerKeyManager {
	private static Logger log;
	static {
		log = Logger.getLogger(ServerKeyManager.class.getName());
	}

	public static final byte[] SALT = "ycgT4QT7ZZCFAR51m500C4AiCqkVMnhKRdq2Nte2QKictVvqFhC03oHEu2Lw".getBytes();
	public static final int ITERATION_COUNT = 100000;

	public static KeyPair generateKey()
	throws GeneralSecurityException {
		log.info("Initializing keypair generator for EC secp521r1");
		ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp521r1");
		log.info("EC Spec: " + ecSpec.toString());
		KeyPairGenerator g = KeyPairGenerator.getInstance("ECDH", "BC");
		g.initialize(ecSpec, new SecureRandom());
		log.info("Generating EC secp521r1 Key Pair");
		KeyPair pair = g.generateKeyPair();
		log.info("ECDC KeyPair generated with public key:" + pair.getPublic().getEncoded());
		return pair;
	}

	public static void encryptKey(PrivateKey key, char[] password, String filename)
	throws IOException, IllegalStateException, InvalidCipherTextException {
		// TODO: Use Standard PKCS7 stuff.  PBEWITHSHA256AND256BITAES-CBC-BC
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
		cipher.doFinal(encryptedPKCS8Key, offset);
		
		log.debug("Writing encrypted key to file " + filename);
		// Store the key in the file specified
		FileOutputStream fileOutputStream = new FileOutputStream(new File(filename));
		fileOutputStream.write(encryptedPKCS8Key);
		fileOutputStream.flush();
		fileOutputStream.close();
	}
	
	public static void readKey(byte[] cipherData, char[] password)
	throws IOException, IllegalStateException, InvalidCipherTextException {
		GCMBlockCipher cipher = new GCMBlockCipher(new AESEngine());
		PBEParametersGenerator generator = new PKCS5S2ParametersGenerator();
		ByteArrayInputStream bIn = new ByteArrayInputStream(cipherData);
		ASN1InputStream dIn = new ASN1InputStream(bIn);
		EncryptedPrivateKeyInfo info = new EncryptedPrivateKeyInfo((ASN1Sequence)dIn.readObject());
        PBES2Parameters alg = new PBES2Parameters((ASN1Sequence)info.getEncryptionAlgorithm().getParameters());
        PBKDF2Params func = PBKDF2Params.getInstance(alg.getKeyDerivationFunc().getParameters());
        EncryptionScheme scheme = alg.getEncryptionScheme();
        int keySize = func.getKeyLength().intValue() * 8;
        int iterationCount = func.getIterationCount().intValue();
        byte[] salt = func.getSalt();
        generator.init(PBEParametersGenerator.PKCS5PasswordToBytes(password),
            salt, iterationCount);
        byte[]  iv = ((ASN1OctetString)scheme.getObject()).getOctets();
        CipherParameters param = new ParametersWithIV(generator.generateDerivedParameters(keySize), iv);
        cipher.init(false, param);
        byte[] data = info.getEncryptedData();
        byte[] out = new byte[cipher.getOutputSize(data.length)];
        int len = cipher.processBytes(data, 0, data.length, out, 0);
        len += cipher.doFinal(out, len);
	}
}
