// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util.charset;

import webit.script.io.charset.Encoder;

/**
 *
 * @author Zqq
 */
public class ISO_8859_1 {

    public static int decode(final byte[] src, int sp, int len, final char[] dst) {
        if (len > dst.length) {
            len = dst.length;
        }
        int dp = 0;
        while (dp < len) {
            dst[dp++] = (char) (src[sp++] & 0xff);
        }
        return dp;
    }

    public static int encode(final char[] src, int sp, int len, final byte[] dst) {
        int dp = 0;
        int sl = sp + Math.min(len, dst.length);
        char c;
        while (sp < sl) {
            c = src[sp++];
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
            dst[dp++] = Encoder.REPLACEMENT;
        }
        return dp;
    }
}
