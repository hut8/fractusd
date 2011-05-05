package fractus.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import fractus.domain.User;
import fractus.main.UserTracker.ModifyContactResponse;

/**
 * Database connector.
 * Pools connections like a boss.
 * Use only subclasses directly.
 * @author bowenl2
 *
 */
public class Database {
	// Statics and Initializer
	public static String url;
	private final static Logger log;
	private static BoneCPConfig poolConfig;
	private static BoneCP connectionPool;

	static {
		log = Logger.getLogger(Database.class.getName());
		url = "jdbc:mysql://localhost:3306/fractus";

		log.info("Loading MySQL JDBC driver...");

		try {
			Class.forName("com.mysql.jdbc.Driver");
			log.info("MySQL JDBC driver loaded");
		} catch (Exception e) {
			log.fatal("Fatal error: could not load Database driver", e);
			throw new RuntimeException("Database driver could not be loaded.");
		}

		// Connection pool
		poolConfig.setJdbcUrl(url);
		poolConfig.setUsername("fractus"); 
		poolConfig.setPassword("");
		poolConfig.setMinConnectionsPerPartition(5);
		poolConfig.setMaxConnectionsPerPartition(10);
		poolConfig.setPartitionCount(1);

		log.info("Establishing connection pool...");
		try {
			connectionPool = new BoneCP(poolConfig);
			log.info("Connection pool established");
		} catch (SQLException e) {
			log.error("Could not create connection pool",e);
			throw new RuntimeException("Fatal error: connection pool broken");
		}
		
	}

	public static class Authentication {
		public static boolean authenticate(UserCredentials credentials)
		throws SQLException {
			Connection conn = connectionPool.getConnection();
			PreparedStatement sth = null;
			try {
				sth = conn.prepareStatement("CALL AuthenticateUser_prc(?,?)");
				sth.setString(1, credentials.getUsername());
				sth.setString(2, credentials.getPassword());
				sth.execute();
				ResultSet authRes = sth.getResultSet();

				if (authRes.first()) {
					log.debug("User authenticated passed: " + credentials.getUsername());
					return true;
				} else {
					log.debug("User authenticated failed: " + credentials.getUsername());
					return false;
				}
			} finally {
				if (sth != null)
					sth.close();
			}
		}
		
		
	}
	
	public static class UserTracker {
		public static fractus.main.UserTracker.ModifyContactResponse
		addContact(String sourceUsername, String destinationUsername)
		throws SQLException {
			log.debug("Trying to add " + destinationUsername + " as a contact of " + sourceUsername);
			Connection conn = connectionPool.getConnection();
			PreparedStatement sth = null;
			try {
				sth = conn.prepareStatement("CALL AddContact_prc(?,?)");
				sth.setString(1, sourceUsername);
				sth.setString(2, destinationUsername);
				sth.execute();
				ResultSet authRes = sth.getResultSet();
				if (!authRes.first()) {
					throw new SQLException("Invalid result (nothing came back)");
				}
				return authRes.getBoolean("SUCCESS") ?
						ModifyContactResponse.SUCCESS : ModifyContactResponse.REDUNDANT; 
			} finally {
				if (sth != null)
					sth.close();
			}
		}
		
		public static ModifyContactResponse removeContact(String sourceUsername, String destinationUsername)
		throws SQLException {
			log.debug("Trying to add " + destinationUsername + " as a contact of " + sourceUsername);
			Connection conn = connectionPool.getConnection();
			PreparedStatement sth = null;
			try {
				sth = conn.prepareStatement("CALL DeleteContact_prc(?,?)");
				sth.setString(1, sourceUsername);
				sth.setString(2, destinationUsername);
				int rowcount = sth.executeUpdate();
				return rowcount > 0 ?
						ModifyContactResponse.SUCCESS : ModifyContactResponse.REDUNDANT; 
			} finally {
				if (sth != null)
					sth.close();
			}
		}
		
		public static boolean confirmContact(String sourceUsername, String destinationUsername)
		throws SQLException {
			log.debug("Confirming that " + destinationUsername + " is a contact of " + sourceUsername);
			Connection conn = connectionPool.getConnection();
			PreparedStatement sth = null;
			try {
				sth = conn.prepareStatement("CALL VerifyContact_prc(?,?)");
				sth.setString(1, sourceUsername);
				sth.setString(2, destinationUsername);
				sth.execute();
				ResultSet authRes = sth.getResultSet();
				if (!authRes.first()) {
					throw new SQLException("Invalid result (nothing came back)");
				}
				return authRes.getBoolean("CONTACT");
			} finally {
				if (sth != null)
					sth.close();
			}
		}
		
		public static List<User> listNonreciprocalContacts(String username) {
			return new ArrayList<User>();
		}
	}
}
