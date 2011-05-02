package fractus.tests;

import static org.junit.Assert.*;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.provider.asymmetric.ec.KeyPairGenerator;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.junit.Before;
import org.junit.Test;

import fractus.crypto.ClientCipher;
import fractus.crypto.KeyDerivationEngine;
import fractus.main.BinaryUtil;

public class ClientCipherTest {
	private TestKDE aliceKDE;
	private ClientCipher aliceClientCipher;
	
	private TestKDE bobKDE;
	private ClientCipher bobClientCipher;


	@Before
	public void setUp() throws Exception {
		aliceKDE = new TestKDE();
		bobKDE = new TestKDE();
		aliceClientCipher = new ClientCipher(aliceKDE);
		bobClientCipher = new ClientCipher(bobKDE);
	}

	@Test
	public void testClientCipher() {
		assertFalse(this.aliceClientCipher.isInitialized());
		assertFalse(this.bobClientCipher.isInitialized());
	}

	@Test
	public void testNegotiate() {
		aliceClientCipher.negotiate( , remotePublicKey, decryptNonce);
		bobClientCipher.negotiate(keyEncoding, remotePublicKey, decryptNonce);
	}

	@Test
	public void testCiphers() {
		
	}
}
