package fractus.main;




import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.Logger;
import org.bouncycastle.crypto.agreement.kdf.ECDHKEKGenerator;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.math.ec.ECPoint;

import fractus.crypto.EncryptionManager;

public class ClientCipher {

	private boolean initialized;

	public boolean isInitialized() {
		return initialized;
	}
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	// Remote peer public data
	private ECPoint remotePublicPoint;
	private ECPublicKey remotePublicKey;
	
	// Symmetrical cipher
	private SecretKeySpec secretKeySpec;
	private GCMBlockCipher encryptCipher;
	private GCMBlockCipher decryptCipher;
	private byte[] IV;
	
	private static Logger log;
	private EncryptionManager encryptionManager;

	static {
		log = Logger.getLogger(ClientCipher.class.getName());
	}

	public ClientCipher(EncryptionManager encryptionManager) {
		this.initialized = false;
		this.encryptionManager = encryptionManager;
		this.encryptCipher = new GCMBlockCipher(new AESFastEngine());
		this.decryptCipher = new GCMBlockCipher(new AESFastEngine());
		this.IV = new byte[96/8];
	}
	
	public void initialize() {
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(this.IV);
	}
	
	public byte[] getIV() {
		return this.IV;
	}

	public ECPoint getRemotePoint() {
		return this.remotePublicPoint;
	}
	
	public void negotiate(String keyEncoding, byte[] remotePublicKey, byte[] remoteIV)
	throws GeneralSecurityException {
		if (!"X.509".equals(keyEncoding)) {
			log.warn("Could not recognize that key encoding [Not X.509]: " + keyEncoding);
			throw new GeneralSecurityException("Key not X.509 (Unsupported)");
		}

		log.debug("Trying to derive secret key from ours and " + BinaryUtil.encodeData(remotePublicKey));
		
		// Create their public key, public point object for ECDH
		X509EncodedKeySpec ks = new X509EncodedKeySpec(remotePublicKey);
		KeyFactory keyFactory = KeyFactory.getInstance("ECDH", "BC");
		try {
			this.remotePublicKey = (ECPublicKey)keyFactory.generatePublic(ks);
		} catch (ClassCastException ex) {
			log.warn("Not given an EC Public Key!", ex);
			return;
		}
		this.remotePublicPoint = this.remotePublicKey.getQ();
		
		this.secretKeySpec = encryptionManager.deriveKey(this.remotePublicKey);
		
		ParametersWithIV encryptParams = new ParametersWithIV(null, this.IV);
		ParametersWithIV decryptParams = new ParametersWithIV(null, remoteIV);
		
		// Initialize ciphers
		
		this.encryptCipher.init(true, encryptParams);
		this.decryptCipher.init(false, decryptParams);
		
		initialized = true;
	}

	public byte[] encrypt(byte[] plaintext) throws IllegalBlockSizeException, BadPaddingException {
		// TODO
		return null;
	}

	public byte[] decrypt(byte[] ciphertext) throws IllegalBlockSizeException, BadPaddingException {
		// TODO
		return null;
	}
}
