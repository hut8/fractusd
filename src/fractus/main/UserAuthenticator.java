package fractus.main;

import java.sql.SQLException;
import org.apache.log4j.Logger;


public class UserAuthenticator {
	private static Logger log = Logger.getLogger(UserAuthenticator.class.getName());
	
	public UserAuthenticator() { }

	public static boolean authenticate(UserCredentials credentials) throws SQLException {
            return Database.authenticate(credentials);
	}
}
