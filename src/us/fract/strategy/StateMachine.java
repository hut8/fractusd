/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package us.fract.strategy;

import fractus.main.ClientCipher;
import fractus.main.MessageDescriptor;
import fractus.main.PacketHandler;
import fractus.net.FractusConnector;
import org.bouncycastle.math.ec.ECPoint;

/**
 * State Machine to update packet handler strategy based
 * on current connection state, authentication status, etc.
 * @author bowenl2
 */
public class StateMachine {
    private PacketHandler packetHandler;
    private FractusConnector fractusConnector;

    public StateMachine(PacketHandler packetHandler, FractusConnector fractusConnector) {
        this.packetHandler = packetHandler;
        this.fractusConnector = fractusConnector;
    }

    public void keyReceived(ClientCipher clientCipher) {
        // Create strategy for receiving RegisterKey
        RegisterKeyStrategy rks = new RegisterKeyStrategy(clientCipher, fractusConnector);
        packetHandler.register(new MessageDescriptor(MessageDescriptor.REGISTER_KEY_REQ), rks);
    }

    /**
     * To be called as soon as the remote key can be linked to a
     * username
     * @param username
     */
    public void authenticated(String username) {
        
    }
}
