package fractus.crypto;


import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.agreement.kdf.DHKDFParameters;
import org.bouncycastle.crypto.agreement.kdf.ECDHKEKGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
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
	GeneralSecurityException {
		this.keyPair = keyPair;
		encodingType = keyPair.getPublic().getFormat();

		// Reference Private Key
		ECPrivateKey privKey = (ECPrivateKey) keyPair.getPrivate();

		agreement = new ECDHBasicAgreement();
		ECParameterSpec spec = privKey.getParameters();
		ECDomainParameters dp = new ECDomainParameters(spec.getCurve(), spec.getG(), spec.getN(),
				spec.getH(), spec.getSeed());
		ECPrivateKeyParameters pkp = new ECPrivateKeyParameters(privKey.getD(), dp);
		agreement.init(pkp);
	}

	public String getEncodingFormat() {
		return encodingType;
	}

	public ECPublicKey getPublicKey() {
		return (ECPublicKey)keyPair.getPublic();
	}
	
	public byte[] getEncodedPublicKey() {
		return keyPair.getPublic().getEncoded();
	}

	public SecretKeySpec deriveKey(ECPublicKey pubkey)
	throws NoSuchAlgorithmException {
		ECParameterSpec parameterSpec = pubkey.getParameters();
		ECDomainParameters publicDomainParameters = new ECDomainParameters(
				parameterSpec.getCurve(), parameterSpec.getG(), parameterSpec.getN(),
				parameterSpec.getH(), parameterSpec.getSeed());
		ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(pubkey.getQ(),
				publicDomainParameters);
		BigInteger sharedSecret = agreement.calculateAgreement(publicKeyParameters);
		
		ECDHKEKGenerator kdf = new ECDHKEKGenerator(new SHA256Digest());
		DERObjectIdentifier kdfObjectIdentifier = NISTObjectIdentifiers.id_aes256_GCM;
		DHKDFParameters dhParameters = new DHKDFParameters(kdfObjectIdentifier, 256, sharedSecret.toByteArray());
		kdf.init(dhParameters);
		byte[] keymaterial = new byte[256/8];
		
		kdf.generateBytes(keymaterial, 0, keymaterial.length);
		return new SecretKeySpec(keymaterial, 0, keymaterial.length, "AES");
	}
}
