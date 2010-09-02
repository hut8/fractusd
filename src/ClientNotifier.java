import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.crypto.NoSuchPaddingException;


public class ClientNotifier
implements Runnable {
	private FractusMessage message;
	private UserLocation location;
	private EncryptionManager em;
	private Logger log;
	
	/**
	 * Class to asynchronously send a message to a client
	 * (for notifications of buddylist changes, etc).  Dies with
	 * warning on error (does not requeue failed messages).
	 * @param message
	 */
	public ClientNotifier(FractusMessage message, UserLocation location, EncryptionManager em) {
		this.message = message;
		this.location = location;
		this.em = em;
		this.log = Logger.getLogger(this.getClass().getName());
	}
	
	@Override
	public void run() {
		log.info("Client notifier thread alive for: " + location.getAddress() +
				" port: " + location.getPort().toString());

		// Connect
		InetAddress addr;
		try {
			addr = InetAddress.getByName(location.getAddress());
		} catch (UnknownHostException e) {
			log.warning("Unable to notify client of message: could not resolve user location host: " + location.getAddress());
			return;
		}
		InetSocketAddress buddyaddr = new InetSocketAddress(addr,location.getPort());
		Socket s = new Socket();
		try {
			s.connect(buddyaddr);
		} catch (IOException e) {
			log.warning("Unable to notify client: could not connect to host: " +
					location.getAddress() + " port: " + location.getPort().toString());
			return;
		}
		
		// Handle headers and encryption
		// Create actual connection with streams
		Headers recvHeaders;
		InputStream input;
		OutputStream output;
		try {
			input = s.getInputStream();
			output = s.getOutputStream();
		} catch (IOException e) {
			log.warning("Could not get streams from socket");
			return;
		}

		// Transfer headers
		Headers sendHeaders = new Headers();
		sendHeaders.writeHeaders(output, em);
		try {
			recvHeaders = Headers.receive(input);
		} catch (IOException e2) {
			log.warning("IO Error from client during header negotiation:" + e2.getMessage());
			return;
		} catch (ProtocolException e2) {
			log.warning("Received invalid header from client (protocol error)");
			return;
		}

		// Make crypto data
		PeerCryptoData pcd;
		try {
			pcd = PeerCryptoData.negotiate(recvHeaders, em);
		} catch (GeneralSecurityException e) {
			
			return;
		}

		FractusPacket fp = null;
		// Serialize message
		byte[] serializedMessage;
		try {
			serializedMessage = message.serialize().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e2) {
			log.warning("Could not get unicode bytes from message passed in");
			return;
		}
		
		FractusPacket packet = new FractusPacket(serializedMessage, pcd.getSecretKeySpec());
		byte[] serializedPacket;
		try {
			serializedPacket = packet.serialize();
		} catch (GeneralSecurityException e1) {
				log.warning("Encountered General Security Exception while trying to serialize packet: " +
						e1.getMessage() + "(" + e1.getCause() + ")");
				return;
		} catch (IOException e) {
			log.warning("Could not write to byte array output stream to serialize key: " + e.getMessage());
			return;
		}
		
		try {
			output.write(serializedPacket);
		} catch (IOException e) {
			log.warning("Encountered IO problem sending serialized packet: " + e.getMessage());
		}

		fp = new FractusPacket(pcd.getSecretKeySpec());
		try {
			fp.readPacket(input, pcd.getEncodedKey(), em);
		}
		catch (UnsupportedEncodingException e) {
			log.warning("Could not deserialize UTF-8 string due to encoding issue");
			return;
		} catch (ProtocolException e) {
			log.warning("Encountered protocol exception while reading response");
			return;
		} catch (GeneralSecurityException e) {
			log.warning("Encountered General Security Exception while reading packet: " + e.getMessage());
			return;
		}

		try {
			s.close();
		} catch (IOException e) { }
		
		// TODO
		// Make sure response is correct
		
	}
}
