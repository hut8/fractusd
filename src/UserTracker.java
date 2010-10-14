
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

public class UserTracker {

    private Map<String, String> keyMap;
    private Logger log;

    public UserTracker() {
        keyMap = new HashMap<String, String>();
        log = Logger.getLogger(this.getClass().getName());
    }

    public void addContact(FractusMessage response, String sourceUser, String destUser, ClientConnector fc, EncryptionManager em) {
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

    public void removeContact(FractusMessage response, String sourceUser, String destUser, ClientConnector fc) {
        UserData su, du;

        try {
            su = new UserData(sourceUser);
            du = new UserData(destUser);
        } catch (UnknownUserException e) {
            RemoveContactResponse acr = new RemoveContactResponse("invalid-user");
            
            return;
        } catch (SQLException e) {
            RemoveContactResponse acr = new RemoveContactResponse("internal-error");
            
            return;
        }


        if (su.removeContact(du, fc)) {
            /* success */
            RemoveContactResponse acr = new RemoveContactResponse();
            
        } else {
            /* failure */
            RemoveContactResponse acr = new RemoveContactResponse("redundant-request");
            
        }
    }

    public void sendContactData(FractusMessage response, String username, ClientConnector fc) {
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

    public void registerKey(FractusMessage response, String username, String key, ClientConnector fc) {
        // Make sure key isn't taken
        if (keyMap.containsKey(key)) {
            RegisterKeyResponse res = new RegisterKeyResponse("key-collision");
            
            try {
                fc.sendMessage(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        keyMap.put(key, username);
        RegisterKeyResponse res = new RegisterKeyResponse();
        
    }

    public String identifyKey(String encodedKey) {
        return keyMap.get(encodedKey);
    }

    public void identifyKey(FractusMessage response, String encodedKey, ClientConnector fc) {
        String username = keyMap.get(encodedKey);
        if (username == null) {
            log.info("Identified [" + encodedKey + "]: [" + keyMap.get(encodedKey) + "]");
            IdentifyKeyResponse ikr = new IdentifyKeyResponse(true, keyMap.get(encodedKey), encodedKey);
            
        } else {
            log.info("Could not identify key [" + encodedKey + "]");
            IdentifyKeyResponse ikr = new IdentifyKeyResponse(false, "unknown-key", encodedKey);
            
        }
    }

    public void registerLocation(FractusMessage response, String username, String address, String portString, ClientConnector fc) {
        // Parse parameters
        if (address == null || portString == null) {
            // Create error packet and send back to fc
            RegisterLocationResponse res = new RegisterLocationResponse("null-parameters");
            
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException nfe) {
            RegisterLocationResponse res = new RegisterLocationResponse("nonnumeric-port");
            
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
        RegisterLocationResponse res;
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
                res = new RegisterLocationResponse(new UserLocation(address, port));
            } else {
                throw new SQLException("Invalid results returned from server");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            res = new RegisterLocationResponse("internal-error");
        }
        
    }

    public void invalidateLocation(FractusMessage response, String username, String address, String portString) {
        // Parse parameters
        InvalidateLocationResponse res;
        if (address == null || portString == null) {
            // Create error packet and send back to fc
            res = new InvalidateLocationResponse("null-parameters");
            
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException nfe) {
            res = new InvalidateLocationResponse("nonnumeric-port");
            
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
                res = new InvalidateLocationResponse();
            } else {
                throw new SQLException("Invalid results returned from server");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            res = new InvalidateLocationResponse("internal-error");
        }
        // TODO: GIVE RESPONSE
    }
}
