package fractus.main;


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

import fractus.crypto.EncryptionManager;
import fractus.domain.Location;

public class ClientNotifier
        implements Runnable {

    private FractusMessage message;
    private Location location;
    private EncryptionManager em;
    private Logger log;

    /**
     * Class to asynchronously send a message to a client
     * (for notifications of buddylist changes, etc).  Dies with
     * warning on error (does not requeue failed messages).
     * @param message
     */
    public ClientNotifier(FractusMessage message, Location location, EncryptionManager em) {
        this.message = message;
        this.location = location;
        this.em = em;
        this.log = Logger.getLogger(this.getClass().getName());
    }

    @Override
    public void run() {
        log.info("Client notifier thread alive for: " + location.getAddress()
                + " port: " + location.getPort().toString());

        // Connect
        InetAddress addr;
        try {
            addr = InetAddress.getByName(location.getAddress());
        } catch (UnknownHostException e) {
            log.warning("Unable to notify client of message: could not resolve user location host: " + location.getAddress());
            return;
        }
        InetSocketAddress buddyaddr = new InetSocketAddress(addr, location.getPort());
        Socket s = new Socket();
        try {
            s.connect(buddyaddr);
        } catch (IOException e) {
            log.warning("Unable to notify client: could not connect to host: "
                    + location.getAddress() + " port: " + location.getPort().toString());
            return;
        }
    }
}
