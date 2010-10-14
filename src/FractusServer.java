
import java.io.Console;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.w3c.dom.Element;

public class FractusServer {

    ServerSocket serverSock;
    InetSocketAddress bindAddr;
    EncryptionManager em;
    UserTracker tracker;
    private static Logger log;

    public FractusServer(int port, EncryptionManager em)
            throws IOException {
        this.em = em;
        this.tracker = new UserTracker();
        log.debug("Creating server socket");
        this.serverSock = new ServerSocket();
        log.info("Server socket created");
        log.debug("Setting socket option: reuse address");
        try {
            this.serverSock.setReuseAddress(true);
        } catch (SocketException e) {
            log.error("Fatal error: could not set socket option", e);
            System.exit(-1);
        }
        bindAddr = new InetSocketAddress(port);
    }

    public void serve() {
        log.info("Binding socket...");
        try {
            serverSock.bind(bindAddr);
        } catch (IOException e) {
            System.err.println("Fatal error: could not bind to socket");
            System.exit(-1);
        }
        log.debug("Creating callbacks");
        PacketHandler handler = new PacketHandler(generateCallbackMap(), tracker);
        log.info("Waiting for connections");

        while (serverSock.isBound()) {
            try {
                Socket clientSock = serverSock.accept();
                log.info("Accepted connection from " + clientSock.getInetAddress().getHostAddress());
                new Thread(new ClientConnector(clientSock, em, handler)).start();
            } catch (IOException e) {
                log.warn("Could not accept client connection", e);
                continue;
            }
        }
    }

    public static void main(String[] args) {
        // Setup Logging
        PropertyConfigurator.configure(FractusServer.class.getClassLoader().getResource("log4j.properties"));

        log = Logger.getLogger(FractusServer.class.getName());
        log.info("Fractus Daemon");
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Runtime information
        Runtime runtime = Runtime.getRuntime();
        log.info("Available processors: " + runtime.availableProcessors());
        log.info("Max VM Memory: " + runtime.maxMemory());

        Console cons = System.console();
        if (cons == null) {
            log.error("This application requires a real console.  Do not run from debugger.");
            return;
        }

        char[] passwd = null;

        /* check for just generating key */
        if (args.length > 0) {
            if (args[0].equals("keygen")) {
                if (args.length != 2) {
                    System.out.println("Usage: FractusServer keygen <outfile>");
                    return;
                }
                if (cons != null) {
                    System.out.println("===================================================");
                    passwd = cons.readPassword("%s", "Enter passphrase for key encryption (will not echo)");
                } else {
                    log.error("Could not read from secure console");
                    return;
                }
                log.info("Generating key to " + args[1]);
                try {
                    EncryptionManager.generateKey(args[1], passwd);
                } catch (IOException e) {
                    log.error("Fatal error: IO Exception while generating key: " + e.getMessage());
                    return;
                } catch (GeneralSecurityException e) {
                    log.error("Fatal error: Security exception while generating key: " + e.getMessage());
                    log.error("REMEMBER TO INSTALL UNLIMITED STRENGTH JURISDICTION POLICY FILES.");
                    return;
                }
                log.info("Key generation successful.");
                return;
            }
        }

        if (!(args.length == 1 || args.length == 2)) {
            log.info("Usage: FractusServer <keyfile> [<port>]");
            return;
        }

        log.info("Processing cryptographic key");
        EncryptionManager em = null;

        passwd = cons.readPassword("%s", "Enter private key passphrase:");
        try {
            em = new EncryptionManager(args[0], passwd);
        } catch (IOException e) {
            log.error("Fatal error: IO Exception while reading key: " + e.getMessage());
            return;
        } catch (GeneralSecurityException e) {
            log.error("Fatal error: Security exception while creating encryption manager: " + e.getMessage());
            return;
        } catch (ClassNotFoundException e) {
            log.error("Fatal error: encountered invalid KeyPair object in decrypted Key file");
            return;
        }

        log.info("Overwriting password buffer");
        java.util.Arrays.fill(passwd, ' ');

        /* parse port */
        int port = 1337;
        if (args.length == 2) {
            port = Integer.parseInt(args[1]);
        }

        FractusServer server;
        try {
            server = new FractusServer(port, em);
        } catch (IOException e) {
            log.error("Could not allocate socket resources");
            return;
        }
        server.serve();
    }

    private HashMap<String, Callback> generateCallbackMap() {
        HashMap<String, Callback> map = new HashMap<String, Callback>();

        map.put("register-location", new Callback() {

            public void dispatch(FractusMessage response, String sender, Element message, ClientConnector fc, UserTracker tracker) {
                String clientAddress = message.getAttribute("address");
                String clientPort = message.getAttribute("port");
                tracker.registerLocation(response, sender, clientAddress, clientPort, fc);
            }
        });

        map.put("invalidate-location", new Callback() {

            public void dispatch(FractusMessage response, String sender, Element message, ClientConnector fc, UserTracker tracker) {
                String clientAddress = message.getAttribute("address");
                String clientPort = message.getAttribute("port");
                tracker.invalidateLocation(response, sender, clientAddress, clientPort);
            }
        });

        map.put("send-contact-data", new Callback() {

            public void dispatch(FractusMessage response, String sender, Element message, ClientConnector fc, UserTracker tracker) {
                tracker.sendContactData(response, sender, fc);
            }
        });

        map.put("add-contact", new Callback() {

            public void dispatch(FractusMessage response, String sender, Element message, ClientConnector fc, UserTracker tracker) {
                String targetUser = message.getAttribute("target");
                tracker.addContact(response, sender, targetUser, fc, em);
            }
        });

        map.put("remove-contact", new Callback() {

            public void dispatch(FractusMessage response, String sender, Element message, ClientConnector fc, UserTracker tracker) {
                String targetUser = message.getAttribute("target");
                tracker.removeContact(response, sender, targetUser, fc);
            }
        });

        map.put("register-key", new Callback() {

            public void dispatch(FractusMessage response, String sender, Element message, ClientConnector fc, UserTracker tracker) {
                String encodedKey = message.getAttribute("key");
                tracker.registerKey(response, sender, encodedKey, fc);
            }
        });

        map.put("identify-key", new Callback() {

            public void dispatch(FractusMessage response, String sender, Element message, ClientConnector fc, UserTracker tracker) {
                String encodedKey = message.getAttribute("key");
                tracker.identifyKey(response, encodedKey, fc);
            }
        });

        return map;
    }
}
