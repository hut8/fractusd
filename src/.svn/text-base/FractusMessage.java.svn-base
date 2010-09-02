import org.w3c.dom.*;
import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

/**
 * Represents a message in Fractus, which is always put into a FractusPacket
 * Provides for easy XML serializing and neat handling
 * @author bowenl2
 *
 */
public class FractusMessage {
	private Document document;
	
	public FractusMessage (Document document) {
		this.document = document;
		
	}
	
	public FractusMessage()
	throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		document = docBuilder.newDocument();
		document.appendChild(document.createElement("fractus"));
	}
	
	
	public Document getDocument() {
		return document;
	}
	
	public Element getDocumentElement() {
		if (document == null) return null;
		return document.getDocumentElement();
	}
	
	public FractusMessage appendElement(Element e) {
		document.getDocumentElement().appendChild(e);
		return this;
	}
	
	public void setError(String errorMsg) {
		document.getDocumentElement().setAttribute("error", errorMsg);
	}
	
	public UserCredentials getUserCredentials() {
		Element rootElement = document.getDocumentElement();
		String username = rootElement.getAttribute("username");
		String password = rootElement.getAttribute("password");
		if (username == null || password == null) {
			return null;
		}
		return new UserCredentials(username, password);
	}
	
	public void setUserCredentials(UserCredentials uc) {
		Element rootElement = document.getDocumentElement();
		rootElement.setAttribute("username", uc.getUsername());
		rootElement.setAttribute("password", uc.getPassword());
	}
		
	public String serialize() {
        try {
            Source source = new DOMSource(document.getDocumentElement());
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
	}
}
