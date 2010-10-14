
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;

public class ClientConnector
        implements Runnable {
    private Socket socket;
    private final Object sendMutex;
    private EncryptionManager em;
    private InputStream input;
    private OutputStream output;
    private MessageHandler handler;
    private ClientCipher clientCipher;
    private String encodedPublicKey;
    private static Logger log;
    static {
        log = Logger.getLogger(ClientConnector.class.getName());
    }

    public ClientConnector(Socket socket, EncryptionManager em, MessageHandler handler) {
        this.socket = socket;
        this.em = em;
        this.handler = handler;
        sendMutex = new Object();
    }

    public void disconnect() {
        log.info("Disconnecting");
        try {
            socket.close();
        } catch (IOException e) {
        }
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
        // Make Message
        ProtocolBuffer.PublicKey pk =
                ProtocolBuffer.PublicKey.newBuilder().setEncoding(em.getEncodingFormat()).setPublicKey(ByteString.copyFrom(em.getPublicKey())).build();

        // Make FractusMessage
        FractusMessage fm = FractusMessage.build(pk);
        FractusPacket fp = new FractusPacket(fm.getSerialized());

        // Write serialized cipher data to client
        output.write(fp.serialize());
    }

    private void negotiateCipherData() throws IOException, GeneralSecurityException {
        // Get the packet
        log.debug("Negotiating symmetric key (waiting for remote side)");
        FractusPacket fp = FractusPacket.read(input);
        if (fp == null) {
            log.warn("Unable to retrieve remote public key.  Disconnecting.");
            disconnect(); return;
        }
        ProtocolBuffer.PublicKey remotePKPB = ProtocolBuffer.PublicKey.parseFrom(fp.getContents());

        String remotePKEncoding = remotePKPB.getEncoding();
        byte[] remotePKData = remotePKPB.getPublicKey().toByteArray();

        clientCipher = new ClientCipher(remotePKEncoding, remotePKData, em);
    }

    @Override
    public void run() {
        log.info("ClientConnector alive");
        connectStreams();
        try {
            publishCipherData();
        } catch (IOException ex) {
            log.warn("Could not publish cipher data to remote client", ex);
            disconnect();
            return;
        }

        try {
            negotiateCipherData();
        } catch (IOException ex) {
            log.warn("Could not receive cipher data from remote client", ex);
            disconnect();
            return;
        } catch (GeneralSecurityException ex) {
            log.warn("Encountered security exception while negotiating key", ex);
            disconnect(); return;
        }
        
        serveConnection();
    }

    private void serveConnection() {
        log.info("Entering main client service loop");
        while (socket.isConnected()) {
            log.debug("Waiting for packet");
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

            log.debug("Received packet");
            handler.handle(fp, this);
        }
    }

    public void sendMessage(FractusMessage message) throws
            IllegalBlockSizeException, BadPaddingException, IOException {
        log.debug("ClientConnector will send FractusMessage: " + message.getDescriptorName());
        byte[] plainText = message.getSerialized();
        byte[] cipherText = clientCipher.encrypt(plainText);
        FractusPacket sendPacket = new FractusPacket(cipherText);

        synchronized(sendMutex) {
            output.write(sendPacket.serialize());
        }
    }
}
