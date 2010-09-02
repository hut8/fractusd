import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class AddContactResponse {
	private Boolean success;
	private String errormsg;
	private Boolean reciprocal;
	List<UserLocation> locations;
	
	
	public AddContactResponse(String errormsg) {
		this.errormsg = errormsg;
		this.success = false;
		this.reciprocal = false;
	}
	
	public AddContactResponse(boolean reciprocal) {
		this.success = true;
		this.reciprocal = reciprocal;
	}
	
	public void setLocationData(List<UserLocation> locations) {
		this.locations = locations;
	}
	
	public Element serialize(Document doc) {
		Element res = doc.createElement("add-contact-response");
		
		if (success) {
			res.setAttribute("success", "true");
			if (reciprocal) {
				res.setAttribute("reciprocal", "true");
				if (locations != null) {
					for (UserLocation loc : locations) {
						Element locationsElm = doc.createElement("location");
						locationsElm.setAttribute("address", loc.getAddress());
						locationsElm.setAttribute("port", loc.getPort().toString());
						res.appendChild(locationsElm);
					}
				}
			} else {
				res.setAttribute("reciprocal", "false");
				// the other client is notified by the UserTracker here
			}
		} else {
			res.setAttribute("success", "false");
			res.setAttribute("error", errormsg);	
		}

		return res;
	}
}
