import org.w3c.dom.Element;
import org.w3c.dom.Document;


public class RegisterKeyResponse {
	private Boolean success;
	private String errormsg;
	
	public RegisterKeyResponse() {
		success = true;
	}
	
	public RegisterKeyResponse(String errormsg) {
		success = false;
		this.errormsg = errormsg;
	}
	
	public Element serialize(Document doc) {
		Element res = doc.createElement("register-key-response");
		if (success) {
			res.setAttribute("success", "true");
		} else {
			res.setAttribute("success", "false");
			res.setTextContent(errormsg);
		}
		return res;
	}

}
