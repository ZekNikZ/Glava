package zeknikz;

import java.io.UnsupportedEncodingException;

public class GlavaEncoding {

    private static final char[] encoding;

    static {
        char[] buffer = new char[256];
        byte[] chrStrBuffer = new byte[256];
        String charStr = null;
        for (int i = 0; i < 256; i++) chrStrBuffer[i] = (byte) i;
        try {
            charStr = new String(chrStrBuffer, "ISO-8859-7");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 256; i++) {
            buffer[i] = charStr.charAt(i);
        }
        encoding = buffer;
        // OPEN SPACE ~ 127 -> 159
    }

    public static String encodeUTF8(String str) {
        return null;
    }

    public static String decodeToUTF8(String str) {
        str.getBytes();
        return null;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        for (char c : encoding) {
            System.out.println(c);
        }
    }
}
