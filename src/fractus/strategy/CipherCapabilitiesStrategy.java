package fractus.strategy;

import com.google.protobuf.InvalidProtocolBufferException;

import fractus.net.FractusConnectorInitializer;
import fractus.net.ProtocolBuffer;

public class CipherCapabilitiesStrategy
implements PacketStrategy {

	private FractusConnectorInitializer initializer;
	
	public CipherCapabilitiesStrategy(FractusConnectorInitializer initializer) {
		this.initializer = initializer;
	}
	
	@Override
	public void dispatch(byte[] contents) {
		ProtocolBuffer.CipherCapabilities cipherCapabilities;
		try {
			cipherCapabilities = ProtocolBuffer.CipherCapabilities.parseFrom(contents);
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			return;
		}
		this.initializer.receiveCipherCapabilities(cipherCapabilities.getCipherSuitesList());
	}

}
