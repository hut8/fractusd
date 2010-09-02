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
		// Identity of remote user
		String username;
		
		// One message will get one response
		FractusMessage response;
		try {
			 response = new FractusMessage();
		} catch (ParserConfigurationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return;
		}
		
		// Identify sender based on public key
		String encodedKey = fp.getEncodedKey();
		username = tracker.identifyKey(encodedKey);
		
		// If not identified by public key, fine --
		// we can still see if it has its own authentication data
		
		// Turn FractusPacket message into XML Document		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			return;
		}
		Document doc;
		try {
			doc = docBuilder.parse(new ByteArrayInputStream(fp.getMessage()));
		} catch (SAXException e1) {
			e1.printStackTrace();
			return;
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
	    // Extract FractusMessage from packet
		FractusMessage msg = new FractusMessage(doc);
		
		// Extract authentication data if there is any
		UserCredentials uc = msg.getUserCredentials();
		
		// Make sure that the key and usercredentials agree if both are present
		if (username != null && uc != null) {
			if (!username.equals(uc.getUsername())) {
				// TODO: THIS IS VERY BAD.
				System.out.println("PacketHandler: serious error: username mismatched");
				return;
			}
		}
		
		if (username == null && uc != null) {
			// Verify user credentials
			UserAuthenticator auth = new UserAuthenticator(uc);
			if (auth.authenticate() != null) {
				username = uc.getUsername();
			} else {
				// Error: authentication failure
				FractusErrorResponse fer = new FractusErrorResponse("authentication-failure");
				response.appendElement(fer.serialize(response.getDocument()));
				try {
					fc.sendMessage(response);
				} catch (Exception e) { e.printStackTrace(); }
				return;
			}
		}
		
		if (username == null) {
			// Error: we have no idea who this is.
			FractusErrorResponse res = new FractusErrorResponse("authentication-required");
			response.appendElement(res.serialize(response.getDocument()));
			try {
				fc.sendMessage(response);
			} catch (Exception e) { e.printStackTrace(); }
			return;
		}
		
	    // Loop through root level elements
		Element root = doc.getDocumentElement();
		NodeList operations = root.getChildNodes();
		for (int i=0; i < operations.getLength(); i++) {
			Node n = operations.item(i); 
			if (n.getNodeType() != Node.ELEMENT_NODE) { continue; }
			
			Element e = (Element)n;
		    Callback cb = callbackMap.get(e.getTagName());
		    if (cb == null) {
		    	// TODO: Error: unknown tag name
		    	/* send an error to the fractusconnector */
		    	System.out.println("unknown tag name!");
		    	continue;
		    }
		    System.out.println("dispatching: "+e.getTagName());
		    cb.dispatch(response, username, e, fc, tracker);
		}
		try {
			fc.sendMessage(response);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getAnonymousLogger().log(Level.SEVERE, " exception while sending response");
		}
	}
}
