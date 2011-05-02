package fractus.net;

import fractus.crypto.ClientCipher;
import fractus.crypto.EncryptionManager;
import fractus.main.MessageDescriptor;
import fractus.main.BinaryUtil;
import fractus.main.FractusMessage;
import fractus.main.FractusPacket;
import com.google.protobuf.ByteString;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.log4j.Logger;
import org.bouncycastle.crypto.InvalidCipherTextException;

import fractus.strategy.PublicKeyStrategy;

public class FractusConnector
implements Runnable {
	private static Logger log;
	static {
		log = Logger.getLogger(FractusConnector.class.getName());
	}

	private EncryptionManager encryptionManager;
	private InputStream input;
	private OutputStream output;
	private PacketHandler handler;
	private Socket socket;
	private final ConcurrentLinkedQueue<FractusMessage> queue;
	private Thread consumerThread;
	private MessageConsumer messageConsumer;
	private ClientCipher clientCipher;

	public FractusConnector(Socket socket, EncryptionManager em) {
		this.socket = socket;
		this.encryptionManager = em;
		this.handler = new PacketHandler();
		this.queue = new ConcurrentLinkedQueue<FractusMessage>();
		this.clientCipher = new ClientCipher(em);
		
		// Set up the handler to receive only CipherCapabilities messages
		
		// Set up the handler to receive only public key messages and nothing else
		handler.register(new MessageDescriptor(MessageDescriptor.HANDSHAKE_DATA),
				new PublicKeyStrategy(this, clientCipher));
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
			log.warn("Could not get streams from socket", e);
			disconnect();
		}
	}
	
	private void publishCipherData() throws IOException {
		log.debug("Constructing Handshake Data");

		// Make Message
		ProtocolBuffer.HandshakeData pk =
			ProtocolBuffer.HandshakeData.newBuilder()
			.setPublicKeyEncoding(encryptionManager.getEncodingFormat())
			.setPublicKey(ByteString.copyFrom(encryptionManager.getEncodedPublicKey()))
			.setNonce(ByteString.copyFrom(clientCipher.getLocalNonce().getData()))
			.build();

//		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		pk.writeTo(os);
//		log.debug("Serialized Public Key Message: " + BinaryUtil.encodeData(pk.toByteArray()));

		// Make FractusMessage
		FractusMessage fm = FractusMessage.build(pk);
		byte[] sm = fm.getSerialized();
		log.debug("FractusMessage, serialized, is: " + BinaryUtil.encodeData(sm));
		log.debug("Publishing Key: FractusMessage has tag: " + fm.getDescriptorName());
		log.debug("Publishing Key: FractusMessage is: " + sm.length + " bytes");
		FractusPacket fp = new FractusPacket(sm);

		// Write serialized cipher data to client
		byte[] sp = fp.serialize();
		log.debug("Sending via socket: " + BinaryUtil.encodeData(sp));
		output.write(sp);
	}

	@Override
	public void run() {
		log.info("ClientConnector alive");
		connectStreams();
		try {
			publishHeader();
			receiveHeader();
			publishCipherData();
		} catch (IOException ex) {
			log.warn("Could not publish header / cipher data to remote client", ex);
			disconnect();
			return;
		}

		// Create consumer of locally enqueued messages
		createConsumer();
		// Enter infinite service loop
		serveConnection();
	}

	private void publishHeader()
	throws UnsupportedEncodingException, IOException {
		output.write("FRACTUS 0".getBytes("UTF-8"));
	}

	private void receiveHeader() {
		Scanner headerScanner = new Scanner(input);
		try {
			String protocolName = headerScanner.next();
			if (!"FRACTUS".equals(protocolName)) {
				log.warn("Remote client sent invalid protocol name: " + protocolName);
				disconnect();
				return;
			}
			headerScanner.skip("\\s+"); // Skip whitespace
			int protocolVersion = headerScanner.nextInt();    // Protocol Version
			if (protocolVersion > 0) {
				log.info("");
			}
			log.debug("Using FRACTUS protocol version " + protocolVersion);
		} catch (Exception e) {
			log.warn("Protocol error while receiving headers");
			disconnect();
			return;
		}
	}

	private void serveConnection() {
		log.debug("Entering main client service loop");
		while (socket.isConnected()) {
			log.debug("Waiting for packet from remote side");
			FractusPacket fp;
			try {
				fp = FractusPacket.read(input);
			} catch (IOException ex) {
				log.info("Remote host disconnected", ex);
				disconnect(); return;
			}

			if (fp == null) {
				log.info("Received null packet.  Disconnecting.");
				disconnect(); return;
			}

			log.debug("Received packet [" + fp.getContents().length + " B]:" +
					BinaryUtil.encodeData(fp.getContents()));

			log.debug("Dispatching to handler");
			handler.handle(fp);
		}
		messageConsumer.shutdown();
	}

	public void sendMessage(FractusMessage message) throws
	IllegalBlockSizeException, BadPaddingException, IOException {
		log.debug("Adding to queue: " + message.getDescriptorName());
		queue.add(message);
		synchronized(queue) {
			queue.notifyAll();
		}
	}

	private void createConsumer() {
		this.messageConsumer = new MessageConsumer();
		this.consumerThread = new Thread();
		this.consumerThread.start();
	}

	private class MessageConsumer
	implements Runnable {
		boolean active;
		
		public MessageConsumer() {
			this.active = true;
		}
		public void shutdown() {
			log.debug("Received request to shut down");
			active = false;
			synchronized (consumerThread) {
				consumerThread.notifyAll();	
			}
		}
		
		@Override
		public void run() {
			while (active) {
				try {
					// NOTE: I think this is "queue" but might be "consumer"
					synchronized (queue) {
						log.debug("Pausing to be awoken to process message...");
						queue.wait();
					}
				} catch (InterruptedException e) {
					log.debug("Consumer awoken");
				}
				if (!active) {
					log.info("Consumer: shutting down");
					break;
				}
				synchronized (queue) {
					while (!FractusConnector.this.queue.isEmpty()) {
						FractusMessage fm = FractusConnector.this.queue.remove();
						byte[] plainText = fm.getSerialized();
						byte[] cipherText;
							try {
								cipherText = clientCipher.encrypt(plainText);
							} catch (IllegalStateException e1) {
								log.warn("",e1);
								disconnect();
								break;
							} catch (InvalidCipherTextException e1) {
								log.warn("[run]",e1);
								disconnect();
								break;
							}
						FractusPacket sendPacket = new FractusPacket(cipherText);

						try {
							output.write(sendPacket.serialize());
						} catch (IOException e) {
							FractusConnector.this.queue.add(fm);
							e.printStackTrace();
						}
					}
				}
			}
		}
	};    	
}
