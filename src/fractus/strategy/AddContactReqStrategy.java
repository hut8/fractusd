package fractus.strategy;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.log4j.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import fractus.main.FractusMessage;
import fractus.main.UserTracker;
import fractus.main.UserTracker.ModifyContactResponse;
import fractus.net.ConnectorContext;
import fractus.net.ProtocolBuffer;
import fractus.net.ProtocolBuffer.AddContactRes.ResponseCode;

public class AddContactReqStrategy
	implements PacketStrategy {
	
    private final static Logger log =
    	Logger.getLogger(AddContactReqStrategy.class.getName());	
	private UserTracker userTracker;
	private ConnectorContext connectorContext;
	
	public AddContactReqStrategy(ConnectorContext connectorContext, UserTracker userTracker) {
		this.userTracker = userTracker;
		this.connectorContext = connectorContext;
	}
	
	private void sendResponse(ProtocolBuffer.AddContactRes.ResponseCode responseCode) {
			connectorContext.getFractusConnector().sendMessage(
					FractusMessage.build(
							ProtocolBuffer.AddContactRes.newBuilder().setCode(responseCode).build()
					));
	}
	
	@Override
	public void dispatch(byte[] contents) {
		log.debug("Received message to dispatch");
		
		// Deserialize packet contents
		ProtocolBuffer.AddContactReq request;
		try {
			request = ProtocolBuffer.AddContactReq.parseFrom(contents);
		} catch (InvalidProtocolBufferException e) {
			log.warn("Received unparsable message", e);
			connectorContext.getFractusConnector().disconnect();
			return;
		}
		
		String targetUsername = request.getUsername();
		String sourceUsername = connectorContext.getUsername();
		
		// Make the request happen
		ModifyContactResponse modifyContactResponse;
		try {
			 modifyContactResponse = userTracker.addContact(sourceUsername, targetUsername);
		} catch (SQLException e) {
			sendResponse(ResponseCode.SERVER_ERROR);
			log.error("Could not add contact due to database error", e);
			return;
		}
		
		if (modifyContactResponse == ModifyContactResponse.SUCCESS) {
			// TODO: Spawn Client Notifier
			sendResponse(ResponseCode.SUCCESS);
		} else {
			sendResponse(ResponseCode.INVALID);
		}
	}
}
