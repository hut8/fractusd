package fractus.strategy;

import fractus.main.UserTracker;
import fractus.net.ProtocolBuffer;

public class AddContactReqStrategy
	implements PacketStrategy {
	
	private UserTracker userTracker;
	
	public AddContactReqStrategy(UserTracker userTracker) {
		this.userTracker = userTracker;
	}
	
	
	@Override
	public void dispatch(byte[] contents) {
		// Deserialize packet contents
		
		
		
		// Establish username from public key

	}

}
