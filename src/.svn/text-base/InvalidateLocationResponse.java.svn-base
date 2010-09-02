import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class InvalidateLocationResponse {
	Boolean success;
	String errormessage;
	
	public InvalidateLocationResponse(Element elm) {
		
	}
	
	public InvalidateLocationResponse(String errorMessage) {
		this.errormessage = errorMessage;
		success = false;
	}
	
	public InvalidateLocationResponse() {
		success = true;
	}
	
	public Element serialize(Document doc) {
		Element res = doc.createElement("invalidate-location-response");
		if (success == true) {
			res.setAttribute("success", "true");
		} else {
			res.setAttribute("success", "false");
			res.setAttribute("error", errormessage);
		}
		return res;
	}
}
