package fractus.net;

import fractus.crypto.ClientCipher;

public class ConnectorContext {

	private String username;
	private PacketHandler packetHandler;
	private FractusConnector fractusConnector;
	private ClientCipher clientCipher;
	
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

	public ClientCipher getClientCipher() {
		return clientCipher;
	}

	public void setClientCipher(ClientCipher clientCipher) {
		this.clientCipher = clientCipher;
	}
	
	@Override
	public String toString() {
		return "[Context for: " + fractusConnector.toString() + "]";
	}
	
}
