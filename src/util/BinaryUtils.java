package util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heavy on 2017/9/13.
 */

public class BinaryUtils {

    static final int SHORT_LOW_BYTE_MASK = 0xff;

    public static byte[] bytes2array(byte... bytes){
        return bytes;
    }

    public static byte int2byte(int source) {
        return (byte) (source & 0xff);
    }

    public static short int2short(int source) {
        return (short) (source & 0xffff);
    }

    public static String byte2HexString(byte b) {
        return Integer.toHexString(byte2int(b));
    }

    public static String byte2String(byte b) {
        return Integer.toString(byte2int(b));
    }

    public static int byte2int(byte source) {
        return ((int) source) & 0xff;
    }

    public static byte float2byte(float source, int factor) {
        return int2byte((int) (source * factor));
    }

    public static float byte2float(byte source, int factor) {
        return byte2int(source) / factor;
    }


    public static long bytes2long(byte[] data) {
        if (data == null) {
            return 0;
        }
        long result = 0;
        for (int i = 0; i < data.length; i++) {
            result = (result << 8) | data[i];
        }
        return result;
    }

    public static byte[] long2bytes(long value) {
        byte[] data = new byte[8];
        data[0] = (byte) (value >> 56);
        data[1] = (byte) (value >> 48);
        data[2] = (byte) (value >> 40);
        data[3] = (byte) (value >> 32);
        data[4] = (byte) (value >> 24);
        data[5] = (byte) (value >> 16);
        data[6] = (byte) (value >> 8);
        data[7] = (byte) value;
        return data;
    }

    public static String bytes2String(byte[] data) {
        if (data == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(Integer.toHexString(byte2int(b)));
        }
        return sb.toString();
    }

    public static short bytes2shortHtL(byte highByte, byte lowByte) {

        int highByteValue = byte2int(highByte) << 8;

        int lowValue = byte2int(lowByte);

        return int2short(highByteValue | lowValue);
    }

    public static byte[] short2BytesHtL(short source) {
        byte[] result = new byte[2];
        result[0] = (byte) (source >> 8);
        result[1] = (byte) (source & SHORT_LOW_BYTE_MASK);
        return result;
    }

    public static void writeBytes(byte[] source, byte[] target, int offset) {
        System.arraycopy(source, 0, target, offset, source.length);
    }
}
