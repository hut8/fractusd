package fractus.tests;

import static org.junit.Assert.*;

import java.security.GeneralSecurityException;
import java.security.Security;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fractus.crypto.ClientCipher;
import fractus.crypto.Nonce;

public class ClientCipherTest {
	private TestKDE aliceKDE;
	private ClientCipher aliceClientCipher;

	private TestKDE bobKDE;
	private ClientCipher bobClientCipher;
	
	@BeforeClass
	public static void setUpClass() {
		Security.addProvider(new BouncyCastleProvider());
	}

	@Before
	public void setUp()
	throws Exception {
		aliceKDE = new TestKDE();
		bobKDE = new TestKDE();

		aliceClientCipher = new ClientCipher(aliceKDE);
		bobClientCipher = new ClientCipher(bobKDE);
	}

	@After
	public void tearDown() {
		Nonce.resetPool();
	}

	@Test
	public void testClientCipher() {
		assertFalse(this.aliceClientCipher.isInitialized());
		assertFalse(this.bobClientCipher.isInitialized());
	}

	private void negotiate()
	throws GeneralSecurityException {
		aliceClientCipher.negotiate(
				bobKDE.getLocalPublicKey().getFormat(),
				bobKDE.getLocalPublicKey().getEncoded(),
				bobClientCipher.getLocalNonce());
		bobClientCipher.negotiate(
				aliceKDE.getLocalPublicKey().getFormat(),
				aliceKDE.getLocalPublicKey().getEncoded(),
				aliceClientCipher.getLocalNonce());
	}

	@Test
	public void testNegotiate()
	throws GeneralSecurityException {
		negotiate();

		assertTrue("Ciphers are not initialized",
				aliceClientCipher.isInitialized() && bobClientCipher.isInitialized());

		assertArrayEquals("Shared secret is different for Alice and Bob",
				aliceClientCipher.getSecret().getEncoded(),
				bobClientCipher.getSecret().getEncoded()
		);
	}

	@Test
	public void testCiphers()
	throws GeneralSecurityException,
	IllegalStateException,
	InvalidCipherTextException {
		negotiate();
		testCipher("‽※☞");
		testCipher("Machines take me by surprise with great frequency.");
		testCipher("We can only see a short distance ahead, but we can see plenty there that needs to be done");
	}
	
	public void testCipher(String secret)
	throws IllegalStateException, InvalidCipherTextException {

		// Generate ciphertexts
		byte[] aliceCipherText = aliceClientCipher.encrypt(secret.getBytes());
		byte[] bobCipherText = bobClientCipher.encrypt(secret.getBytes());
		assertEquals(aliceCipherText.length, bobCipherText.length);

		// [This is where you'd exchange the ciphertexts IRL]
		// Alice's given to Bob for use in bobCipherText and vice versa

		// Same ciphertext would mean that the IVs are the same (very bad)
		boolean sameCipherTexts = true;
		for (int i=0; i < aliceCipherText.length; i++) {
			if (aliceCipherText[i] != bobCipherText[i]) {
				sameCipherTexts = false;
				break;
			}
		}
		assertFalse("Cipher Texts are identical; IV scheme defeated.", sameCipherTexts);

		// Decrypt the other person's ciphertext
		byte[] bobPlainText = bobClientCipher.decrypt(aliceCipherText);
		byte[] alicePlainText = aliceClientCipher.decrypt(bobCipherText);

		assertArrayEquals(bobPlainText, alicePlainText);
	}

	@Test(expected = InvalidCipherTextException.class)
	public void testDetectTamper()
	throws IllegalStateException,
	InvalidCipherTextException,
	GeneralSecurityException {
		negotiate();
		String secret = "¡Aye Carumba my Ciphertext broke!";
		
		// Generate ciphertexts
		byte[] aliceCipherText = aliceClientCipher.encrypt(secret.getBytes());
		byte[] bobCipherText = bobClientCipher.encrypt(secret.getBytes());
		assertEquals(aliceCipherText.length, bobCipherText.length);

		// [This is where you'd exchange the ciphertexts IRL]
		// Alice's given to Bob for use in bobCipherText and vice versa

		// MESS WITH IT
		int messWithItIndex = aliceCipherText.length - 5;
		aliceCipherText[messWithItIndex] &= 0xaa;
		messWithItIndex++;
		aliceCipherText[messWithItIndex] &= 0x04;
		messWithItIndex++;

		bobCipherText[messWithItIndex] &= 0xaa;
		messWithItIndex++;
		bobCipherText[messWithItIndex] &= 0x04;
		messWithItIndex++;

		// Same ciphertext would mean that the IVs are the same (very bad)
		boolean sameCipherTexts = true;
		for (int i=0; i < aliceCipherText.length; i++) {
			if (aliceCipherText[i] != bobCipherText[i]) {
				sameCipherTexts = false;
				break;
			}
		}
		assertFalse("Cipher Texts are identical; IV scheme defeated.", sameCipherTexts);

		// Decrypt the other person's ciphertext
		byte[] bobPlainText = bobClientCipher.decrypt(aliceCipherText);
		byte[] alicePlainText = aliceClientCipher.decrypt(bobCipherText);

		assertArrayEquals(bobPlainText, alicePlainText);
	}
}
