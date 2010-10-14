/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import com.google.protobuf.Message;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.*;

/**
 *
 * @author bowenl2
 */
public class FractusMessage {
    // Key Management

    public static short PUBLIC_KEY = 0x01a4;
    public static short PUBLISH_KEY_REQ = 0x0bbb;
    public static short PUBLISH_KEY_RES = 0x18d9;
    public static short REVOKE_KEY_REQ = 0x6a3c;
    public static short REVOKE_KEY_RES = 0x6df3;
    public static short IDENTIFY_KEY_REQ = 0x739e;
    public static short IDENTIFY_KEY_RES = 0x0c80;
    // Locations
    public static short REGISTER_LOCATION_REQ = 0x533a;
    public static short UNREGISTER_LOCATION_REQ = 0x7224;
    // Contacts
    public static short CONTACT_DATA_REQ = 0x33ea;
    public static short CONTACT_DATA_RES = 0x38cf;
    public static short ADD_CONTACT_REQ = 0x7984;
    public static short ADD_CONTACT_RES = 0x5d77;
    public static short ADD_CONTACT_NOTICE = 0x13e6;
    public static short REMOVE_CONTACT_REQ = 0x1aa3;
    public static short REMOVE_CONTACT_RES = 0x2fde;
    public static short ONEWAY_CONTACT_REQ = 0x5cec;
    public static short ONEWAY_CONTACT_RES = 0x4c7e;
    // IMs
    public static short INSTANT_MESSAGE = 0x5789;
    // Status
    public static short PUBLISH_STATUS = 0x448d;

    public static boolean validateDescriptor(short type) {
        return descriptorMap.containsKey(type);
    }
    private final static Logger log = Logger.getLogger(FractusMessage.class);
    private final static HashMap<Short, String> descriptorMap;
    private final static HashMap<Class, Short> typeDescriptorMap;

    static {
        descriptorMap = new HashMap<Short, String>();
        typeDescriptorMap = new HashMap<Class, Short>();
        Field[] fields = FractusMessage.class.getDeclaredFields();
        for (Field f : fields) {
            if (!Modifier.isStatic(f.getModifiers())) {
                log.debug("Found instance field: " + f.getName());
                continue;
            }

            try {
                descriptorMap.put(f.getShort(null), f.getName());
                log.debug("Mapping " + f.getShort(null) + " to " + f.getName());
            } catch (IllegalArgumentException ex) {
                log.error("Unable to construct MessageType map.", ex);
            } catch (IllegalAccessException ex) {
                log.error("Unable to construct MessageType map", ex);
            }
        }

        typeDescriptorMap.put(ProtocolBuffer.PublicKey.class, PUBLIC_KEY);
    }

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
    private byte[] serialized;

    public byte[] getSerialized() {
        return serialized;
    }

    // Factory method
    public static FractusMessage build(Message message) {
        FractusMessage prototype = new FractusMessage();
        prototype.sequenceNumber = 0;
        // Determine first two bytes
        Class sourceClass = message.getClass();
        Short sourceDescriptor = typeDescriptorMap.get(sourceClass);
        prototype.descriptor = sourceDescriptor;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeShort(sourceDescriptor);
            dos.writeInt(0);
            message.writeTo(baos);
        } catch (IOException ex) {
            log.warn("Cannot serialize FractusMessage.", ex);
        }
        prototype.serialized = baos.toByteArray();

        return prototype;
    }
}
