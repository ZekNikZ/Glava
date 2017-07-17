package zeknikz;

import java.io.UnsupportedEncodingException;

class GlavaEncoding {

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
        for (int i = 0; i < 127; i++) {
            buffer[i] = (char) i;
        }
        int[] greek = {
                182, 184, 185, 186, 188, 190, 191,
                192, 195, 196, 200, 203, 206,
                208, 211, 214, 216, 217, 218, 219, 220, 221, 222, 223,
                224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238,
                240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254
        };
        for(int chr : greek) {
            assert charStr != null;
            buffer[chr] = charStr.charAt(chr);
        }
        String c80_to_99 = "⑴⑵⑶⑷⑸⑹⑺⑻⑼⑽ⒷⒸⒹⒻⒾⓈ①②③④⑤⑥⑦⑧⑨⑩";
        for (int i = 0; i < 26; i++) {
            buffer[i + 0x80] = c80_to_99.charAt(i);
        }
        encoding = buffer;
    }

    static String decodeToUTF8(byte[] bytes) {
        StringBuilder s = new StringBuilder();
        for (byte b : bytes) {
            s.append(encoding[b&0xFF]);
        }
        return s.toString();
    }
}
