
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.util.encoders.*;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;

public class EncryptionManager {
	private static Logger log;
	
	private KeyPair keyPair;
	private String encodingType;
	private String encodedKey;
	ECDHBasicAgreement agreement;
	
	static {
		log = Logger.getLogger(EncryptionManager.class.getName());
	}
	
	public static byte[] convertToBytes(char[] passwd)
	throws UnsupportedEncodingException,
	NoSuchAlgorithmException {
		log.info("Converting password to bytes...");
		// Salt password
		byte[] salt;
		salt = "man, fractus is really secure".getBytes("UTF-8");
		byte[] b = new byte[passwd.length+salt.length];
		for(int i = 0; i < passwd.length; i++) {
			b[i] = (byte) passwd[i];
		}
		for(int i = passwd.length; i < b.length; i++) {
			b[i] = salt[i-passwd.length];
		}
	    
		// SHA256 Message
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
	    digest.reset();
	    byte[] input = digest.digest(b);
	    log.info("Created array of " + input.length + " bytes from password");
	    
		return input;
	}
	
	public static byte[] serialize(KeyPair kp)
	throws IOException {
		log.info("Serializing KeyPair with Public Key [" + kp.getPublic().getEncoded() + "]");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(kp);
		byte[] mem = baos.toByteArray();
		log.info("Serialized " + mem.length + " bytes");
		return mem;
	}
	
	private static KeyPair decryptKey(String keyfile, char[] passwd)
	throws IOException,
	GeneralSecurityException,
	ClassNotFoundException {
		log.info("Attemping to decrypt key from " + keyfile);
		
		// Read in entire encrypted key file
		File file = new File(keyfile);
		FileInputStream fis = new FileInputStream(keyfile);
		byte[] inputData = new byte[(int)file.length()];
		fis.read(inputData);
		fis.close();
		
		// Create cipher from hashed password
		Cipher outCipher = getCipher();
		SecretKeySpec skeySpec = new SecretKeySpec(convertToBytes(passwd), "AES");
		outCipher.init(Cipher.DECRYPT_MODE, skeySpec);
		
		// Decrypt password
		byte[] cipherData = null;
		cipherData = outCipher.doFinal(inputData);
		
		// Create keypair object
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(cipherData));
		KeyPair keyPair;
		keyPair = (KeyPair)ois.readObject();
		return keyPair;
	}
	
	private static Cipher getCipher() throws GeneralSecurityException {
		return Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
	}
	
	private static void encryptKey(KeyPair keypair, String keyfile, char[] passwd)
	throws IOException,
	GeneralSecurityException {
		log.info("Encrypting key and writing to [" + keyfile + "]");
		Cipher outCipher = getCipher();
		log.info("Got cipher for output: " + outCipher.getAlgorithm());
		log.info("Provider info: " + outCipher.getProvider().getInfo());
		
		byte[] passHash = convertToBytes(passwd);
		log.info("Password Hash is " + passHash.length + " bytes long");
		
		SecretKeySpec skeySpec = new SecretKeySpec(passHash, "AES");
		log.info("Got SecretKeySpec from hash: " + skeySpec.getAlgorithm());

		byte[] cipherData = null;
		outCipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		cipherData = outCipher.doFinal(serialize(keypair));
		FileOutputStream fos;
		fos = new FileOutputStream(keyfile);
		fos.write(cipherData);
		fos.flush();
		fos.close();
	}
	
	public EncryptionManager(String keyfile, char[] passwd)
	throws IOException,
	GeneralSecurityException,
	ClassNotFoundException {
		keyPair = decryptKey(keyfile,passwd);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Base64.encode(keyPair.getPublic().getEncoded(), baos);
		
		encodedKey = baos.toString("UTF-8");
		encodingType = keyPair.getPublic().getFormat();
		
		// Reference Private Key
		ECPrivateKey privKey = (ECPrivateKey)keyPair.getPrivate();
		log.info("Constructed private key object from key in format: " + encodingType);
		
		log.info("Initializing ECDH Agreement Engine");
		// Initialize ECDH
		agreement = new ECDHBasicAgreement();
		ECParameterSpec spec = privKey.getParameters();
		ECDomainParameters dp = new ECDomainParameters(spec.getCurve(), spec.getG(), spec.getN(), spec.getH(), spec.getSeed());
		ECPrivateKeyParameters pkp = new ECPrivateKeyParameters(privKey.getD(), dp);
		agreement.init(pkp);
		log.info("ECDH Agreement Engine initialized");
	}
	

	public SecretKeySpec deriveKey(CipherParameters cp)
	throws NoSuchAlgorithmException {
		log.info("Calculating secret key");
		BigInteger bi = agreement.calculateAgreement(cp);
		byte[] key = bi.toByteArray();
		MessageDigest digest = null;
		digest = MessageDigest.getInstance("SHA-256");
	    digest.reset();
	    byte[] hash = digest.digest(key);		
		return new SecretKeySpec(hash, 0, hash.length, "AES");
	}
	
	private static KeyPair generateKeyPair()
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
	
	public static void generateKey(String filename, char[] passwd)
	throws IOException,
	GeneralSecurityException {
		log.info("Generating EC Key Pair and exporting to [" + filename + "]");
		KeyPair pair = generateKeyPair();
		encryptKey(pair, filename, passwd);
	}
	
	public String getEncodingFormat() {
		return encodingType;
	}
	
	public String getEncodedKey() {
		return encodedKey;
	}

        public byte[] getPublicKey() {
            return keyPair.getPublic().getEncoded();
        }

	public SecretKeySpec deriveKey(ECPublicKey pubkey)
	throws NoSuchAlgorithmException {
		// Extract parameters
		ECParameterSpec spec = pubkey.getParameters();
		ECDomainParameters dp = new ECDomainParameters(spec.getCurve(), spec.getG(), spec.getN(), spec.getH(), spec.getSeed());
		ECPublicKeyParameters pkp = new ECPublicKeyParameters(pubkey.getQ(), dp);
		return deriveKey(pkp);
	}
}
