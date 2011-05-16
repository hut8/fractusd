package fractus.main;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import fractus.domain.Location;
import fractus.domain.Location.InvalidLocationException;
import fractus.main.UserTracker.ContactOperationResponse;
import fractus.domain.AccountData;

/**
 * Database connector.
 * Pools connections like a boss.
 * @author bowenl2
 *
 */
public class Database {
	private final static Database instance;
	private Database() { }
	public static Database getInstance() {
		return instance;
	}
	
	private final static String url;
	private final static Logger log;
	static {
		log = Logger.getLogger(Database.class.getName());
		url = "jdbc:mysql://localhost:3306/fractus";
		instance = new Database();
	}
	
	private BoneCPConfig poolConfig;
	private BoneCP connectionPool;

	public void initialize() {
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
		poolConfig.setPassword("fractus");
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
	
	public void shutdown() {
		connectionPool.close();
	}
	
	// Contact DB methods
	public ContactOperationResponse	addContact(String sourceUsername, String destinationUsername)
	throws SQLException {
		log.debug("Trying to add " + destinationUsername + " as a contact of " + sourceUsername);
		Connection conn = connectionPool.getConnection();
		PreparedStatement sth = null;
		try {
			sth = conn.prepareStatement("CALL AddContact_prc(?,?)");
			sth.setString(1, sourceUsername);
			sth.setString(2, destinationUsername);
			int rowsAffected = sth.executeUpdate();
			return rowsAffected > 0 ?
					ContactOperationResponse.SUCCESS : ContactOperationResponse.REDUNDANT; 
		} finally {
			if (sth != null)
				sth.close();
		}
	}

	public ContactOperationResponse removeContact(String sourceUsername, String destinationUsername)
	throws SQLException {
		log.debug("Trying to remove " + destinationUsername + " as a contact of " + sourceUsername);
		Connection conn = connectionPool.getConnection();
		PreparedStatement sth = null;
		try {
			sth = conn.prepareStatement("CALL DeleteContact_prc(?,?)");
			sth.setString(1, sourceUsername);
			sth.setString(2, destinationUsername);
			int rowcount = sth.executeUpdate();
			return rowcount > 0 ?
					ContactOperationResponse.SUCCESS : ContactOperationResponse.REDUNDANT; 
		} finally {
			if (sth != null)
				sth.close();
		}
	}

	public boolean verifyContact(String sourceUsername, String destinationUsername)
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

	public Set<String> listNonreciprocalContacts(String username)
	throws SQLException {
		log.debug("Finding nonreciprocal contacts of " + username);
		Set<String> contacts = new HashSet<String>();
		Connection conn = connectionPool.getConnection();
		PreparedStatement sth = null;
		try {
			sth = conn.prepareStatement("CALL FindNonreciprocalContacts_prc(?)");
			sth.setString(1, username);
			sth.execute();
			ResultSet authRes = sth.getResultSet();
			while (authRes.next()) {
				contacts.add(authRes.getString("USERNAME"));
			}
		} finally {
			if (sth != null)
				sth.close();
		}
		return contacts;
	}

	// Authentication / User Management
	public AccountData getAccountData(String username)
	throws SQLException {
		log.debug("Trying to retrieve account data for " + username);
		Connection conn = connectionPool.getConnection();
		PreparedStatement sth = null;
		try {
			sth = conn.prepareStatement("CALL GetAccountData_prc(?)");
			sth.setString(1, username);
			ResultSet accountDataRS = sth.executeQuery();
			if (!accountDataRS.next()) {
				log.debug("Username " + username + " does not exist");
				return null;
			}
			AccountData accountData = new AccountData();
			accountData.setUsername(username);
			accountData.setEmail(accountDataRS.getString("EMAIL"));
			accountData.setCreationDate(accountDataRS.getDate("REGISTEREDTIME"));
			accountData.setPassword(accountDataRS.getBytes("PASSWORD"));
			accountData.setSalt(accountDataRS.getBytes("SALT"));
			return accountData;
		} finally {
			if (sth != null)
				sth.close();
			conn.close();
		}
	}	
	
	
	

	// Location
	
	public boolean registerLocation(String username, InetAddress address, short port)
	throws SQLException {
		// (Username VARCHAR(32), Address BINARY(4), Port SMALLINT)
		log.debug("Registering location " + address.getHostAddress()
				+ ":" + port + " to " + username);
		Connection conn = connectionPool.getConnection();
		PreparedStatement sth = null;
		try {
			sth = conn.prepareStatement("CALL RecordUserLocation_prc(?,?,?)");
			sth.setString(1, username);
			sth.setBytes(2, address.getAddress());
			sth.setShort(3, port);
			int rowcount = sth.executeUpdate();
			return rowcount > 0; 
		} finally {
			if (sth != null)
				sth.close();
		}		
	}
	
	public boolean invalidateLocation(String username, InetAddress address, short port)
	throws SQLException {
		log.debug("Invalidating location" + address.getHostAddress() +
				":" + port + " from " + username);
		Connection conn = connectionPool.getConnection();
		PreparedStatement sth = null;
		try {
			sth = conn.prepareStatement("CALL InvalidateUserLocation_prc(?,?,?)");
			sth.setString(1, username);
			sth.setBytes(2, address.getAddress());
			sth.setInt(3, port);
			int rowcount = sth.executeUpdate();
			return rowcount > 0; 
		} finally {
			if (sth != null)
				sth.close();
		}

	}
	
	public Set<Location> locateUser(String username)
	throws SQLException {
		Set<Location> locations = new HashSet<Location>();
		log.debug("Trying to locate " + username);
		Connection conn = connectionPool.getConnection();
		PreparedStatement sth = null;
		try {
			sth = conn.prepareStatement("CALL LocateUser_prc(?)");
			sth.setString(1, username);
			ResultSet locationSet = sth.executeQuery();
			while (locationSet.next()) {
				Location l = null;
				try {
					l = new Location(locationSet.getBytes("ADDRESS"), locationSet.getShort("PORT"));
				} catch (UnknownHostException e) {
					log.warn("[locateUser]",e);
					continue;
				} catch (InvalidLocationException e) {
					log.warn("[locateUser]",e);
					continue;
				}
				locations.add(l);
			}
		} finally {
			if (sth != null)
				sth.close();
		}
		return locations;
	}
}
