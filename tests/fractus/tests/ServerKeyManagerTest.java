package fractus.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fractus.crypto.ServerKeyManager;

public class ServerKeyManagerTest {

	private static KeyPair ecdhKeyPair;
	private static final char[] testPassword = "HELLO WORLD!".toCharArray();
	private static TemporaryFolder tempFolder;
	private static File keyFile; 
	
	@BeforeClass
	public static void testGenerateKey()
	throws IOException {
		Security.addProvider(new BouncyCastleProvider());
		
		try {
			ecdhKeyPair = ServerKeyManager.generateKey();
		} catch (GeneralSecurityException e) {
			fail("Got exception while trying to generate key: " + e);
		}
		tempFolder = new TemporaryFolder();
		keyFile = tempFolder.newFile("encrypted_pkcs8_key.dat");
	}

	@Test
	public void testEncryptKey() {
		try {
			ServerKeyManager.encryptKey(ecdhKeyPair.getPrivate(),
					testPassword, keyFile.getAbsolutePath());
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	@Test
	public void testDecryptKey()
	throws Exception {
		KeyPair testKeyPair = 
			ServerKeyManager.decryptKey(testPassword, keyFile.getAbsolutePath());
		assertEquals("Private keys do not match", ecdhKeyPair.getPrivate(), testKeyPair.getPrivate());
		assertEquals("Public keys do not match", ecdhKeyPair.getPublic(), testKeyPair.getPublic());
	}

	@AfterClass
	public static void tearDown() {
		keyFile.delete();
	}
}
