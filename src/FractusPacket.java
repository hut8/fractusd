import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Base64;


public class FractusPacket {
	private SecretKeySpec sks;
	private byte[] message;
	private String encodedPublicKey;
	private Logger log;
	
	/* Each packet sent to one another is this */
	public FractusPacket(byte[] message, SecretKeySpec sks) {
		this();
		this.message = message;
		this.sks = sks;
	}
	
	public FractusPacket(SecretKeySpec sks) {
		this();
		this.sks = sks;
	}
	
	private FractusPacket() {
		log = Logger.getLogger(this.getClass().getName());
	}
	
	public byte[] getMessage() {
		return message;
	}
	
	public String getEncodedKey() {
		return encodedPublicKey;
	}
	
	private static final byte[] serializeInt(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
	}

	
	private static final int deserializeInt(byte [] b) {
        return (b[0] << 24)
                + ((b[1] & 0xFF) << 16)
                + ((b[2] & 0xFF) << 8)
                + (b[3] & 0xFF);
	}

	
	/**
	 * Creates byte stream to be sent directly to socket
	 * @return Encrypted byte array
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 */
	public byte[] serialize()
	throws IOException,
	GeneralSecurityException {
		log.info("encrypting message to send: " + new String(message, "UTF-8"));
		
		// Initialize cipher
		Cipher outCipher = null;
		outCipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
		outCipher.init(Cipher.ENCRYPT_MODE, sks);

		
		// Encrypt with secret key
		byte[] cipherData = null;
		cipherData = outCipher.doFinal(message);


		log.info("Encrypting cipher data of " + cipherData.length + " bytes");
		
		// Wrap everything
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(serializeInt(cipherData.length)); // Prepend length of packet
		baos.write(cipherData);
		return baos.toByteArray();
	}
		
	public void readPacket(InputStream input, String encodedKey, EncryptionManager em)
	throws ProtocolException,
	GeneralSecurityException,
	UnsupportedEncodingException {
		this.encodedPublicKey = encodedKey;
		
		// Read the length
		byte[] msglenBytes = new byte[4];
		int msglen;
		byte[] msgBuff;
		try {
			if (input.read(msglenBytes) != 4) {
				Logger.getAnonymousLogger().log(Level.WARNING, "received incorrect message length size (expected 4)");
				throw new ProtocolException();
			}
			msglen = deserializeInt(msglenBytes);
			log.info("Reading packet of " + msglen + " bytes");
			msgBuff = new byte[msglen];

			// Receive crypto-blob
			input.read(msgBuff);
			Logger.getAnonymousLogger().log(Level.INFO, "received bytes: " + new String(Base64.encode(msgBuff)));
		} catch (IOException e) {
			log.severe("IO exception: " + e.getLocalizedMessage());
			return;
		}
		
		// msgBuff now has an encrypted fractus packet
		message = decryptMessage(msgBuff);
		Logger.getAnonymousLogger().log(Level.INFO, "decrypted as: " + new String(message, "UTF-8"));
	}
	
	private byte[] decryptMessage(byte[] ciphertext)
	throws UnsupportedEncodingException,
	NoSuchAlgorithmException,
	NoSuchPaddingException,
	InvalidKeyException,
	IllegalBlockSizeException,
	BadPaddingException {
		// Create cipher
		Cipher inCipher = null;
		try {
			inCipher = Cipher.getInstance("AES/ECB/PKCS7Padding","BC");
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		inCipher.init(Cipher.DECRYPT_MODE, sks);
		Logger.getAnonymousLogger().log(Level.INFO, "Decrypting ciphertext bytes of length " + ciphertext.length);
		return inCipher.doFinal(ciphertext);
		
	}	
}
