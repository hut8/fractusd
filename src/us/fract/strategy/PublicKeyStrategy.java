/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package us.fract.strategy;

import com.google.protobuf.InvalidProtocolBufferException;
import fractus.main.ClientCipher;
import fractus.net.FractusConnector;
import fractus.net.ProtocolBuffer;
import java.security.GeneralSecurityException;
import org.apache.log4j.Logger;

/**
 *
 * @author bowenl2
 */
public class PublicKeyStrategy
implements PacketStrategy {
    private ClientCipher clientCipher;
    private FractusConnector fractusConnector;
    private StateMachine stateMachine;
    private final static Logger log = Logger.getLogger(PublicKeyStrategy.class.getName());

    public PublicKeyStrategy(FractusConnector connector,
            StateMachine stateMachine,
            ClientCipher clientCipher) {
        log.debug("Creating new Public Key Strategy with client cipher: " + clientCipher.toString());
        this.clientCipher = clientCipher;
        this.stateMachine = stateMachine;
        this.fractusConnector = connector;
    }

    @Override
    public synchronized void dispatch(byte[] contents) {
        log.debug("Received message to dispatch");
        
        // If the public key is established, DO NOT renegotiate -- disconnect!
        if (clientCipher.isInitialized()) {
            log.warn("***RENEGOTIATION REQUESTED - DISCONNECTING***");
            fractusConnector.disconnect();
            return;
        }

        // Construct public key message
        ProtocolBuffer.PublicKey remotePKPB;
        try {
            remotePKPB = ProtocolBuffer.PublicKey.parseFrom(contents);
        } catch (InvalidProtocolBufferException ex) {
            log.warn("Received unparseable message", ex);
            // This is severe, so we should disconnect
            return;
        }
        String remotePKEncoding = remotePKPB.getEncoding();
        byte[] remotePKData = remotePKPB.getPublicKey().toByteArray();
        try {
            clientCipher.negotiate(remotePKEncoding, remotePKData);
        } catch (GeneralSecurityException ex) {
            log.warn("Strategy failed; unable to negotiate AES key.", ex);
        }
        
        log.debug("Negotiated key, notifying state machine");

        stateMachine.keyReceived(clientCipher);
    }

}
