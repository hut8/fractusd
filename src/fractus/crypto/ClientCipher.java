package fractus.crypto;




import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.Logger;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.math.ec.ECPoint;

import fractus.main.BinaryUtil;

public class ClientCipher {

	private boolean initialized;
	public boolean isInitialized() {
		return initialized;
	}

	// Remote peer public data
	private ECPoint remotePublicPoint;
	private ECPublicKey remotePublicKey;

	// Symmetrical cipher
	private SecretKeySpec secretKeySpec;
	private GCMBlockCipher encryptCipher;
	private GCMBlockCipher decryptCipher;
	private Nonce encryptNonce;
	private Nonce decryptNonce;

	private static Logger log;
	private KeyDerivationEngine keyDerivationEngine;

	static {
		log = Logger.getLogger(ClientCipher.class.getName());
	}

	public ClientCipher(KeyDerivationEngine keyDerivationEngine) {
		this.initialized = false;
		this.keyDerivationEngine = keyDerivationEngine;
		this.encryptCipher = new GCMBlockCipher(new AESFastEngine());
		this.decryptCipher = new GCMBlockCipher(new AESFastEngine());
		this.encryptNonce = Nonce.generate(); // Automatically adds Nonce to used pool
		log.debug("ClientCipher constructed (Cipher engines, KDE, Nonce)");
	}

	public Nonce getLocalNonce() {
		return this.encryptNonce;
	}

	public ECPoint getRemotePoint() {
		return this.remotePublicPoint;
	}

	public void negotiate(String keyEncoding, byte[] remotePublicKey, Nonce decryptNonce)
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
			log.warn("Not given a valid EC Public Key!", ex);
			throw new GeneralSecurityException(ex);
		} catch (InvalidKeySpecException ex) {
			log.warn("Not given a valid EC Public Key!", ex);
			throw new GeneralSecurityException(ex);
		}
		this.remotePublicPoint = this.remotePublicKey.getQ();

		// Perform DH
		this.secretKeySpec = keyDerivationEngine.deriveKey(this.remotePublicKey);

		// Deal with remote/decrypt nonce
		this.decryptNonce = decryptNonce;
		if (Nonce.isUsed(this.decryptNonce)) {
			GeneralSecurityException gse = new GeneralSecurityException("Received duplicate Nonce");
			log.warn("Remotely supplied (decryption) Nonce is duplicate!", gse);
			throw gse;
		}

		KeyParameter baseParameter = new KeyParameter(this.secretKeySpec.getEncoded());
		ParametersWithIV encryptParams = new ParametersWithIV(baseParameter, this.encryptNonce.getData());
		ParametersWithIV decryptParams = new ParametersWithIV(baseParameter, this.decryptNonce.getData());

		// Initialize ciphers
		this.encryptCipher.init(true, encryptParams);
		this.decryptCipher.init(false, decryptParams);

		Nonce.record(decryptNonce);
		
		initialized = true;
	}

	public byte[] encrypt(byte[] plaintext)
	throws IllegalStateException, InvalidCipherTextException {
		int outsize = this.encryptCipher.getOutputSize(plaintext.length);
		byte[] cipherTextBuffer = new byte[outsize];
		int offset = encryptCipher.processBytes(plaintext, 0, plaintext.length, cipherTextBuffer, 0);
		offset += encryptCipher.doFinal(cipherTextBuffer, offset);
		log.debug("Processed " + plaintext.length + " PT bytes into " + offset + " CT bytes");
		return cipherTextBuffer;
	}

	public byte[] decrypt(byte[] ciphertext)
	throws IllegalStateException, InvalidCipherTextException {
		int outsize = this.decryptCipher.getOutputSize(ciphertext.length);
		byte[] plainTextBuffer = new byte[outsize];
		int offset = decryptCipher.processBytes(ciphertext, 0, ciphertext.length, plainTextBuffer, 0);
		offset += encryptCipher.doFinal(plainTextBuffer, offset);
		log.debug("Processed " + ciphertext.length + " CT bytes into " + offset + " PT bytes");
		return plainTextBuffer;
	}
}
