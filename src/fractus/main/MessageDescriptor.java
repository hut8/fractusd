/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fractus.main;

import com.google.protobuf.Message;

import fractus.net.ProtocolBuffer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author bowenl2
 */
public class MessageDescriptor {
        // Key Management
    public final static short PUBLIC_KEY = 0x01a4;
    public final static short REGISTER_KEY_REQ = 0x0bbb;
    public final static short REGISTER_KEY_RES = 0x18d9;
    public final static short REVOKE_KEY_REQ = 0x6a3c;
    public final static short REVOKE_KEY_RES = 0x6df3;
    public final static short IDENTIFY_KEY_REQ = 0x739e;
    public final static short IDENTIFY_KEY_RES = 0x0c80;
    // Locations
    public final static short REGISTER_LOCATION_REQ = 0x533a;
    public final static short UNREGISTER_LOCATION_REQ = 0x7224;
    // Contacts
    public final static short CONTACT_DATA_REQ = 0x33ea;
    public final static short CONTACT_DATA_RES = 0x38cf;
    public final static short ADD_CONTACT_REQ = 0x7984;
    public final static short ADD_CONTACT_RES = 0x5d77;
    public final static short ADD_CONTACT_NOTICE = 0x13e6;
    public final static short REMOVE_CONTACT_REQ = 0x1aa3;
    public final static short REMOVE_CONTACT_RES = 0x2fde;
    public final static short ONEWAY_CONTACT_REQ = 0x5cec;
    public final static short ONEWAY_CONTACT_RES = 0x4c7e;
    // IMs
    public final static short INSTANT_MESSAGE = 0x5789;
    // Status
    public final static short PUBLISH_STATUS = 0x448d;


    public static boolean validateDescriptor(short type) {
        return descriptorMap.containsKey(type);
    }
    private final static Logger log = Logger.getLogger(MessageDescriptor.class);
    private final static HashMap<Short, String> descriptorMap;
    private final static HashMap<Class, Short> typeDescriptorMap;

    static {
        log.debug("Populating descriptor map");
        descriptorMap = new HashMap<Short, String>();
        typeDescriptorMap = new HashMap<Class, Short>();
        Field[] fields = MessageDescriptor.class.getDeclaredFields();
        log.debug("Found" + fields.length + " fields");
        for (Field f : fields) {
            if (!Modifier.isStatic(f.getModifiers())) {
                log.debug("Found instance field: " + f.getName());
                continue;
            }
            if (!f.getType().equals(short.class)) {
                log.debug("Found non-short field: " + f.getName());
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
        typeDescriptorMap.put(ProtocolBuffer.RegisterKeyReq.class, REGISTER_KEY_REQ);
        typeDescriptorMap.put(ProtocolBuffer.RegisterLocationReq.class, REGISTER_LOCATION_REQ);
    }

    public static String getDescriptorName(FractusMessage fractusMessage) {
        log.debug("Looking up descriptor for: " + fractusMessage.getDescriptor().toString());
        if (!descriptorMap.containsKey(fractusMessage.getDescriptor())) {
            log.warn("Could not find descriptor name for descriptor: " + fractusMessage.getDescriptor().toString());
            return "[UNKNOWN]";
        }
        return descriptorMap.get(fractusMessage.getDescriptor());
    }

    public static String getDescriptorName(Short descriptor) {
        if (!descriptorMap.containsKey(descriptor)) {
            log.warn("Could not find descriptor name for descriptor: " + descriptor);
            return "[UNKNOWN]";
        }
        return descriptorMap.get(descriptor);
    }

    public static Short getDescriptor(Message message) {
        return typeDescriptorMap.get(message.getClass());
    }

    private Short descriptor;
    public MessageDescriptor(Short descriptor) {
        if (!validateDescriptor(descriptor)) {
            throw new IllegalArgumentException("Not a valid descriptor");
        }
        this.descriptor = descriptor;
    }
    public String getName() {
        return getDescriptorName(descriptor);
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof MessageDescriptor)) return false;
        return ((MessageDescriptor)obj).descriptor.equals(descriptor);
    }
    @Override
    public int hashCode() {
        return descriptor;
    }
}
