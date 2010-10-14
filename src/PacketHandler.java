import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class PacketHandler {
	private HashMap<String,Callback> callbackMap;
	private UserTracker tracker;
	
	public PacketHandler(HashMap<String, Callback> callbackMap, UserTracker tracker) {
		this.callbackMap = callbackMap;	
		this.tracker = tracker;
	}
	
	
	public void handle(FractusPacket fp, ClientConnector fc) {
            // TODO: Totally fucked!
	}
}
