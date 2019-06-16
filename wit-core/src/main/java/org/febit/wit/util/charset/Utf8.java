// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util.charset;

public class Utf8 {

    public static final int MAX_BYTES_PER_CHAR = 3;

    private Utf8() {
    }

    public static int decode(final byte[] sa, final int from, final int len, final char[] da) {
        final int end = from + len;
        int count = 0;
        int pos = from;
        while (pos < end) {
            int b1 = sa[pos++];
            if (b1 >= 0) {
                // 1 byte, 7 bits: 0xxxxxxx
                da[count++] = (char) b1;
                continue;
            } else if ((b1 >> 5) == -2 && pos < end) {
                // 2 bytes, 11 bits: 110xxxxx 10xxxxxx
                int b2 = sa[pos++];
                if ((b1 & 0x1e) != 0x0 && (b2 & 0xc0) == 0x80) {
                    da[count++] = (char) (((b1 << 6) ^ b2)
                            ^ ((0xFFFFFFC0 << 6) ^ 0xFFFFFF80));
                    continue;
                }
                pos--;
            } else if ((b1 >> 4) == -2 && pos + 1 < end) {
                // 3 bytes, 16 bits: 1110xxxx 10xxxxxx 10xxxxxx
                int b2 = sa[pos++];
                int b3 = sa[pos++];
                if ((b1 != (byte) 0xe0 || (b2 & 0xe0) != 0x80)
                        && (b2 & 0xc0) == 0x80 && (b3 & 0xc0) == 0x80) {
                    da[count++] = (char) ((b1 << 12)
                            ^ (b2 << 6)
                            ^ (b3
                            ^ ((0xFFFFFFE0 << 12) ^ (0xFFFFFF80 << 6) ^ 0xFFFFFF80)));
                    continue;
                }
                pos -= 2;
            } else if ((b1 >> 3) == -2 && pos + 2 < end) {
                // 4 bytes, 21 bits: 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
                int uc = (b1 << 18)
                        ^ (sa[pos++] << 12)
                        ^ (sa[pos++] << 6)
                        ^ (sa[pos++]
                        ^ ((0xFFFFFFF0 << 18) ^ (0xFFFFFF80 << 12) ^ (0xFFFFFF80 << 6) ^ 0xFFFFFF80));
                if (Character.isSupplementaryCodePoint(uc)) {
                    da[count++] = (char) ((uc >>> 10) + (Character.MIN_HIGH_SURROGATE
                            - (Character.MIN_SUPPLEMENTARY_CODE_POINT >>> 10)));
                    da[count++] = (char) ((uc & 0x3ff) + Character.MIN_LOW_SURROGATE);
                    continue;
                }
                pos -= 3;
            }
            // unsupported bytes
            da[count++] = '\uFFFD';
        }
        return count;
    }

    public static int encode(final byte[] da, final char[] sa, final int from, final int to) {
        int dp = 0;
        char c;
        char d;
        int uc;
        int pos = from;
        while (pos < to) {
            c = sa[pos++];
            if (c < 0x80) {
                // Have at most seven bits
                da[dp++] = (byte) c;
                continue;
            }
            if (c < 0x800) {
                // 2 bytes, 11 bits
                da[dp++] = (byte) (0xc0 | (c >> 6));
                da[dp++] = (byte) (0x80 | (c & 0x3f));
                continue;
            }
            if (c >>> 11 != 0x1B) {
                //if not SURROGATE: c < Character.MIN_HIGH_SURROGATE || c > Character.MAX_LOW_SURROGATE
                // 3 bytes, 16 bits
                da[dp++] = (byte) (0xe0 | (c >> 12));
                da[dp++] = (byte) (0x80 | ((c >> 6) & 0x3f));
                da[dp++] = (byte) (0x80 | (c & 0x3f));
                continue;
            }
            if (c <= Character.MAX_HIGH_SURROGATE && pos < to) {
                // if is HIGH_SURROGATE && has next char
                d = sa[pos++];
                if (d >>> 10 == 0x37) {
                    // if is LOW_SURROGATE: Character.MIN_LOW_SURROGATE <= d <= Character.MAX_LOW_SURROGATE
                    uc = Character.toCodePoint(c, d);
                    da[dp++] = (byte) (0xf0 | (uc >> 18));
                    da[dp++] = (byte) (0x80 | ((uc >> 12) & 0x3f));
                    da[dp++] = (byte) (0x80 | ((uc >> 6) & 0x3f));
                    da[dp++] = (byte) (0x80 | (uc & 0x3f));
                    continue;
                }
                --pos; // back the LOW_SURROGATE char
            }
            // unsupported char
            da[dp++] = '?';
        }
        return dp;
    }
}
