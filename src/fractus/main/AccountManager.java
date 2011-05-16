package fractus.main;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;
import org.apache.log4j.Logger;

import fractus.domain.AccountData;


public class AccountManager {
	private static AccountManager instance = new AccountManager();
	public static AccountManager getInstance() { return instance; }
	private final static Logger log = Logger.getLogger(AccountManager.class);

	private AccountManager() {	}

	public boolean authenticate(String username, String password)
	throws SQLException {
		AccountData accountData = null;
		try {
			accountData = Database.getInstance().getAccountData(username);
		} catch (SQLException e) {
			log.warn("Could not authenticate due to SQL Error",e);
			throw e;
		}
		if (accountData == null) {
			log.info("No such account: " + username);
			return false;
		}

		byte[] candidatePassword = derivePassword(password, accountData.getSalt());
		if (Arrays.equals(candidatePassword, accountData.getPassword())) {
			log.info("Authenticated " + username);
			return true;
		}
		log.info("Authenticate failed for " + username);
		return false;
	}

	public static byte[] derivePassword(String password, byte[] salt) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			throw new Error(e);
		}
		byte[] passwordEncoded;
		try {
			passwordEncoded = password.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("Lost my UTF-8!",e);
			throw new Error(e);
		}
		digest.update(passwordEncoded, 0, passwordEncoded.length);
		digest.update(salt, 0, salt.length);
		byte[] passwordDigest = digest.digest();
		return passwordDigest;
	}
}

