
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author bowenl2
 */
public class ClientCipher {
    private SecretKeySpec secretKeySpec;
    private Cipher encryptCipher;
    private Cipher decryptCipher;

    public ClientCipher(SecretKeySpec secretKeySpec)
            throws GeneralSecurityException {
            this.secretKeySpec = secretKeySpec;

        encryptCipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        decryptCipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
    }

    public byte[] encrypt(byte[] plaintext) throws IllegalBlockSizeException, BadPaddingException {
        return encryptCipher.doFinal(plaintext);
    }

    public byte[] decrypt(byte[] ciphertext) throws IllegalBlockSizeException, BadPaddingException  {
        return decryptCipher.doFinal(ciphertext);
    }
}
