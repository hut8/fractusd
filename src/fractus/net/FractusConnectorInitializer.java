package fractus.net;

/**
 * Initializes connection for FractusConnector
 * Handles header, cryptography negotiation, PacketHandler initialization
 * @author 14581
 *
 */
public class FractusConnectorInitializer {
	private FractusConnector fractusConnector;
	
	public FractusConnectorInitializer(FractusConnector fractusConnector) {
		this.fractusConnector = fractusConnector;
	}
}
