package fractus.tests;

import static org.junit.Assert.*;

import java.security.GeneralSecurityException;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fractus.crypto.ClientCipher;
import fractus.crypto.Nonce;

public class ClientCipherTest {
	private TestKDE aliceKDE;
	private Nonce aliceNonce;
	private ClientCipher aliceClientCipher;
	
	private TestKDE bobKDE;
	private Nonce bobNonce;
	private ClientCipher bobClientCipher;

	@Before
	public void setUp()
	throws Exception {
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
	public void testNegotiate()
	throws GeneralSecurityException {
		aliceNonce = new Nonce(Hex.decode("CFC6A23EBBA0F4DD4F2C8BE1"));
		bobNonce = new Nonce(Hex.decode("85C23AC9A7162E177EDF2602"));
		
		// Typically, the generated nonces would be added to the "used" set
		// but because it's static, tests would fail because negotiation method queries
		// the same set for membership of encrypt and decrypt nonces.
		
		aliceClientCipher.negotiate(
				bobKDE.getLocalPublicKey().getFormat(),
				bobKDE.getLocalPublicKey().getEncoded(),
				bobNonce);
		bobClientCipher.negotiate(
				aliceKDE.getLocalPublicKey().getFormat(),
				aliceKDE.getLocalPublicKey().getEncoded(),
				aliceNonce);
		
		assertTrue("Ciphers are not initialized",
				aliceClientCipher.isInitialized() && bobClientCipher.isInitialized());
		
	}

	@Test
	@Ignore
	public void testCiphers() {
		
		
	}
}
