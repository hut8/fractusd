package fractus.tests;

import static org.junit.Assert.*;

import java.security.Security;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fractus.crypto.Nonce;

public class NonceTest {
	private SecretKey secretKey;
	
	@Before
	public void setUp() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		Nonce.setGenerator(new InsecureRandom());
//		KeyGenerator kg = KeyGenerator.getInstance("AES", "BC");
//		kg.init(256);
//		secretKey = kg.generateKey();
	}

	@Test
	public void testGenerate() {
		// Generate 100 nonces.  This should fail.
		for (int i=0; i < 100; i++) {
			Nonce n = Nonce.generate();
			if (Nonce.isUsed(n)) {
				fail("Generated used nonce");
			}
		}
	}
}
