import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



public class UserData {
	private String username;
	private Integer userId;
	
	public String toString() {
		return username + " [" + userId + "]";
	}

	public UserData(String username)
	throws UnknownUserException, SQLException {
		Logger.getAnonymousLogger().log(Level.INFO, "retrieving UserId of '" + username + "'");
		this.username = username;
		// Verify that user exists
		Connection db = Database.getConnection();
		PreparedStatement sth = db.prepareStatement("CALL VerifyUser_prc(?)");			
		sth.setString(1, username);
		sth.execute();
		ResultSet authRes = sth.getResultSet();
		if (authRes.first()) {
			userId = authRes.getInt(1);
		} else {
			db.close();
			throw new UnknownUserException();
		}
		db.close();
		Logger.getAnonymousLogger().log(Level.INFO, "created object: " + this.toString());
	}

	public UserData(String username, Integer userId) {
		this.username = username;
		this.userId = userId;
	}

	public Integer getUserId() {
		return userId;
	}

	public List<UserLocation> locate() {
		ArrayList<UserLocation> locations = new ArrayList<UserLocation>();
		// Connect to the database
		Connection db = null;
		try {
			db = Database.getConnection();
			db.prepareCall("");
		} catch (SQLException e) {
			try {
				db.close();
			} catch (SQLException e1) { }
			return null;
		}
		return locations;
	}

	public boolean addLocation(UserLocation location) {
		Connection db = null;
		int rows = 0;
		try {
			db = Database.getConnection();
			PreparedStatement sth = db.prepareStatement("CALL RecordUserLocation_prc(?,?,?)");			
			sth.setInt(1, userId);
			sth.setString(2, location.getAddress());
			sth.setInt(3, location.getPort());
			rows = sth.executeUpdate();
		} catch (SQLException e) {
			return false;
		}
		finally {
			try {
				db.close();
			} catch (SQLException e1) { }
		}
		return rows == 1;
	}

	/**
	 * Adds the destination user as a contact of the source
	 * @param destination
	 * @param fc
	 * @return true if now a contact pair, false if now nonreciprocal contact exists
	 * @throws DuplicateAddRequestException 
	 */
	public boolean addContact(UserData destination, ClientConnector fc)
	throws DuplicateAddRequestException {
		Logger.getAnonymousLogger().log(Level.INFO, "adding contact from " + this.toString() +" to " + destination.toString());
		Connection db = null;
		int recipRowCount = 0;
		try {
			db = Database.getConnection();
			PreparedStatement sth = db.prepareStatement("CALL AddContact_prc(?,?)");			
			sth.setInt(1, userId);
			sth.setInt(2, destination.getUserId());
			ResultSet rs = sth.executeQuery();
			if (rs.next()) {
				recipRowCount = rs.getInt(0); 
				Logger.getAnonymousLogger().log(Level.INFO, "got reciprocal row count: " + recipRowCount);
			} else {
				Logger.getAnonymousLogger().log(Level.INFO, "confused - got no rows for reciprocal contact count!");
			}
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return recipRowCount == 1;
	}

	public boolean removeContact(UserData destination, ClientConnector fc) {
		Connection db = null;
		int rows = 0;
		try {
			db = Database.getConnection();
			PreparedStatement sth = db.prepareStatement("CALL RemoveContact_prc(?,?)");			
			sth.setInt(1, userId);
			sth.setInt(2, destination.getUserId());
			rows = sth.executeUpdate();
		} catch (SQLException e) {

			return false;
		}
		finally {
			try {
				db.close();
			} catch (SQLException e) { }
		}
		return rows > 0;
	}
}
