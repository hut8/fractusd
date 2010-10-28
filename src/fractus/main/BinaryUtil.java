/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fractus.main;

/**
 *
 * @author bowenl2
 */
public class BinaryUtil {
    public static String encodeData(byte[] data) {
        return encodeData(data, 32);
    }

    public static String encodeData(byte[] data, int columns) {
        StringBuilder sb = new StringBuilder();
        sb.append('\n');
        int i = 0;
        for (byte b : data) {
            sb.append(String.format("%02X", b & 0xff));
            if (i != data.length - 1) {
                sb.append(' ');
            }
            if (++i % columns == 0) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

}
