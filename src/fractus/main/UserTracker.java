package fractus.main;

import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.bouncycastle.math.ec.ECPoint;

import fractus.net.FractusConnector;

public class UserTracker {
	private Map<ECPoint, String> keyUserMap;
	private Map<String, Set<ECPoint>> userKeyMap;

	private static Logger log;
	static {
		log = Logger.getLogger(UserTracker.class.getName());
	}

	public UserTracker() {
		keyUserMap = new HashMap<ECPoint, String>();
		userKeyMap = new HashMap<String, Set<ECPoint>>();
	}

	public static enum ModifyContactResponse { 
		SUCCESS,
		REDUNDANT,
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
	public synchronized boolean invalidateKey(ECPoint key, String username) {
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

	// Location Operations

	public void registerLocation(String username, String address, String portString) {
		// Validate and parse parameters
		if (address == null || portString == null) {           
			throw new IllegalArgumentException("Address or port null");
		}
		int port;
		port = Integer.parseInt(portString);

		// TODO: Database stuff
	}

	public void invalidateLocation(String username, String address, String portString) {
		// Parse and validate parameter
		if (address == null || portString == null) {
			throw new IllegalArgumentException("Null address or port");
		}

		int port = 0;
		port = Integer.parseInt(portString);

		// TODO
	}
}
