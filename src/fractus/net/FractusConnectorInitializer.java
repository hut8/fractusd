package fractus.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.protobuf.ByteString;
import com.sun.org.apache.xml.internal.security.encryption.AgreementMethod;

import fractus.crypto.ClientCipher;
import fractus.crypto.EncryptionManager;
import fractus.main.FractusMessage;
import fractus.main.MessageDescriptor;
import fractus.net.ProtocolBuffer.CipherSuite;
import fractus.strategy.CipherCapabilitiesStrategy;
import fractus.strategy.HandshakeDataStrategy;

/**
 * Initializes connection for FractusConnector
 * Handles header, cryptography negotiation, PacketHandler initialization
 * @author 14581
 *
 */
public class FractusConnectorInitializer {
	private static Logger log;
	static {
		log = Logger.getLogger(FractusConnectorInitializer.class.getName());
	}

	private FractusConnector fractusConnector;
	private EncryptionManager encryptionManager;
	private ClientCipher clientCipher;
	private OutputStream outputStream;
	private InputStream inputStream;
	private PacketHandler packetHandler;
	private HandshakeDataStrategy handshakeDataStrategy;

	public FractusConnectorInitializer(FractusConnector fractusConnector,
			EncryptionManager encryptionManager,
			ClientCipher clientCipher,
			OutputStream outputStream,
			InputStream inputStream) {
		this.fractusConnector = fractusConnector;
		this.encryptionManager = encryptionManager;
		this.clientCipher = clientCipher;
		this.outputStream = outputStream;
		this.inputStream = inputStream;
		this.packetHandler = this.fractusConnector.getPacketHandler();
	}

	public void initialize()
	throws IOException {
		createStrategies();
		negotiateHeaders();
		publishCipherCapabilities();
		fractusConnector.syncReceiveMessage(); // Wait for other cipher capabilities
		publishHandshakeData();
		fractusConnector.syncReceiveMessage(); // Wait for other handshake data
	}

	private String remoteVersion;
	public String getRemoteVersion() { return remoteVersion; }

	private void createStrategies() {
		this.handshakeDataStrategy = new HandshakeDataStrategy(fractusConnector, clientCipher);
		this.packetHandler.register(
				new MessageDescriptor(MessageDescriptor.CIPHER_CAPABILITIES),
				new CipherCapabilitiesStrategy(this));
		this.packetHandler.register(new MessageDescriptor(MessageDescriptor.HANDSHAKE_DATA),
				this.handshakeDataStrategy);
	}
	
	private void negotiateHeaders()
	throws IOException {
		log.debug("Negotiating headers");
		DataOutputStream dos = new DataOutputStream(this.outputStream);
		dos.writeUTF("FRACTUS 1\n\n");
		dos.flush();

		BufferedReader reader = new BufferedReader(new InputStreamReader(this.inputStream));
		this.remoteVersion = reader.readLine();
		if (!"FRACTUS 1".equals(this.remoteVersion)) {
			log.warn("Received different protocol version: " + this.remoteVersion);
		}
		while (reader.readLine() != "") { }
	}

	private void publishCipherCapabilities()
	throws IOException {
		FractusMessage fractusMessage =
			FractusMessage.build(this.encryptionManager.getCipherCapabilities());
		this.fractusConnector.syncSendMessage(fractusMessage);
	}

	public void receiveCipherCapabilities(List<CipherSuite> cipherSuites) {
		// Now that this packet has come in, we should forget how to handle it
		this.packetHandler.unregister(new MessageDescriptor(MessageDescriptor.CIPHER_CAPABILITIES));
		
		ProtocolBuffer.CipherSuite localCipherSuite =
			this.encryptionManager.getCipherCapabilities().getCipherSuites(0);
		ProtocolBuffer.CipherSuite agreedCipherSuite = null;

		Iterator<CipherSuite> cipherItr = cipherSuites.iterator();
		while (cipherItr.hasNext() && agreedCipherSuite == null) {
			CipherSuite trialSuite = cipherItr.next();
			if (trialSuite.getCipherAlgorithm().equalsIgnoreCase(localCipherSuite.getCipherAlgorithm()) &&
					(trialSuite.getCipherKeySize() == localCipherSuite.getCipherKeySize()) &&
					(trialSuite.getCipherMode().equalsIgnoreCase(localCipherSuite.getCipherMode())) &&
					(trialSuite.getPublicKeyType().equalsIgnoreCase(localCipherSuite.getPublicKeyType())) &&
					(trialSuite.getKeyDerivationFunction().equalsIgnoreCase(localCipherSuite.getKeyDerivationFunction())) &&
					(trialSuite.getSecretEstablishmentAlgorithm().equalsIgnoreCase(localCipherSuite.getSecretEstablishmentAlgorithm()))
			) {
				log.debug("Found compatible ciphersuite");
				agreedCipherSuite = trialSuite;
				this.handshakeDataStrategy.agreedCipherSuite();
				break;
			}	else {
				log.debug("Found incompatible ciphersuite");
			}
		}

		if (agreedCipherSuite == null) {
			log.warn("No supported CipherSuite found");
			// TODO: Protocol Error
			fractusConnector.disconnect();
			return;
		}
	}

	private void publishHandshakeData() throws IOException {
		log.debug("Constructing Handshake Data");

		// Make Message
		ProtocolBuffer.HandshakeData handshakeData =
			ProtocolBuffer.HandshakeData.newBuilder()
			.setPublicKeyEncoding(encryptionManager.getEncodingFormat())
			.setPublicKey(ByteString.copyFrom(encryptionManager.getEncodedPublicKey()))
			.setNonce(ByteString.copyFrom(clientCipher.getLocalNonce().getData()))
			.build();

		fractusConnector.syncSendPlaintext(FractusMessage.build(handshakeData));
	}
}
