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
        UserData su, du;

        try {
            su = new UserData(sourceUser);
            du = new UserData(destUser);
        } catch (UnknownUserException e) {
            // TODO Create error response
            return;
        } catch (SQLException e) {
            // TODO Create error response
            return;
        }

        boolean validPair;
        try {
            validPair = su.addContact(du, fc);
        } catch (DuplicateAddRequestException e) {
            // TODO Create error response
            return;
        }

// TODO Create correct response
        if (validPair) {
            // TODO: If reciprocal contact exists, send Contact Data as well
            List<UserLocation> locations = du.locate();
            // TODO Append Location Data
        } else {
            // Construct message to send to destination user


            
            

            // Locate destination user
            List<UserLocation> locations = du.locate();
            for (UserLocation loc : locations) {
                
                
            }

        }
        // TODO Create Response
    }

    public void removeContact(FractusMessage response, String sourceUser, String destUser, FractusConnector fc) {
        UserData su, du;

        try {
            su = new UserData(sourceUser);
            du = new UserData(destUser);
        } catch (UnknownUserException e) {
            
            
            return;
        } catch (SQLException e) {
            
            
            return;
        }


        if (su.removeContact(du, fc)) {
            /* success */
            
            
        } else {
            /* failure */
            
            
        }
    }

    public void sendContactData(FractusMessage response, String username, FractusConnector fc) {
        // Get User Object
        UserData user = null;
        try {
            user = new UserData(username);
        } catch (UnknownUserException e) {
            e.printStackTrace();
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // Assemble data map
        Map<String, List<UserLocation>> dataMap = new HashMap<String, List<UserLocation>>();
        Connection db = null;
        ResultSet rs;
        CallableStatement sth;

        // Retrieve contacts and empty location list
        try {
            db = Database.getConnection();
            sth = db.prepareCall("CALL GetUserContacts_prc(?)");
            sth.setInt(1, user.getUserId());
            rs = sth.executeQuery();
            while (rs.next()) {
                dataMap.put(rs.getString(1), new ArrayList<UserLocation>());
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            return;
        }


        // Get their locations
        try {
            sth = db.prepareCall("CALL SendContactData_prc(?)");
            sth.setInt(1, user.getUserId());
            rs = sth.executeQuery();
            while (rs.next()) {
                String contactUsername = rs.getString(1);
                if (!dataMap.containsKey(contactUsername)) {
                    // Should not happen
                    dataMap.put(contactUsername, new ArrayList<UserLocation>());
                }
                List<UserLocation> locations = dataMap.get(contactUsername);
                locations.add(new UserLocation(rs.getString(2), rs.getInt(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        
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
        UserData ud;
        try {
            ud = new UserData(username);
        } catch (UnknownUserException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
            return;
        } catch (SQLException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
            return;
        }

        // Store in database
        
        Connection c = null;
        try {
            c = Database.getConnection();
            CallableStatement auStmt = c.prepareCall("call RecordUserLocation_prc(?,?,?)");
            auStmt.setInt(1, ud.getUserId());
            auStmt.setString(2, address);
            auStmt.setInt(3, port);
            auStmt.execute();
            int uc = auStmt.getUpdateCount();
            if (uc == 1) {
                //res = new RegisterLocationResponse(new UserLocation(address, port));
            } else {
                throw new SQLException("Invalid results returned from server");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //res = new RegisterLocationResponse("internal-error");
        }
        
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
        UserData ud;
        try {
            ud = new UserData(username);
        } catch (UnknownUserException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
            return;
        } catch (SQLException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
            return;
        }

        Connection c = null;
        try {
            c = Database.getConnection();
            CallableStatement auStmt = c.prepareCall("CALL InvalidateUserLocation_prc(?,?,?)");
            auStmt.setInt(1, ud.getUserId());
            auStmt.setString(2, address);
            auStmt.setInt(3, port);
            auStmt.execute();
            int uc = auStmt.getUpdateCount();
            if (uc == 1) {
                //res = new InvalidateLocationResponse();
            } else {
                throw new SQLException("Invalid results returned from server");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //res = new InvalidateLocationResponse("internal-error");
        }
        // TODO: GIVE RESPONSE
    }
}
