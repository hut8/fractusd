import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class FractusErrorResponse {
	String errormsg;
	public FractusErrorResponse(String errormsg) {
		this.errormsg = errormsg;
	}
	
	public Element serialize(Document doc) {
		Element res = doc.createElement("fractus-error");
		
		res.setTextContent(errormsg);
		return res;
	}
}
