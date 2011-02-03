package fractus.main;


import fractus.net.FractusConnector;
import java.security.interfaces.ECPublicKey;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.bouncycastle.math.ec.ECPoint;

public class UserTracker {
    private Map<ECPoint, String> keyMap;
    private static Logger log;
    static {
        log = Logger.getLogger(UserTracker.class.getName());
    }

    public UserTracker() {
        keyMap = new HashMap<ECPoint, String>();
    }

    public void addContact(FractusMessage response, String sourceUser, String destUser, FractusConnector fc, EncryptionManager em) {

    }

    public void removeContact(FractusMessage response, String sourceUser, String destUser, FractusConnector fc) {
 
    }

    public void sendContactData(FractusMessage response, String username, FractusConnector fc) {
 
    }

    public synchronized void registerKey(ECPoint key, String username)
    throws IllegalStateException {
        if (keyMap.containsKey(key)) {
            throw new IllegalStateException("Key already registered");
        }
        keyMap.put(key, username);
    }

    public String identifyKey(String encodedKey) {
        return keyMap.get(encodedKey);
    }

    public void identifyKey(FractusMessage response, String encodedKey, FractusConnector fc) {
        String username = keyMap.get(encodedKey);
        if (username == null) {
            log.info("Identified [" + encodedKey + "]: [" + keyMap.get(encodedKey) + "]");
            
            
        } else {
            log.info("Could not identify key [" + encodedKey + "]");
            
            
        }
    }

    public void registerLocation(FractusMessage response, String username, String address, String portString, FractusConnector fc) {
        // Parse parameters
        if (address == null || portString == null) {
            // Create error packet and send back to fc
            
            
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException nfe) {

            
            return;
        }

        // Find user object
 
        // Store in database
 
        
    }

    public void invalidateLocation(FractusMessage response, String username, String address, String portString) {
        // Parse parameters
        
        if (address == null || portString == null) {
            // Create error packet and send back to fc
        
            
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException nfe) {
            
            
            return;
        }

        // Find user object
  
        // TODO: GIVE RESPONSE
    }
}
