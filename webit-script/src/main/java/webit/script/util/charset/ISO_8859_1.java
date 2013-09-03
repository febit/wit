// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util.charset;

/**
 *
 * @author Zqq
 */
public class ISO_8859_1 {

    //public static final double MAX_CHARS_PER_BYTE = 1.0;
    //public static final double MAX_BYTES_PER_CHAR = 1.0;
    public static int decode(byte[] src, int sp, int len, char[] dst) {
        if (len > dst.length) {
            len = dst.length;
        }
        int dp = 0;
        while (dp < len) {
            dst[dp++] = (char) (src[sp++] & 0xff);
        }
        return dp;
    }
    private final static byte repl = (byte) '?';

    public static int encode(char[] src, int sp, int len, byte[] dst) {
        int dp = 0;
        int sl = sp + Math.min(len, dst.length);
        while (sp < sl) {
            char c = src[sp++];
            if (c <= '\u00FF') {
                dst[dp++] = (byte) c;
                continue;
            }
            if (Character.isHighSurrogate(c) && sp < sl
                    && Character.isLowSurrogate(src[sp])) {
                if (len > dst.length) {
                    sl++;
                    len--;
                }
                sp++;
            }
            dst[dp++] = repl;
        }
        return dp;
    }
}
