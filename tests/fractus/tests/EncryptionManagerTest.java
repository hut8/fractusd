package fractus.tests;

import static org.junit.Assert.*;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.asymmetric.ec.KeyPairGenerator;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.junit.Before;
import org.junit.Test;

import fractus.crypto.EncryptionManager;

public class EncryptionManagerTest {
	private EncryptionManager aliceEncryptionManager;
	private EncryptionManager bobEncryptionManager;
	
	@Before
	public void setUp() throws Exception {
		KeyPairGenerator kpg = new KeyPairGenerator.ECDH();
		kpg.initialize(ECNamedCurveTable.getParameterSpec("secp521r1"));
		
		aliceEncryptionManager = new EncryptionManager(kpg.generateKeyPair());
		bobEncryptionManager = new EncryptionManager(kpg.generateKeyPair());
	}

	@Test
	public void testDeriveKey()
	throws NoSuchAlgorithmException {
		SecretKeySpec aliceSecret =
			aliceEncryptionManager.deriveKey(bobEncryptionManager.getPublicKey());
		SecretKeySpec bobSecret =
			bobEncryptionManager.deriveKey(aliceEncryptionManager.getPublicKey());
		
		byte[] aliceKM = aliceSecret.getEncoded();
		byte[] bobKM = bobSecret.getEncoded();
		
		assertSame("Alice's key is 256 bits", 256/8, aliceKM.length);
		assertSame("Bob's key is 256 bits", 256/8, bobKM.length);
	
		assertArrayEquals("Derived secret material matches", aliceKM, bobKM);
		
	}

}
