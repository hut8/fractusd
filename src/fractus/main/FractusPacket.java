package fractus.main;




import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;


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
        if (packetLength > 0xffff || packetLength < 0) {
            log.warn(String.format("Received unreasonable size for packet (0X%1$X).  Abandoning.", packetLength));
            return null;
        }
        byte[] contents = new byte[packetLength];
        input.read(contents);
        return new FractusPacket(contents);
    }
}
