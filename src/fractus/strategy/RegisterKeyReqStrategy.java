/**
 * 
 */
package fractus.strategy;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import fractus.net.ConnectorContext;
import fractus.net.FractusConnector;
import fractus.net.ProtocolBuffer;
import fractus.net.ProtocolBuffer.RegisterKeyRes;
import fractus.net.ProtocolBuffer.RegisterKeyRes.ResponseCode;
import fractus.main.AccountManager;
import fractus.main.FractusMessage;
import fractus.main.UserTracker;

/**
 * @author bowenl2
 *
 */
public class RegisterKeyReqStrategy
implements PacketStrategy {

	private final static Logger log =
		Logger.getLogger(RegisterKeyReqStrategy.class.getName());
	private ConnectorContext connectorContext;

	public RegisterKeyReqStrategy(ConnectorContext connectorContext) {
		this.connectorContext = connectorContext;
	}

	private void sendResponse(RegisterKeyRes.ResponseCode responseCode) {
		// Serialize response
		ProtocolBuffer.RegisterKeyRes response = 
			ProtocolBuffer.RegisterKeyRes.newBuilder()
			.setCode(responseCode).build();

		// Send response
		FractusMessage responseMessage = FractusMessage.build(response);
		try {
			connectorContext.getFractusConnector().sendMessage(responseMessage);
		} catch (Throwable t) {
			log.warn("Unable to send message via connector", t);
		}
	}
	
	@Override
	public void dispatch(byte[] contents) {
		log.debug("Received message to dispatch");

		// Get connector from context
		FractusConnector connector = connectorContext.getFractusConnector();

		// Deserialize packet
		ProtocolBuffer.RegisterKeyReq request;
		try {
			request = ProtocolBuffer.RegisterKeyReq.parseFrom(contents);
		} catch (InvalidProtocolBufferException e) {
			// Invalid request received.  Disconnect and abort.
			log.warn("Invalid protocol buffer from " + connectorContext.toString(), e);
			connector.disconnect();
			return;
		}

		// Authenticate user
		String username = request.getUsername();
		String password = request.getPassword();

		boolean authSuccess = false;
		try { 
			authSuccess = AccountManager.getInstance().authenticate(username, password);
		} catch (SQLException e) {
			log.error("Could not authenticate user due to database error", e);
			sendResponse(ResponseCode.SERVER_ERROR);
			return;
		}

		if (!authSuccess) {
			log.info("Authentication failure for " + username);
			sendResponse(ResponseCode.AUTHENTICATION_FAILURE);
			return;
		}
		
		// Register key
		try {
			UserTracker.getInstance().registerKey(connectorContext.getClientCipher().getRemotePoint(), username);
			connectorContext.setUsername(username);
			sendResponse(ResponseCode.SUCCESS);
			return;
		} catch (IllegalStateException e) {
			log.warn("User " + username + " tried to register existing key");
			sendResponse(ResponseCode.DUPLICATE_KEY);
			return;
		}
	}
}
