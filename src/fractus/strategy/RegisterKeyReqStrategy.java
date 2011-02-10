/**
 * 
 */
package fractus.strategy;

import java.io.IOException;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.log4j.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import fractus.net.ConnectorContext;
import fractus.net.FractusConnector;
import fractus.net.ProtocolBuffer;
import fractus.net.ProtocolBuffer.RegisterKeyRes.ResponseCode;
import fractus.main.Database;
import fractus.main.FractusMessage;
import fractus.main.FractusPacket;
import fractus.main.UserCredentials;

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

			return;
		}

		// Response code
		ResponseCode responseCode;

		// Authenticate user
		String username = request.getUsername();
		String password = request.getPassword();

		UserCredentials credentials = new UserCredentials(username, password);
		try { 
			responseCode = Database.Authenticator.authenticate(credentials) ?
					ResponseCode.SUCCESS : ResponseCode.AUTHENTICATION_FAILURE;
		} catch (SQLException e) {
			log.error("Could not authenticate user due to database error", e);
			responseCode = ResponseCode.INTERNAL_ERROR;
		}

		// TODO: Register key


		// Serialize response
		ProtocolBuffer.RegisterKeyRes response = 
			ProtocolBuffer.RegisterKeyRes.newBuilder()
			.setCode(responseCode).build();

		// Send response
		FractusMessage responseMessage = FractusMessage.build(response);
		try {
			connector.sendMessage(responseMessage);
		} catch (Throwable t) {
			log.warn("Unable to send message via connector", t);
		}
	}
}
