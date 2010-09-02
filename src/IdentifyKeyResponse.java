import org.w3c.dom.Element;
import org.w3c.dom.Document;


public class IdentifyKeyResponse {
	private Boolean success;
	private String errormsg;
	private String encodedKey;
	private String username;
	
	public IdentifyKeyResponse(Boolean success, String arg1, String encodedKey) {
		this.success = success;
		this.encodedKey = encodedKey;
		if (success) {
			this.username = arg1;
		} else {
			this.errormsg = arg1;
		}
	}
	
	public Element serialize(Document doc) {
		Element res = doc.createElement("identify-key-response");
		
		res.setAttribute("key", encodedKey);
		if (success == false) {
			res.setAttribute("error", errormsg);
			return res;
		}
		
		res.setAttribute("username", username);
		
		return res;
	}
	
}
