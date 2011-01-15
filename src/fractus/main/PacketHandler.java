package fractus.main;



import fractus.strategy.PacketStrategy;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class PacketHandler {
    private static Logger log = Logger.getLogger(PacketHandler.class.getName());
    private Map<MessageDescriptor, PacketStrategy> strategyMap;
	

    public PacketHandler() {
        strategyMap = new HashMap<MessageDescriptor, PacketStrategy>();
    }

    public synchronized void register(MessageDescriptor md, PacketStrategy s) {
        log.debug("Registering descriptor strategy: " + md.getName() + " to: " + s.toString());
        strategyMap.put(md, s);
    }

    public synchronized void handle(FractusPacket packet) {
        ByteArrayInputStream bais = new ByteArrayInputStream(packet.getContents());

        // Determine MessageDescriptor
        DataInputStream dis = new DataInputStream(bais);
        Short descriptorValue;
        try {
            descriptorValue = dis.readShort();
        } catch (IOException ex) {
            log.warn("Not handling packet - could not determine descriptor", ex);
            return;
        }

        MessageDescriptor descriptor = new MessageDescriptor(descriptorValue);
        if (!strategyMap.containsKey(descriptor)) {
            log.warn("There was no handler for descriptor: " + descriptor);
            return;
        }
        PacketStrategy strategy = strategyMap.get(descriptor);


        // Get rest of message contents
        byte[] messageData = new byte[bais.available()];
        try {
            bais.read(messageData);
        } catch (IOException ex) {
            log.warn("Could not read contents of packet from memory");
            return;
        }

        log.debug("Dispatching message contents [" + messageData.length + "] to strategy object: " + strategy.toString());
        strategy.dispatch(messageData);
    }
}