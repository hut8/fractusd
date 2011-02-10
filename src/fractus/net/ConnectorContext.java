package fractus.net;

public class ConnectorContext {

	private String username;
	private PacketHandler packetHandler;
	private FractusConnector fractusConnector;
	
	public ConnectorContext(PacketHandler packetHandler,
			FractusConnector fractusConnector) {
		this.packetHandler = packetHandler;
		this.fractusConnector = fractusConnector;
	}
	
	public PacketHandler getPacketHandler() {
		return packetHandler;
	}
	
	public FractusConnector getFractusConnector() {
		return fractusConnector;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "[Context for: " + fractusConnector.toString() + "]";
	}
	
}
