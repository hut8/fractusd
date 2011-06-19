package fractus.strategy;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import fractus.domain.Location;
import fractus.domain.UserData;
import fractus.main.Database;
import fractus.main.FractusMessage;
import fractus.net.ConnectorContext;
import fractus.net.FractusConnector;
import fractus.net.ProtocolBuffer;
import fractus.net.ProtocolBuffer.ContactData;
import fractus.net.ProtocolBuffer.ContactDataRes;
import fractus.net.ProtocolBuffer.ContactDataRes.ResponseCode;

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
		ProtocolBuffer.ContactDataRes.Builder responseBuilder =
			ProtocolBuffer.ContactDataRes.newBuilder();
		
		if (remoteUsername == null) {
			// Remote user unauthenticated.  Fail.
			log.warn("User attempted unauthenticated ContactDataReq");
			responseBuilder.setCode(ResponseCode.AUTHENTICATION_FAILURE);
			sendResponse(responseBuilder.build());
			return;
		}

		// Deserialize request
		ProtocolBuffer.ContactDataReq request;
		try {
			request = ProtocolBuffer.ContactDataReq.parseFrom(contents);
		} catch (InvalidProtocolBufferException e) {
			log.warn("Received unparseable message", e);
			// TODO: Send appropriate protocol error
			remoteConnection.disconnect(); 
			return;
		}
		
		List<String> usernameList = request.getUsernamesList();
		if (usernameList.size() == 0) {
			// User requesting all contact data
			Set<UserData> contactData = null;
			try {
				contactData =
					Database.getInstance().getContactData(remoteUsername);
			} catch (SQLException e) {
				log.warn("Database error", e);
			}
			
			// Populate response with correct username / location data
			responseBuilder.setCode(ResponseCode.SUCCESS);
			// Loop through all returned contacts
			for (UserData userData : contactData) {
				ContactData.Builder contactDataBuilder =
					ContactData.newBuilder();
				// Response part: every username
				contactDataBuilder.setUsername(userData.getUsername());
				for (Location location : userData.getLocations()) {
					// Response part: every location per username
					contactDataBuilder.addLocation(
							ProtocolBuffer.Location
							.newBuilder()
							.setAddress(location.getAddress().getHostAddress())
							.setPort(location.getPort())
							);
				}
				// Add contact data to the reponse
				responseBuilder.addContactData(contactDataBuilder);
			};
			
			// Finally, send the response
			sendResponse(responseBuilder.build());
			return;
		} else {
			// User requesting subset of contact data
			for (String username : usernameList) {
				try {
					// Verify requested contacts are valid
					if (Database.getInstance().verifyContact(
							this.connectorContext.getUsername(), username)) {
						// TODO
						throw new RuntimeException();
					}
				} catch (SQLException e) {
					log.warn("Database error",e);
				}
			}
		}
	}
	
	public void sendResponse(ContactDataRes response) {
		FractusConnector fractusConnector = connectorContext.getFractusConnector();
		fractusConnector.sendMessage(FractusMessage.build(response));
	}

}
