package fractus.strategy;

import org.apache.log4j.Logger;
import org.bouncycastle.math.ec.ECPoint;

import com.google.protobuf.InvalidProtocolBufferException;

import fractus.main.FractusMessage;
import fractus.main.UserTracker;
import fractus.net.ConnectorContext;
import fractus.net.FractusConnector;
import fractus.net.ProtocolBuffer;
import fractus.net.ProtocolBuffer.RevokeKeyRes.ResponseCode;

public class RevokeKeyReqStrategy implements PacketStrategy {
	private final static Logger log =
		Logger.getLogger(RevokeKeyReqStrategy.class.getName());
	private ConnectorContext connectorContext;
	
	public RevokeKeyReqStrategy(ConnectorContext connectorContext) {
		this.connectorContext = connectorContext;
	}
	
	@Override
	public void dispatch(byte[] contents) {
		log.debug("Processing message");
		
		// Get connector from context
		FractusConnector connector = connectorContext.getFractusConnector();
		
		// Deserialize packet
		ProtocolBuffer.RevokeKeyReq request;
		try {
			request = ProtocolBuffer.RevokeKeyReq.parseFrom(contents);
		} catch (InvalidProtocolBufferException e) {
			// Invalid request received.  Disconnect and abort.
			log.warn("Invalid protocol buffer from " + connectorContext.toString(), e);
			// TODO: Send protocol error
			connector.disconnect();
			return;
		}
		
		// Revoke key (self-authenticating if connector context contains username,
		// so simply revoke it.)
		String remoteUsername = this.connectorContext.getUsername();
		if (remoteUsername == null) {
			sendResponse(ResponseCode.AUTHORIZATION_FAILURE);
			return;
		}
		
		ECPoint remotePoint = connectorContext.getClientCipher().getRemotePoint();
		UserTracker.getInstance().unregisterKey(remotePoint, remoteUsername);
		sendResponse(ResponseCode.SUCCESS);
	}
	
	private void sendResponse(ResponseCode responesCode) {
		this.connectorContext.getFractusConnector()
			.sendMessage(FractusMessage.build(
					ProtocolBuffer.RevokeKeyRes
						.newBuilder().setCode(responesCode)
						.build()
				)
			);
	}

}
