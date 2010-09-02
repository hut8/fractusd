import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class RegisterLocationResponse {
	Boolean success;
	String errormessage;
	UserLocation location;
	
	public RegisterLocationResponse(String errorMessage) {
		this.errormessage = errorMessage;
		success = false;
	}
	
	public RegisterLocationResponse(UserLocation location) {
		success = true;
		this.location = location;
	}
	
	public Element serialize(Document doc) {
		Element res = doc.createElement("register-location-response");
		if (success == true) {
			res.setAttribute("success", "true");
			res.setAttribute("address", location.getAddress());
			res.setAttribute("port", location.getPort().toString());
			
		} else {
			res.setAttribute("success", "false");
			res.setAttribute("error", errormessage);
		}
		return res;
	}
}
