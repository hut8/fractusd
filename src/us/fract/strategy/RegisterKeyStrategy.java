/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.fract.strategy;

import com.google.protobuf.InvalidProtocolBufferException;
import fractus.main.FractusMessage;
import fractus.main.UserAuthenticator;
import fractus.main.UserCredentials;
import fractus.main.UserTracker;
import fractus.net.FractusConnector;
import fractus.net.ProtocolBuffer;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author bowenl2
 */
public class RegisterKeyStrategy
        implements PacketStrategy {

    private static Logger log = Logger.getLogger(RegisterKeyStrategy.class.getName());
    private UserTracker tracker;
    private FractusConnector connector;

    public RegisterKeyStrategy(UserTracker tracker, FractusConnector connector) {
        this.tracker = tracker;
        this.connector = connector;
    }

    private void sendResponse(ProtocolBuffer.RegisterKeyRes.ResponseCode rc) {
        ProtocolBuffer.RegisterKeyRes.Builder responseBuilder = ProtocolBuffer.RegisterKeyRes.newBuilder();
        responseBuilder.setCode(rc);
        ProtocolBuffer.RegisterKeyRes response = responseBuilder.build();
        FractusMessage fm = FractusMessage.build(response);
        try {
            connector.sendMessage(fm);
        } catch (GeneralSecurityException ex) {
            log.warn("Encountered security exception while sending response", ex);
        } catch (IOException ex) {
            log.warn("Encountered IO Exception while sending response", ex);
        }
    }

    @Override
    public void dispatch(byte[] contents) {
        log.debug("Deserializing Register Key Request");
        ProtocolBuffer.RegisterKeyReq registerKeyReq;
        try {
            registerKeyReq = ProtocolBuffer.RegisterKeyReq.parseFrom(contents);
        } catch (InvalidProtocolBufferException ex) {
            log.warn("Could not deserialize message", ex);
            return;
        }        

        log.debug("Register Key Request deserialized");
        String username = registerKeyReq.getUsername();
        String password = registerKeyReq.getPassword();
        log.debug("Attempting to authenticate " + username);
        boolean authenticated = false;
        try {
            authenticated = UserAuthenticator.authenticate(new UserCredentials(username, password));
        } catch (SQLException ex) {
            log.warn("Encountered SQL Exception during authentication", ex);
            sendResponse(ProtocolBuffer.RegisterKeyRes.ResponseCode.INTERNAL_ERROR);
            return;
        }

        if (authenticated == false) {
            sendResponse(ProtocolBuffer.RegisterKeyRes.ResponseCode.AUTHENTICATION_FAILURE);
            return;
        }

        log.info("Attempting to register key for " + username);

        tracker.registerKey(null, username);
        
    }
}
