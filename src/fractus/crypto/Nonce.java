package fractus.crypto;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.bouncycastle.crypto.RuntimeCryptoException;

import fractus.main.BinaryUtil;

public class Nonce {
	// Static
	public final static int NONCE_BITS = 96;
	private static Random generator;
	private static Set<Nonce> usedNonces;
	private static Logger log;
	static {
		log = Logger.getLogger(Nonce.class);
		generator = new SecureRandom();
		usedNonces = new HashSet<Nonce>();
	}
	public static void setGenerator(Random generator) {
		Nonce.generator = generator; 
	}

	public synchronized static Nonce generate() {
		Nonce nonce = null;
		boolean found = false;
		// Try to generate an unused Nonce
		for(int i=0; i < 65536; i++) {
			byte[] nonceData = new byte[NONCE_BITS/8];
			generator.nextBytes(nonceData);
			nonce = new Nonce(nonceData);
			if (!usedNonces.contains(nonce)) {
				found = true;
				break;
			}
		}
		if (!found) {
			log.error("RNG Failure");
			throw new RuntimeCryptoException(
					"Cannot generate unused Nonce: Random Number Generator is broken!");
		}
		Nonce.usedNonces.add(nonce);
		return nonce;
	}
	
	public synchronized static boolean record(Nonce nonce) {
		return usedNonces.add(nonce);
	}
	
	public synchronized static boolean isUsed(Nonce nonce) {
		return usedNonces.contains(nonce);
	}

	// Instance
	private byte[] data;
	public byte[] getData() {
		return data;
	}
	private SecretKey secretKey;
	private Integer hashCode;

	public Nonce(byte[] data) {
		if (data == null || secretKey == null || secretKey.getEncoded() == null) {
			throw new UnsupportedOperationException("Null secret or data cannot be used");
		}
		if (data.length != Nonce.NONCE_BITS/8) {
			throw new IllegalArgumentException(
					"Incorrect data size (" + data.length + ", expected " + (Nonce.NONCE_BITS/8) + ")");
		}
		this.data = data;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Nonce)) {
			return false;
		}
		Nonce other = (Nonce)obj;
		if (other.data == null ||
				other.data.length != this.data.length) {
			return false;
		}
		for (int i=0; i < this.data.length; i++) {
			if (this.data[i] != other.data[i]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Takes first four bytes of Nonce data and interprets them as an integer
	 * Perfect for an acutal random source.
	 */
	@Override
	public int hashCode() {
		if (this.hashCode == null) {
			// Return the first 32 bits of Nonce as an integer
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
			try {
				this.hashCode = dis.readInt();
			} catch (Exception e) {
				// It's not even four bytes so nobody cares
				this.hashCode = 0;
			}
		}
		return this.hashCode;
	}

	@Override
	public String toString() {
		return BinaryUtil.encodeData(data);
	}


	
	
}
