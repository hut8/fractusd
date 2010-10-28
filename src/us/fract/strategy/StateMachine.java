/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package us.fract.strategy;

import fractus.main.PacketHandler;

/**
 * State Machine to update packet handler strategy based
 * on current connection state, authentication status, etc.
 * @author bowenl2
 */
public class StateMachine {
    private PacketHandler packetHandler;

    public StateMachine(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    public void keyNegotiated() {
        
        
    }

    
}
