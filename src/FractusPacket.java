
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import org.apache.log4j.Logger;

import org.bouncycastle.util.encoders.Base64;

public class FractusPacket {
    private byte[] contents;
    private static Logger log;

    public FractusPacket(byte[] contents) {
        this.contents = contents;
    }

    static {
        log = Logger.getLogger(FractusPacket.class.getName());
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(Integer.SIZE + this.contents.length);
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.contents.length);
        baos.write(contents);
        return baos.toByteArray();
    }

    public byte[] getContents() {
        return contents;
    }

    public static FractusPacket read(InputStream input) throws IOException {
        DataInputStream dis = new DataInputStream(input);
        Integer packetLength = dis.readInt();
        if (packetLength > 0xffff) {
            log.warn(String.format("Received unreasonable size for packet (%1$X).  Abandoning.", packetLength));
            return null;
        }
        byte[] contents = new byte[packetLength];
        input.read(contents);
        return new FractusPacket(contents);
    }
}
