package fractus.strategy;

import org.apache.log4j.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import fractus.net.FractusConnectorInitializer;
import fractus.net.ProtocolBuffer;

public class CipherCapabilitiesStrategy
implements PacketStrategy {

	private FractusConnectorInitializer initializer;
    private final static Logger log =
    	Logger.getLogger(CipherCapabilitiesStrategy.class);
    
	public CipherCapabilitiesStrategy(FractusConnectorInitializer initializer) {
		this.initializer = initializer;
	}
	
	@Override
	public void dispatch(byte[] contents) {
		ProtocolBuffer.CipherCapabilities cipherCapabilities;
		try {
			cipherCapabilities = ProtocolBuffer.CipherCapabilities.parseFrom(contents);
		} catch (InvalidProtocolBufferException e) {
			log.warn("Invalid Protocol Buffer",e);
			return;
		}
		this.initializer.receiveCipherCapabilities(cipherCapabilities.getCipherSuitesList());
	}

}
