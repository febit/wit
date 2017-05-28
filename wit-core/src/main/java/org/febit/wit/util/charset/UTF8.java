// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util.charset;

public class UTF8 {

    private UTF8() {
    }

    public static final int MAX_BYTES_PER_CHAR = 3;

    public static int decode(final byte[] sa, int index, int len, final char[] da) {
        final int end = index + len;
        int count = 0;

        while (index < end) {
            int b1 = sa[index++];
            if (b1 >= 0) {
                // 1 byte, 7 bits: 0xxxxxxx
                da[count++] = (char) b1;
                continue;
            } else if ((b1 >> 5) == -2 && index < end) {
                // 2 bytes, 11 bits: 110xxxxx 10xxxxxx
                int b2 = sa[index++];
                if ((b1 & 0x1e) != 0x0 && (b2 & 0xc0) == 0x80) {
                    da[count++] = (char) (((b1 << 6) ^ b2)
                            ^ (((byte) 0xC0 << 6)
                            ^ ((byte) 0x80)));
                    continue;
                }
                index--;
            } else if ((b1 >> 4) == -2 && index + 1 < end) {
                // 3 bytes, 16 bits: 1110xxxx 10xxxxxx 10xxxxxx
                int b2 = sa[index++];
                int b3 = sa[index++];
                if ((b1 != (byte) 0xe0 || (b2 & 0xe0) != 0x80)
                        && (b2 & 0xc0) == 0x80 && (b3 & 0xc0) == 0x80) {
                    da[count++] = (char) ((b1 << 12)
                            ^ (b2 << 6)
                            ^ (b3
                            ^ (((byte) 0xE0 << 12)
                            ^ ((byte) 0x80 << 6)
                            ^ ((byte) 0x80))));
                    continue;
                }
                index -= 2;
            } else if ((b1 >> 3) == -2 && index + 2 < end) {
                // 4 bytes, 21 bits: 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
                int uc = ((b1 << 18)
                        ^ (sa[index++] << 12)
                        ^ (sa[index++] << 6)
                        ^ (sa[index++]
                        ^ (((byte) 0xF0 << 18)
                        ^ ((byte) 0x80 << 12)
                        ^ ((byte) 0x80 << 6)
                        ^ ((byte) 0x80))));
                if (Character.isSupplementaryCodePoint(uc)) {
                    da[count++] = (char) ((uc >>> 10) + (Character.MIN_HIGH_SURROGATE - (Character.MIN_SUPPLEMENTARY_CODE_POINT >>> 10)));
                    da[count++] = (char) ((uc & 0x3ff) + Character.MIN_LOW_SURROGATE);
                    continue;
                }
                index -= 3;
            }
            da[count++] = '\uFFFD';
        }
        return count;
    }

    public static int encode(final byte[] da, final char[] sa, int from, final int to) {
        int dp = 0;
        char c;
        char d;
        int uc;
        while (from < to) {
            if ((c = sa[from++]) < 0x80) {
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
                da[dp++] = (byte) (0xe0 | ((c >> 12)));
                da[dp++] = (byte) (0x80 | ((c >> 6) & 0x3f));
                da[dp++] = (byte) (0x80 | (c & 0x3f));
                continue;
            }
            if (c <= Character.MAX_HIGH_SURROGATE && from < to) {
                // if is HIGH_SURROGATE && has next char
                if ((d = sa[from++]) >>> 10 == 0x37) {
                    // if is LOW_SURROGATE: Character.MIN_LOW_SURROGATE <= d <= Character.MAX_LOW_SURROGATE
                    uc = Character.toCodePoint(c, d);
                    da[dp++] = (byte) (0xf0 | ((uc >> 18)));
                    da[dp++] = (byte) (0x80 | ((uc >> 12) & 0x3f));
                    da[dp++] = (byte) (0x80 | ((uc >> 6) & 0x3f));
                    da[dp++] = (byte) (0x80 | (uc & 0x3f));
                    continue;
                }
                --from; // back the LOW_SURROGATE char
            }
            da[dp++] = '?';
        }
        return dp;
    }
}
