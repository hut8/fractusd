
import org.w3c.dom.Element;

public interface Callback {
    void dispatch(FractusMessage response, String sender, Element message, ClientConnector fc, UserTracker tracker);
}
