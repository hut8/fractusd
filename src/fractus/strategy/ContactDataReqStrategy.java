package fractus.strategy;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import fractus.net.ConnectorContext;
import fractus.net.FractusConnector;
import fractus.net.ProtocolBuffer;

public class ContactDataReqStrategy implements PacketStrategy {

	private ConnectorContext connectorContext;
    private final static Logger log =
    	Logger.getLogger(ContactDataReqStrategy.class.getName());
	
	public ContactDataReqStrategy(ConnectorContext connectorContext) {
		this.connectorContext = connectorContext;
	}
	
	@Override
	public void dispatch(byte[] contents) {
		log.debug("Received message to dispatch");
		String remoteUsername = connectorContext.getUsername();
		FractusConnector remoteConnection = connectorContext.getFractusConnector();
		
		ProtocolBuffer.ContactDataRes response;
		
		if (remoteUsername == null) {
			// Remote user unauthenticated.  Fail.
			log.warn("User attempted unauthenticated ContactDataReq");
			
		}

		// Deserialize request
		ProtocolBuffer.ContactDataReq request;
		try {
			request = ProtocolBuffer.ContactDataReq.parseFrom(contents);
		} catch (InvalidProtocolBufferException e) {
			log.warn("Received unparseable message", e);
			remoteConnection.disconnect(); 
			return;
		}
		
		List<ProtocolBuffer.ContactData> contactDataList;
		List<String> usernameList = request.getUsernamesList();
		if (usernameList.size() == 0) {
			// User requesting all contact data
			
			
		} else {
			// User requesting subset of contact data
			
			// Verify requested contacts are valid
		}
			
		
		
	}

}
