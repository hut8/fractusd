package fractus.main;




import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.math.ec.ECPoint;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author bowenl2
 */
public class ClientCipher {

    private boolean initialized;

    /**
     * Get the value of initialized
     *
     * @return the value of initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Set the value of initialized
     *
     * @param initialized new value of initialized
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    private ECPoint remotePublicPoint;
    private ECPublicKey remotePublicKey;
    private SecretKeySpec secretKeySpec;
    private Cipher encryptCipher;
    private Cipher decryptCipher;
    private static Logger log;
    private EncryptionManager encryptionManager;

    static {
        log = Logger.getLogger(ClientCipher.class.getName());
    }

    public ClientCipher(EncryptionManager encryptionManager) {
        this.initialized = false;
        this.encryptionManager = encryptionManager;
    }

    public void negotiate(String keyEncoding, byte[] remotePublicKey)
    throws GeneralSecurityException {
                if (!"X.509".equals(keyEncoding)) {
            log.warn("Could not recognize that key encoding [Not X.509]: " + keyEncoding);
            throw new GeneralSecurityException("Key not X.509 (Unsupported)");
        }

        log.debug("Trying to derive secret key from ours and " + BinaryUtil.encodeData(remotePublicKey));

        // Create their public key object for ECDH
        X509EncodedKeySpec ks = new X509EncodedKeySpec(remotePublicKey);
        KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
        try {
            this.remotePublicKey = (ECPublicKey)kf.generatePublic(ks);
        } catch (ClassCastException ex) {
            log.warn("Not given an EC Public Key!", ex);
            return;
        }

        this.remotePublicPoint = this.remotePublicKey.getQ();
        log.debug("Remote ECDH Public Key Q Value: \nX:" +
                remotePublicPoint.getX().toString() + "\n" +
                remotePublicPoint.getY().toString());

        // Extract CipherParameters
        this.secretKeySpec = encryptionManager.deriveKey(this.remotePublicKey);

        encryptCipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        decryptCipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        initialized = true;
    }

    public byte[] encrypt(byte[] plaintext) throws IllegalBlockSizeException, BadPaddingException {
        return encryptCipher.doFinal(plaintext);
    }

    public byte[] decrypt(byte[] ciphertext) throws IllegalBlockSizeException, BadPaddingException {
        return decryptCipher.doFinal(ciphertext);
    }
}
