
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.KeySpec;
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
    private ECPoint remotePublicPoint;
    private ECPublicKey remotePublicKey;
    private SecretKeySpec secretKeySpec;
    private Cipher encryptCipher;
    private Cipher decryptCipher;
    private static Logger log;

    static {
        log = Logger.getLogger(ClientCipher.class.getName());
    }

    public ClientCipher(String keyEncoding, byte[] remotePublicKey, EncryptionManager em)
            throws GeneralSecurityException {
        if (!"X.509".equals(keyEncoding)) {
            log.warn("Could not recognize that key encoding: " + keyEncoding);
            return;
        }

        log.debug("Trying to derive secret key from ours and " + remotePublicKey);

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
        
        // Extract CipherParameters
        this.secretKeySpec = em.deriveKey(this.remotePublicKey);

        encryptCipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        decryptCipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
    }

    public byte[] encrypt(byte[] plaintext) throws IllegalBlockSizeException, BadPaddingException {
        return encryptCipher.doFinal(plaintext);
    }

    public byte[] decrypt(byte[] ciphertext) throws IllegalBlockSizeException, BadPaddingException {
        return decryptCipher.doFinal(ciphertext);
    }
}
