package fractus.strategy;

import org.apache.log4j.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import fractus.main.UserTracker;
import fractus.net.ConnectorContext;
import fractus.net.ProtocolBuffer;

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
		
	}
}
