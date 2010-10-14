
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;

public class ClientConnector
        implements Runnable {

    private Socket socket;
    private EncryptionManager em;
    private Object sendMutex;
    private InputStream input;
    private OutputStream output;
    private PacketHandler handler;
    private ClientCipher clientCipher;
    private String encodedPublicKey;
    private Logger log;

    public ClientConnector(Socket socket, EncryptionManager em, PacketHandler handler) {
        this.socket = socket;
        this.em = em;
        this.handler = handler;
        this.log = Logger.getLogger(this.getClass().getName());
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

    private void receiveCipherData() throws IOException {
        // Get the packet
        FractusPacket fp = FractusPacket.read(input);
        fp.getContents();

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
            receiveCipherData();
        } catch (IOException ex) {
            log.warn("Could not receive cipher data from remote client", ex);
            disconnect();
            return;
        }

        PeerCryptoData pcd;
        try {
            pcd = PeerCryptoData.negotiate(recvHeaders, em);
        } catch (GeneralSecurityException e) {
            log.warning("Encountered general security exception while negotiating peer crypto data: " + e.getMessage());
            disconnect();
            return;
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
            handler.handle(fp, this);
        }
    }

    public void sendMessage(FractusMessage message)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
        sendMessage(message.serialize());
    }
}
