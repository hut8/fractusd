
import com.google.protobuf.Message;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bowenl2
 */
public interface MessageReceiver {
    public void receive(Message message);
}
