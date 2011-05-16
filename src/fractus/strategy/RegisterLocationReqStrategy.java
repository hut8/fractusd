package fractus.strategy;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import fractus.main.FractusMessage;
import fractus.main.UserTracker;
import fractus.main.UserTracker.LocationOperationResponse;
import fractus.net.ConnectorContext;
import fractus.net.FractusConnector;
import fractus.net.ProtocolBuffer;
import fractus.net.ProtocolBuffer.Location;

public class RegisterLocationReqStrategy
implements PacketStrategy {

	private final static Logger log =
		Logger.getLogger(RegisterLocationReqStrategy.class.getName());
	private ConnectorContext connectorContext;

	
	public RegisterLocationReqStrategy(ConnectorContext connectorContext) {
		this.connectorContext = connectorContext;
	}
		
	@Override
	public void dispatch(byte[] contents) {
		log.debug("Received message to dispatch");
		
		// Get connector from context
		FractusConnector connector = connectorContext.getFractusConnector();

		// Deserialize packet
		ProtocolBuffer.RegisterLocationReq request;
		try {
			request = ProtocolBuffer.RegisterLocationReq.parseFrom(contents);
		} catch (InvalidProtocolBufferException e) {
			log.warn("Invalid protocol buffer from " + connectorContext.toString(), e);
			connector.disconnect();
			return;
		}
		
		// Authenticate
		if (connectorContext.getUsername() == null) {
			//sendResponse(ResponseCode.AUTHORIZATION_FAILURE);
			return;
		}

		UserTracker userTracker = UserTracker.getInstance();
		List<Location> locationList = request.getLocationListList();
		ProtocolBuffer.RegisterLocationRes.Builder builder =
			ProtocolBuffer.RegisterLocationRes.newBuilder();
		
		for (Location location : locationList) {
			LocationOperationResponse res =
			userTracker.registerLocation(
					connectorContext.getUsername(), location.getAddress(), location.getPort());
			ProtocolBuffer.RegisterLocationRes.ResponseCode protoBufRC = null;
			switch (res) {
			case DATABASE_ERROR:
				protoBufRC = ProtocolBuffer.RegisterLocationRes.ResponseCode.INTERNAL_ERROR;
				break;
			case INVALID_REQUEST:
				protoBufRC = ProtocolBuffer.RegisterLocationRes.ResponseCode.INVALID_REQUEST;
				break;
			case REDUNDANT:
				protoBufRC = ProtocolBuffer.RegisterLocationRes.ResponseCode.REDUNDANT_REQUEST;
				break;
			case SUCCESS:
				protoBufRC = ProtocolBuffer.RegisterLocationRes.ResponseCode.SUCCESS;
				break;
			}
			builder.addResponseList(
					ProtocolBuffer.RegisterLocationRes.ResponseMessage.newBuilder()
					.setCode(protoBufRC)
					.setLocation(location));
		}

		FractusMessage responseMessage = FractusMessage.build(builder.build());
		try {
			connectorContext.getFractusConnector().sendMessage(responseMessage);
		} catch (Throwable t) {
			log.warn("Unable to send message via connector", t);
		}
	}
}
