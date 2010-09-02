import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;


public class UserAuthenticator {
	private UserCredentials credentials;
	private Logger log;
	
	public UserAuthenticator(UserCredentials credentials) {
		log = Logger.getLogger(this.getClass().getName());
		this.credentials = credentials;
	}
	
	public Integer authenticate() {
		Connection db = null;
		try {
			db = Database.getConnection();
			PreparedStatement sth = db.prepareStatement("CALL AuthenticateUser_prc(?,?)");			
			sth.setString(1, credentials.getUsername());
			sth.setString(2, credentials.getPassword());
			sth.execute();
			ResultSet authRes = sth.getResultSet();
			
			if (authRes.first()) {
				db.close();
				System.out.println("user authenticated "+credentials.getUsername());
				return 1;
			}
			
		} catch (SQLException e) {
			log.severe("Database error while authenticating user");
		} finally {
			try {
				if (db != null) {
					db.close();
				}
			} catch (SQLException e) { }
		}
		return null;
	}
}
