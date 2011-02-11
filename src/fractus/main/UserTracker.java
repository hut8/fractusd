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

    public static enum ModifyContactResponse { 
    	SUCCESS,
    	REDUNDANT,
    	SECURITY_ERROR }
    
    public ModifyContactResponse addContact(String sourceUser, String destUser)
    throws SQLException {
    	return Database.UserTracker.addContact(sourceUser, destUser);
    }

    public void removeContact(String sourceUser, String destUser) {
 
    }
    
    public boolean confirmContact(String sourceUser, String destUser)
    throws SQLException {
    	return Database.UserTracker.confirmContact(sourceUser, destUser);
    }

    public void sendContactData(String username) {
 
    }

    public synchronized void registerKey(ECPoint key, String username)
    throws IllegalStateException {
        if (keyMap.containsKey(key)) {
            throw new IllegalStateException("Key already registered");
        }
        keyMap.put(key, username);
    }

    public String identifyKey(ECPoint point) {
        return keyMap.get(point);
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
