/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fractus.strategy;

import com.google.protobuf.InvalidProtocolBufferException;
import java.security.GeneralSecurityException;
import org.apache.log4j.Logger;

import fractus.crypto.ClientCipher;
import fractus.crypto.Nonce;
import fractus.net.FractusConnector;
import fractus.net.ProtocolBuffer;

/**
 *
 * @author bowenl2
 */
public class HandshakeDataStrategy
implements PacketStrategy {
    private ClientCipher clientCipher;
    private FractusConnector fractusConnector;
    private boolean agreedCipherSuite;
    private final static Logger log =
    	Logger.getLogger(HandshakeDataStrategy.class.getName());

    public HandshakeDataStrategy(FractusConnector connector, ClientCipher clientCipher) {
        log.debug("Creating new Public Key Strategy with client cipher: " + clientCipher.toString());
        this.clientCipher = clientCipher;
        this.fractusConnector = connector;
        this.agreedCipherSuite = false;
    }
    
    public void agreedCipherSuite() {
    	this.agreedCipherSuite = true;
    }

    @Override
    public synchronized void dispatch(byte[] contents) {
        log.debug("Received message to dispatch");
        
        // If the public key is established, DO NOT renegotiate -- disconnect!
        if (clientCipher.isInitialized()) {
            log.warn("Renegotiation Requested - Disconnecting");
            fractusConnector.disconnect();
            return;
        }
        
        if (!this.agreedCipherSuite) {
        	log.warn("Protocol error: agreed cipher suite not established");
        	// TODO: Protocol Error
        	fractusConnector.disconnect();
        	return;
        }

        // Construct handshake message object
        ProtocolBuffer.HandshakeData remotePKPB;
        try {
            remotePKPB = ProtocolBuffer.HandshakeData.parseFrom(contents);
        } catch (InvalidProtocolBufferException ex) {
            log.warn("Received unparseable message", ex);
            fractusConnector.disconnect();
            return;
        }
        
        // Initial validation
        if (!(
        		remotePKPB.hasNonce() &&
        		remotePKPB.hasPublicKey() &&
        		remotePKPB.hasPublicKeyEncoding())) {
        	// TODO: Protocol Error, required fields not sent
        	log.warn("Received invalid handshake packet (fields missing)");
        	fractusConnector.disconnect();
        	return;
        }
        byte[] remoteNonce = remotePKPB.getNonce().toByteArray();
        String remotePKEncoding = remotePKPB.getPublicKeyEncoding();
        byte[] remotePKData = remotePKPB.getPublicKey().toByteArray();

        // Negotiate cryptographic parameters with ClientCipher object
        try {
			clientCipher.negotiate(remotePKEncoding, remotePKData, new Nonce(remoteNonce));
		} catch (GeneralSecurityException e) {
			log.warn("Could not negotiate client cipher",e);
			// TODO: Protocol error
			fractusConnector.disconnect();
			return;
		}
		
        log.debug("Strategy successfully deployed");
    }
}
