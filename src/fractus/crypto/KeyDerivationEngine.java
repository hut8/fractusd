package fractus.crypto;

import java.security.PublicKey;

import javax.crypto.spec.SecretKeySpec;

public interface KeyDerivationEngine {
	public SecretKeySpec deriveKey(PublicKey pubkey);
}
