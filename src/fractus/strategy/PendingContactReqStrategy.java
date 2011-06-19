package fractus.strategy;

import fractus.net.ConnectorContext;

public class PendingContactReqStrategy implements PacketStrategy {
	private ConnectorContext connectorContext;
	
	public PendingContactReqStrategy(ConnectorContext connectorContext) {
		this.connectorContext = connectorContext;
	}
	
	@Override
	public void dispatch(byte[] contents) {
		// TODO Auto-generated method stub
		throw new RuntimeException();

	}

}
