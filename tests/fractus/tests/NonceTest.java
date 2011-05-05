package fractus.tests;

import static org.junit.Assert.*;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.Security;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fractus.crypto.Nonce;
import fractus.main.BinaryUtil;

public class NonceTest {
	
	@Before
	public void setUp() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
	}

	@Test(expected = RuntimeCryptoException.class)
	public void testGenerateWeakRNG() {
		Nonce.setGenerator(new InsecureRandom());
		// Generate 1000 nonces.
		for (int i=0; i < 1000; i++) {
			Nonce n = Nonce.generate();
			assertEquals(n.getData().length, Nonce.NONCE_BITS/8);
		}
	}
	
	@Test
	public void testAddAndCheckDuplicate() {
		Nonce n = new Nonce(Hex.decode("5C23AC9A7162E177FDF2602C"));
		assertTrue(Nonce.record(n));
		assertTrue(Nonce.isUsed(n));
	}
	
	@Test
	public void testGenerate() {
		Nonce.setGenerator(new SecureRandom());
		// Generate 1000 nonces.
		for (int i=0; i < 1000; i++) {
			Nonce n = Nonce.generate();
			assertTrue(Nonce.record(n));
			assertTrue(Nonce.isUsed(n));
			assertEquals(n.getData().length, Nonce.NONCE_BITS/8);
		}
	}
	
	@After
	public void tearDown() {
		Nonce.setGenerator(new SecureRandom());
	}
}
