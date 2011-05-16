package fractus.main;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.bouncycastle.math.ec.ECPoint;

public class UserTracker {
	private final static UserTracker instance;
	public static UserTracker getInstance() {
		return instance;
	}
	
	private Map<ECPoint, String> keyUserMap;
	private Map<String, Set<ECPoint>> userKeyMap;
	
	private static Logger log;
	static {
		log = Logger.getLogger(UserTracker.class.getName());
		instance = new UserTracker();
	}

	private UserTracker() {
		this.keyUserMap = new HashMap<ECPoint, String>();
		this.userKeyMap = new HashMap<String, Set<ECPoint>>();
	}

	public static enum LocationOperationResponse {
		SUCCESS,
		REDUNDANT,
		INVALID_REQUEST,
		DATABASE_ERROR
	}

	public static enum ContactOperationResponse { 
		SUCCESS,
		REDUNDANT,
		INVALID_REQUEST,
		SECURITY_ERROR,
		DATABASE_ERROR
	}
		
	// Key Operations
	/**
	 * Registers key as belonging to specified username
	 * returns True if key successfully registered, False if duplicate
	 */
	public synchronized boolean registerKey(ECPoint key, String username) {
		if (keyUserMap.containsKey(key)) {
			return false;
		}
		keyUserMap.put(key, username);
		if (!userKeyMap.containsKey(username)) {
			userKeyMap.put(username, new HashSet<ECPoint>());
		}
		userKeyMap.get(username).add(key);
		return true;
	}

	/**
	 * Revokes association between given key and username
	 * @param key - public EC point belonging to key
	 * @param username
	 * @return True if invalidated successfully, false if key not present
	 */
	public synchronized boolean unregisterKey(ECPoint key, String username) {
		// Make sure that the key is present in the map
		// and that it belongs to the requestor, then remove it. 
		if (username.equalsIgnoreCase(this.keyUserMap.get(key))) {
			keyUserMap.remove(key);
		} else {
			log.info("Tried to invalidate association between username: [" +
					username + "] and EC PP: [" + BinaryUtil.encodeData(key.getEncoded()) +
			"] but key not present or not owned by that username");
			return false;
		}
		// No longer associate the user with the key.
		Set<ECPoint> userKeys = this.userKeyMap.get(username);
		if (userKeys == null || !userKeys.contains(key)) {
			log.warn("Username present in KeyUserMap but its key not in UserKeyMap!");
			return false;
		}
		userKeys.remove(key);
		log.info("Invalidated association between username: [" + username + "] and EC PP: "
				+ BinaryUtil.encodeData(key.getEncoded()));
		return true;
	}

	/**
	 * Identifies owner of given key
	 * @param point
	 * @param username
	 * @return Username of owner of key, or null if key not present or not contacts
	 * @throws SQLException
	 */
	public synchronized String identifyKey(ECPoint point, String username)
	throws SQLException {
		// Make sure that the remote user is authorized to get this (i.e. that they are contacts) 
		String keyOwner = keyUserMap.get(point);

		return verifyContact(username, keyOwner) ? keyOwner : null;
	}

	// Contact Operations
	
	public boolean verifyContact(String username1, String username2)
	throws SQLException {
		return Database.getInstance().verifyContact(username1, username2);
	}

	public Set<String> listNonreciprocalContacts(String username) {
		try {
			return Database.getInstance().listNonreciprocalContacts(username);
		} catch (SQLException e) {
			log.warn("[listNonreciprocalContacts]",e);
			return null;
		}
	}

	public ContactOperationResponse addContact(String sourceUsername, String destinationUsername) {
		try {
			return Database.getInstance().addContact(sourceUsername, destinationUsername);
		} catch (SQLException e) {
			log.warn("[addContact]",e);
			return ContactOperationResponse.DATABASE_ERROR;
		}
	}

	public ContactOperationResponse removeContact(String sourceUsername, String destinationUsername) {
		try {
			return Database.getInstance().removeContact(sourceUsername, destinationUsername);
		} catch (SQLException e) {
			log.warn("[removeContact]",e);
			return ContactOperationResponse.DATABASE_ERROR;
		}
	}

	// Location Operations
	
	public LocationOperationResponse registerLocation(String username, String address, Integer port) {
		// Validate and parse parameters
		if (address == null || port == null) {           
			return LocationOperationResponse.INVALID_REQUEST;
		}

		if (port > 65535) {
			return LocationOperationResponse.INVALID_REQUEST;
		}
		
		boolean success = false;
		InetAddress a;
		try {
			a = InetAddress.getByName(address);
		} catch (UnknownHostException e1) {
			log.warn("[registerLocation]",e1);
			return LocationOperationResponse.INVALID_REQUEST;
		}
		
		try {
			success = Database.getInstance().registerLocation(username, a, port.shortValue());
		} catch (SQLException e) {
			return LocationOperationResponse.DATABASE_ERROR;
		}
		
		return success ? LocationOperationResponse.SUCCESS : LocationOperationResponse.REDUNDANT;
	}

	public LocationOperationResponse unregisterLocation(String username, String address, String portString) {
		// Validate and parse parameters
		if (address == null || portString == null) {           
			return LocationOperationResponse.INVALID_REQUEST;
		}

		Short port;
		try {
			port = Short.parseShort(portString);
		} catch (NumberFormatException e) {
			return LocationOperationResponse.INVALID_REQUEST;
		}

		InetAddress ipAddress;
		try {
			ipAddress = (InetAddress)InetAddress.getByName(address);
		} catch (UnknownHostException e1) {
			log.warn("[invalidateLocation]",e1);
			return LocationOperationResponse.INVALID_REQUEST;
		}
		
		boolean success = false;
		try {
			success = Database.getInstance().registerLocation(username, ipAddress, port);
		} catch (SQLException e) {
			return LocationOperationResponse.DATABASE_ERROR;
		}
		return success ? LocationOperationResponse.SUCCESS : LocationOperationResponse.REDUNDANT;

	}
	// Contact/Location Data
}
