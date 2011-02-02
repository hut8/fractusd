/**
 * 
 */
package fractus.strategy;

import org.apache.log4j.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import fractus.net.ProtocolBuffer;

/**
 * @author bowenl2
 *
 */
public class RegisterKeyReqStrategy
implements PacketStrategy {
	
	public RegisterKeyReqStrategy() {
		
	}
	
	private final static Logger log =
		Logger.getLogger(RegisterKeyReqStrategy.class.getName());
	
	@Override
	public void dispatch(byte[] contents) {
		log.debug("Received message to dispatch");

		// Deserialize packet
		ProtocolBuffer.RegisterKeyReq request;
		try {
			request = ProtocolBuffer.RegisterKeyReq.parseFrom(contents);
		} catch (InvalidProtocolBufferException e) {
			// Invalid request sent.  Disconnect.
			
		}
			
		// Authenticate user
		
	}
}
