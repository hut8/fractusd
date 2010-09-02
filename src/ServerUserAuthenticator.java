import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ServerUserAuthenticator {
	private UserCredentials credentials;
	
	public ServerUserAuthenticator(UserCredentials credentials) {
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
			} else {
				return authRes.getInt(1);
			}
			
		} catch (SQLException e) {
			try {
				db.close();
			} catch (SQLException e1) { }
			return null;
		} finally {
			try {
				db.close();
			} catch (SQLException e) { }
		}
		return null;
	}
}
