package us.fract.strategy;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author bowenl2
 */
public interface PacketStrategy {
    public void dispatch(byte[] contents);
}
