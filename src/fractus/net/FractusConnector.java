package fractus.net;

import fractus.main.MessageDescriptor;
import fractus.main.BinaryUtil;
import fractus.main.FractusMessage;
import fractus.main.EncryptionManager;
import fractus.main.FractusPacket;
import fractus.main.ClientCipher;
import com.google.protobuf.ByteString;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.log4j.Logger;
import fractus.strategy.PublicKeyStrategy;

public class FractusConnector
        implements Runnable {
    private static Logger log;
    static {
        log = Logger.getLogger(FractusConnector.class.getName());
    }
    
    private InetSocketAddress address;
    private EncryptionManager em;
    private InputStream input;
    private OutputStream output;
    private PacketHandler handler;
    private Socket socket;
    private final ConcurrentLinkedQueue<FractusMessage> queue;
    private Thread consumer;
    private ClientCipher clientCipher;

    public FractusConnector(Socket socket, EncryptionManager em) {
        this.socket = socket;
        this.em = em;
        handler = new PacketHandler();
        queue = new ConcurrentLinkedQueue<FractusMessage>();
        clientCipher = new ClientCipher(em);
        // Set up the handler to receive only public key messages and nothing else
        handler.register(new MessageDescriptor(MessageDescriptor.PUBLIC_KEY),
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
        log.debug("Constructing Public Key Message");
        // Make Message
        ProtocolBuffer.PublicKey pk =
                ProtocolBuffer.PublicKey.newBuilder()
                .setEncoding(em.getEncodingFormat())
                .setPublicKey(ByteString.copyFrom(em.getPublicKey()))
                .build();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        pk.writeTo(os);
        log.debug("Serialized Public Key Message: " + BinaryUtil.encodeData(pk.toByteArray()));

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
            publishCipherData();
        } catch (IOException ex) {
            log.warn("Could not publish cipher data to remote client", ex);
            disconnect();
            return;
        }

        // Create consumer of locally enqueued messages
        createConsumer();
        // Enter infinite service loop
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

            log.debug("Received packet [" + fp.getContents().length + " B]:" +
            BinaryUtil.encodeData(fp.getContents()));

            log.debug("Dispatching to handler");
            handler.handle(fp);
        }
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
        consumer = new Thread() {
            @Override
            public void run() {
                // TODO: Wait until cipher is initialized for sure...
                while (true) {
                    try {
                        synchronized (consumer) {
                            log.info("Pausing to be awoken to process message...");
                            consumer.wait();
                        }
                    } catch (InterruptedException e) {
                        log.info("Consumer awoken");
                    }

                    synchronized (queue) {
                        while (!FractusConnector.this.queue.isEmpty()) {
                            FractusMessage fm = FractusConnector.this.queue.remove();
                            byte[] plainText = fm.getSerialized();
                            byte[] cipherText;
                            try {
                                cipherText = clientCipher.encrypt(plainText);
                            } catch (IllegalBlockSizeException ex) {
                                java.util.logging.Logger.getLogger(FractusConnector.class.getName()).log(Level.SEVERE, null, ex);
                                return;
                            } catch (BadPaddingException ex) {
                                java.util.logging.Logger.getLogger(FractusConnector.class.getName()).log(Level.SEVERE, null, ex);
                                return;
                            }
                            FractusPacket sendPacket = new FractusPacket(cipherText);

                            try {
                                output.write(sendPacket.serialize());
                            } catch (UnsupportedEncodingException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // This could be fixed
                                FractusConnector.this.queue.add(fm);
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        };
        consumer.start();
    }
}
