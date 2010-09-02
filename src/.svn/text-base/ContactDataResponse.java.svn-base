import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class ContactDataResponse {
	private Map<String, List<UserLocation>> dataMap;
	private String errorMsg;
	private Boolean success;
	
	/**
	 * Constructor to be used for results from database
	 */
	public ContactDataResponse(Map<String, List<UserLocation>> dataMap) {
		success = true;
		this.dataMap = dataMap;
	}
	
	/**
	 * Constructor to be used if an error has occured
	 * @param s
	 */
	public ContactDataResponse(String s) {
		success = false;
		errorMsg = s;
	}
	
	public Element serialize(Document doc) {
		Element res = doc.createElement("contact-data");
		if (success == true) {
			res.setAttribute("success", "true");
		} else {
			res.setAttribute("success", "false");
			res.setAttribute("error", errorMsg);
			return res;
		}
		
		for (Entry<String,List<UserLocation>> entry : dataMap.entrySet()) {
			Element contactElm = doc.createElement("contact");
			contactElm.setAttribute("username", entry.getKey());
			for (UserLocation loc : entry.getValue()) {
				Element locElm = doc.createElement("location");
				locElm.setAttribute("address", loc.getAddress());
				locElm.setAttribute("port", loc.getPort().toString());
				contactElm.appendChild(locElm);
			}
			res.appendChild(contactElm);
		}
		return res;
	}
}
