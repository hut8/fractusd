import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class RemoveContactResponse {
	private Boolean success;
	private String errormsg;
	
	public RemoveContactResponse(String errormsg) {
		this.errormsg = errormsg;
		success = false;
	}
	
	public RemoveContactResponse() {
		success = true;
	}
	
	public Element serialize(Document doc) {
		Element res = doc.createElement("remove-contact-response");
		
		if (success) {
			res.setAttribute("success", "true");
		} else {
			res.setAttribute("success", "false");
			res.setAttribute("error", errormsg);	
		}

		return res;
	}
}
