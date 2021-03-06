package fractus.strategy;

import org.apache.log4j.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import fractus.main.FractusMessage;
import fractus.main.UserTracker;
import fractus.main.UserTracker.ContactOperationResponse;
import fractus.net.ConnectorContext;
import fractus.net.ProtocolBuffer;
import fractus.net.ProtocolBuffer.AddContactRes.ResponseCode;

public class AddContactReqStrategy
	implements PacketStrategy {
	
    private final static Logger log =
    	Logger.getLogger(AddContactReqStrategy.class.getName());
	private ConnectorContext connectorContext;
	
	public AddContactReqStrategy(ConnectorContext connectorContext) {
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
		ContactOperationResponse modifyContactResponse;
		modifyContactResponse = UserTracker.getInstance().addContact(sourceUsername, targetUsername);
		
		if (modifyContactResponse == ContactOperationResponse.SUCCESS) {
			// TODO: Spawn Client Notifier
			sendResponse(ResponseCode.SUCCESS);
		} else {
			sendResponse(ResponseCode.INVALID);
		}
	}
}
