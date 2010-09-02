
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class ClientConnector
implements Runnable {
	private Socket socket;
	private EncryptionManager em;
	private Object sendMutex;
	private InputStream input;
	private OutputStream output;
	private PacketHandler handler;
	private SecretKeySpec sks;
	private String encodedPublicKey;
	private Logger log;

	public ClientConnector(Socket socket, EncryptionManager em, PacketHandler handler) {
		this.socket = socket;
		this.em = em;
		this.handler = handler;
		this.log = Logger.getLogger(this.getClass().getName());
		sendMutex = new Object();
	}
	
	public void sendMessage(String message)
	throws IOException {
		byte[] serializedMessage = message.getBytes("UTF-8");
		sendMessage(serializedMessage);
	}
	
	public void sendMessage(byte[] message)
	throws IOException {
		FractusPacket packet = new FractusPacket(message, sks);
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
			synchronized (sendMutex) {
				output.write(serializedPacket);
			}
		} catch (IOException e) {
			log.warning("Encountered IO problem sending serialized packet: " + e.getMessage());
		}
	}
	
	public void disconnect() {
		log.info("Disconnecting");
		try {
			socket.close();
		} catch (IOException e) { }
	}
	
	private void connectStreams() {
		try {
			output = socket.getOutputStream();
			input = socket.getInputStream();
		} catch (IOException e) {
			log.warning("Could not get streams from socket: " + e.getLocalizedMessage());
			disconnect();
		}
	}
	
	@Override
	public void run() {
		log.info("ClientConnector alive");
		
		connectStreams();
		// Send headers
		Headers sendHeaders = new Headers();
		sendHeaders.writeHeaders(output, em);
		
		// Receive headers
		Headers recvHeaders;
		try {
			recvHeaders = Headers.receive(input);
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "error: encountered I/O error while trying to receive headers: " + e.getLocalizedMessage());
			disconnect(); return;
		} catch (ProtocolException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "error: encountered protocol error while trying to receive headers: " + e.getLocalizedMessage());
			disconnect(); return;
		}
		
		PeerCryptoData pcd;
		try {
			pcd = PeerCryptoData.negotiate(recvHeaders, em);
		} catch (GeneralSecurityException e) {
			log.warning("Encountered general security exception while negotiating peer crypto data: " + e.getMessage());
			disconnect(); return;
		}

		if (pcd == null) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "error: could not receive peer cryptographic data.  disconnecting.");
			disconnect();
		}
		sks = pcd.getSecretKeySpec();
		encodedPublicKey = pcd.getEncodedKey();
		serveConnection();
	}
	
	private void serveConnection() {
		while (socket.isConnected()) {
			Logger.getAnonymousLogger().log(Level.INFO, "waiting for packet");
			FractusPacket fp = new FractusPacket(sks);
			try {
				fp.readPacket(input, encodedPublicKey, em);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Logger.getAnonymousLogger().log(Level.INFO, "received fractus packet");
			handler.handle(fp,this);
		}
	}

	public void sendMessage(FractusMessage message)
	throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
		sendMessage(message.serialize());
	}
}
