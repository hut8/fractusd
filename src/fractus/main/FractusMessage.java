package fractus.main;




import com.google.protobuf.Message;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.log4j.*;

/**
 *
 * @author bowenl2
 */
public class FractusMessage {
    // Explicit private constructor
    private FractusMessage() {
    }
    
    private Integer sequenceNumber;
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    private Short descriptor;
    public Short getDescriptor() {
        return descriptor;
    }

    public String getDescriptorName() {
        return MessageDescriptor.getDescriptorName(this);
    }
    private byte[] serialized;
    public byte[] getSerialized() {
        return serialized;
    }

    private static Logger log;
    static {
        log = Logger.getLogger(FractusMessage.class);
    }

    // Factory method
    public static FractusMessage build(Message message) {
        log.debug("Constructing FractusMessage from messsage of type " + message.getClass().getName());
        FractusMessage prototype = new FractusMessage();
        // Determine first two bytes
        Short sourceDescriptor = MessageDescriptor.getDescriptor(message);
        if (sourceDescriptor == null) {
            log.error("Could not find descriptor for message of class " + message.getClass().getName() + " - Cannot build.");
            return null;
        }
        log.debug("Prepending descriptor: " + sourceDescriptor);
        prototype.descriptor = sourceDescriptor;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeShort(sourceDescriptor);
            message.writeTo(baos);
        } catch (IOException ex) {
            log.warn("Cannot serialize FractusMessage.", ex);
            return null;
        }
        prototype.serialized = baos.toByteArray();
        log.debug("FractusMessage built successfully.");
        return prototype;
    }

//    public FractusMessage(FractusPacket packet) throws IOException {
//        byte[] packetContents = packet.getContents();
//        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packetContents));
//        Short packetDescriptor = dis.readShort();
//        // Validate and assign descriptor
//        //ProtocolBuffer.PublicKey.newBuilder().
//
//        //Integer sequence = dis.readInt();
//
//    }
    
}
