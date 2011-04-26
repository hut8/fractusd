package fractus.crypto;


import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.Logger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;

public class EncryptionManager {
	public final static String ELLIPTIC_CURVE = "secp521r1";
	private static Logger log;
	private KeyPair keyPair;
	private String encodingType;
	ECDHBasicAgreement agreement;

	static {
		log = Logger.getLogger(EncryptionManager.class.getName());
	}

	public EncryptionManager(KeyPair keyPair)
	throws IOException,
	GeneralSecurityException,
	ClassNotFoundException {
		this.keyPair = keyPair;
		encodingType = keyPair.getPublic().getFormat();

		// Reference Private Key
		ECPrivateKey privKey = (ECPrivateKey) keyPair.getPrivate();
		log.info("Constructed private key object from key in format: " + encodingType);

		log.info("Initializing ECDH Agreement Engine");
		agreement = new ECDHBasicAgreement();
		ECParameterSpec spec = privKey.getParameters();
		ECDomainParameters dp = new ECDomainParameters(spec.getCurve(), spec.getG(), spec.getN(), spec.getH(), spec.getSeed());
		ECPrivateKeyParameters pkp = new ECPrivateKeyParameters(privKey.getD(), dp);
		agreement.init(pkp);
		log.info("ECDH Agreement Engine initialized");
	}

	public String getEncodingFormat() {
		return encodingType;
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
}
