package fractus.net;

import fractus.crypto.ClientCipher;
import fractus.crypto.EncryptionManager;
import fractus.main.BinaryUtil;
import fractus.main.FractusMessage;
import fractus.main.FractusPacket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.log4j.Logger;
import org.bouncycastle.crypto.InvalidCipherTextException;

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
	public PacketHandler getPacketHandler() { return handler; }
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
	}

	public void disconnect() {
		log.info("Disconnecting");
		if (messageConsumer != null) {
			log.debug("Shutting down message consumer");
			messageConsumer.shutdown();
		}
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


	@Override
	public void run() {
		log.info("FractusConnector alive");
		connectStreams();

		// Initializer deals soley with synchronous socket communication
		FractusConnectorInitializer initializer =
			new FractusConnectorInitializer(this, this.encryptionManager, this.clientCipher,
					this.output, this.input);
		try {
			initializer.initialize();
		} catch (IOException e) {
			log.warn("Could not publish header / cipher data to remote client", e);
			disconnect();
			return;
		}

		// [Enter asynchronous mode]
		// Create consumer of locally enqueued messages
		createConsumer();
		// Enter infinite service loop
		serveConnection();
	}

	private void serveConnection() {
		log.debug("Entering main client service loop");
		while (socket.isConnected()) {
			log.debug("Waiting for packet from remote side");
			syncReceiveMessage();
		}
		messageConsumer.shutdown();
	}

	// Synchronous Send Methods
	public void syncSendPlaintext(FractusMessage fractusMessage)
	throws IOException {
		FractusPacket sendPacket = new FractusPacket(fractusMessage.getSerialized());
		syncSendPlaintext(sendPacket);
	}

	public void syncSendPlaintext(FractusPacket fractusPacket)
	throws IOException {
		output.write(fractusPacket.serialize());
	}

	public void syncSendMessage(FractusMessage message)
	throws IOException {
		log.debug("Sending message synchronously: " + message.getDescriptorName());
		byte[] plainText = message.getSerialized();
		byte[] cipherText = null;
		try {
			cipherText = clientCipher.encrypt(plainText);
		} catch (IllegalStateException e1) {
			log.warn("Could not encrypt cipher (not initialized)",e1);
			// TODO: Protocol Error
			disconnect();
		} catch (InvalidCipherTextException e1) {
			log.warn("Could not encrypt cipher (illegal ciphertext)",e1);
			// TODO: Protocol Error
			disconnect();
		}
		syncSendPlaintext(new FractusPacket(cipherText));
	}

	// Asynchronous send
	public void sendMessage(FractusMessage message) {
		log.debug("Adding to queue: " + message.getDescriptorName());
		queue.add(message);
		synchronized(queue) {
			queue.notifyAll();
		}
	}

	// Synchronous receive
	public void syncReceiveMessage() {
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
		
		byte[] contents;
		if (clientCipher.isInitialized()) {
			log.debug("Decrypting ciphertext");
			try {
				contents = clientCipher.decrypt(fp.getContents());
			} catch (IllegalStateException e) {
				log.warn("Unable to receive packet: invalid cipher state",e);
				this.disconnect(); return;
			} catch (InvalidCipherTextException e) {
				log.warn("Unable to receive packet: failed GMAC",e);
				this.disconnect(); return;
			}
		} else {
			log.debug("Interpreting packet as plaintext");
			contents = fp.getContents();
		}

		log.debug("Dispatching to handler");
		handler.handle(contents);	
		
		
		
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
			synchronized (queue) {
				queue.notifyAll();	
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
						try {
							syncSendMessage(fm);
						} catch (IOException e) {
							log.warn("Consumer: could not send message.",e);
							disconnect();
						}
					}
				}
			}
		}
	};    	
}
