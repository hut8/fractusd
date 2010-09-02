import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;


public class Headers
extends HashMap<String,String> {
	private static final long serialVersionUID = 5855016095678542620L;

	public void writeHeaders(OutputStream os, EncryptionManager em) {
		PrintStream ps = new PrintStream(os);
		ps.println("fractus: 0.1");
		ps.println("key-encoding: " + em.getEncodingFormat());
		ps.println("key: " + em.getEncodedKey());
		ps.println();
	}

	public static Headers receive(InputStream is)
	throws IOException, ProtocolException {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(is));

		// Read in the header params
		Headers headerParams = new Headers();	
		while (true) {
			String currentLine = br.readLine();
			if (currentLine == null ||
					currentLine.equals("")) {
				break;
			}
			String[] parts = currentLine.split(": ");
			if (parts.length != 2) {
				throw new ProtocolException();
			}
			headerParams.put(parts[0], parts[1]);
		}		
		return headerParams;
	}
}
