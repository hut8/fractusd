package fractus.main;


import fractus.crypto.EncryptionManager;
import fractus.crypto.ServerKeyManager;
import fractus.net.FractusConnector;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.Security;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

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
        log.info("Binding to port " + bindAddr.getPort());
        try {
            serverSock.bind(bindAddr);
        } catch (IOException e) {
            log.error("Could not bing to socket", e);
            System.exit(-1);
        }
        log.info("Waiting for connections");
        
        while (serverSock.isBound()) {
            try {
                Socket clientSocket = serverSock.accept();
                log.info("Accepted connection from " + clientSocket.getInetAddress().getHostAddress());
                FractusConnector newconnection =
                        new FractusConnector(clientSocket, em);
                new Thread(newconnection).start();
            } catch (IOException e) {
                log.warn("Could not accept client connection", e);
                continue;
            }
        }
    }

    public static void main(String[] args)
    throws Exception {
        // Setup Logging
        PropertyConfigurator.configure(
        		Class.class.getResource("/log4j.properties")
        		);

        log = Logger.getLogger(FractusServer.class.getName());

        log.info("Fractus Daemon");
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Runtime information
        Runtime runtime = Runtime.getRuntime();
        log.info("Available processors: " + runtime.availableProcessors());
        log.info("Max VM Memory: " + runtime.maxMemory());

        // TODO: DO NOT FAKE PASSWORD
//        Console cons = System.console();
//        if (cons == null) {
//            log.error("This application requires a real console.  Do not run from debugger.");
//            return;
//        }

        char[] passwd = null;

        /* check for just generating key */
        if (args.length > 0) {
            if (args[0].equals("keygen")) {
                if (args.length != 2) {
                    System.out.println("Usage: FractusServer keygen <outfile>");
                    return;
                }
                //if (cons != null) {
                //    System.out.println("===================================================");
                passwd = "1337".toCharArray(); 
                //cons.readPassword("%s", "Enter passphrase for key encryption (will not echo)");
                //} else {
                //    log.error("Could not read from secure console");
                //    return;
                //}
                try {
                	log.info("Generating ECDH Keypair");
                	KeyPair keyPair = ServerKeyManager.generateKey();
                	log.info("Encrypting ECDH Private Key and saving to " + args[1]);
                	ServerKeyManager.encryptKey(keyPair.getPrivate(), passwd, args[1]);
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
        KeyPair keyPair = ServerKeyManager.decryptKey(passwd, args[0]);
        EncryptionManager em = new EncryptionManager(keyPair);
        passwd = "1337".toCharArray();
        //passwd = cons.readPassword("%s", "Enter private key passphrase:");
        log.info("Overwriting password buffer");
        java.util.Arrays.fill(passwd, ' ');

        /* parse port */
        int port = 1337;
        if (args.length == 2) {
            port = Integer.parseInt(args[1]);
        }

        FractusServer server = new FractusServer(port, em);
        while(true) {
        	log.info("Calling serve routine");
        	server.serve();
        }
    }
}
