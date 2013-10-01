// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util.charset;

/*
 * Copyright (c) 2000, 2010, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
import java.nio.ByteBuffer;
import java.nio.charset.CoderResult;
import webit.script.io.charset.Decoder;
import webit.script.io.charset.Encoder;
import webit.script.util.CharUtil;

/* Legal UTF-8 Byte Sequences
 *
 * #    Code Points      Bits   Bit/Byte pattern
 * 1                     7      0xxxxxxx
 *      U+0000..U+007F          00..7F
 *
 * 2                     11     110xxxxx    10xxxxxx
 *      U+0080..U+07FF          C2..DF      80..BF
 *
 * 3                     16     1110xxxx    10xxxxxx    10xxxxxx
 *      U+0800..U+0FFF          E0          A0..BF      80..BF
 *      U+1000..U+FFFF          E1..EF      80..BF      80..BF
 *
 * 4                     21     11110xxx    10xxxxxx    10xxxxxx    10xxxxxx
 *     U+10000..U+3FFFF         F0          90..BF      80..BF      80..BF
 *     U+40000..U+FFFFF         F1..F3      80..BF      80..BF      80..BF
 *    U+100000..U10FFFF         F4          80..8F      80..BF      80..BF
 *
 */
public class UTF_8 {

    public static final int MAX_CHARS_PER_BYTE = 1;
    public static final int MAX_BYTES_PER_CHAR = 3;

    public static int decode(final byte[] sa, int sp, int len, final char[] da) {
        final int sl = sp + len;
        int dp = 0;
        int dlASCII = Math.min(len, da.length);
        ByteBuffer bb = null;  // only necessary if malformed

        // ASCII only optimized loop
        while (dp < dlASCII && sa[sp] >= 0) {
            da[dp++] = (char) sa[sp++];
        }

        while (sp < sl) {
            int b1 = sa[sp++];
            if (b1 >= 0) {
                // 1 byte, 7 bits: 0xxxxxxx
                da[dp++] = (char) b1;
            } else if ((b1 >> 5) == -2) {
                // 2 bytes, 11 bits: 110xxxxx 10xxxxxx
                if (sp < sl) {
                    int b2 = sa[sp++];
                    if (isMalformed2(b1, b2)) {
//                        if (malformedInputAction() != CodingErrorAction.REPLACE) {
//                            return -1;
//                        }
                        da[dp++] = Decoder.REPLACEMENT;
                        sp--;            // malformedN(bb, 2) always returns 1
                    } else {
                        da[dp++] = (char) (((b1 << 6) ^ b2)
                                ^ (((byte) 0xC0 << 6)
                                ^ ((byte) 0x80)));
                    }
                    continue;
                }
//                if (malformedInputAction() != CodingErrorAction.REPLACE) {
//                    return -1;
//                }
                da[dp++] = Decoder.REPLACEMENT;
                return dp;
            } else if ((b1 >> 4) == -2) {
                // 3 bytes, 16 bits: 1110xxxx 10xxxxxx 10xxxxxx
                if (sp + 1 < sl) {
                    int b2 = sa[sp++];
                    int b3 = sa[sp++];
                    if (isMalformed3(b1, b2, b3)) {
//                        if (malformedInputAction() != CodingErrorAction.REPLACE) {
//                            return -1;
//                        }
                        da[dp++] = Decoder.REPLACEMENT;
                        sp -= 3;
                        bb = getByteBuffer(bb, sa, sp);
                        sp += malformedN(bb, 3).length();
                    } else {
                        da[dp++] = (char) ((b1 << 12)
                                ^ (b2 << 6)
                                ^ (b3
                                ^ (((byte) 0xE0 << 12)
                                ^ ((byte) 0x80 << 6)
                                ^ ((byte) 0x80))));
                    }
                    continue;
                }
//                if (malformedInputAction() != CodingErrorAction.REPLACE) {
//                    return -1;
//                }
                da[dp++] = Decoder.REPLACEMENT;
                return dp;
            } else if ((b1 >> 3) == -2) {
                // 4 bytes, 21 bits: 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
                if (sp + 2 < sl) {
                    int b2 = sa[sp++];
                    int b3 = sa[sp++];
                    int b4 = sa[sp++];
                    int uc = ((b1 << 18)
                            ^ (b2 << 12)
                            ^ (b3 << 6)
                            ^ (b4
                            ^ (((byte) 0xF0 << 18)
                            ^ ((byte) 0x80 << 12)
                            ^ ((byte) 0x80 << 6)
                            ^ ((byte) 0x80))));
                    if (isMalformed4(b2, b3, b4)
                            || // shortest form check
                            !Character.isSupplementaryCodePoint(uc)) {
//                        if (malformedInputAction() != CodingErrorAction.REPLACE) {
//                            return -1;
//                        }
                        da[dp++] = Decoder.REPLACEMENT;
                        sp -= 4;
                        bb = getByteBuffer(bb, sa, sp);
                        sp += malformedN(bb, 4).length();
                    } else {
                        da[dp++] = CharUtil.highSurrogate(uc);
                        da[dp++] = CharUtil.lowSurrogate(uc);
                    }
                    continue;
                }
//                if (malformedInputAction() != CodingErrorAction.REPLACE) {
//                    return -1;
//                }
                da[dp++] = Decoder.REPLACEMENT;
                return dp;
            } else {
//                if (malformedInputAction() != CodingErrorAction.REPLACE) {
//                    return -1;
//                }
                da[dp++] = Decoder.REPLACEMENT;
                sp--;
                bb = getByteBuffer(bb, sa, sp);
                CoderResult cr = malformedN(bb, 1);
                if (!cr.isError()) {
                    // leading byte for 5 or 6-byte, but don't have enough
                    // bytes in buffer to check. Consumed rest as malformed.
                    return dp;
                }
                sp += cr.length();
            }
        }
        return dp;
    }

    private static boolean isNotContinuation(int b) {
        return (b & 0xc0) != 0x80;
    }

    //  [C2..DF] [80..BF]
    private static boolean isMalformed2(int b1, int b2) {
        return (b1 & 0x1e) == 0x0 || (b2 & 0xc0) != 0x80;
    }

    //  [E0]     [A0..BF] [80..BF]
    //  [E1..EF] [80..BF] [80..BF]
    private static boolean isMalformed3(int b1, int b2, int b3) {
        return (b1 == (byte) 0xe0 && (b2 & 0xe0) == 0x80)
                || (b2 & 0xc0) != 0x80 || (b3 & 0xc0) != 0x80;
    }

    //  [F0]     [90..BF] [80..BF] [80..BF]
    //  [F1..F3] [80..BF] [80..BF] [80..BF]
    //  [F4]     [80..8F] [80..BF] [80..BF]
    //  only check 80-be range here, the [0xf0,0x80...] and [0xf4,0x90-...]
    //  will be checked by Character.isSupplementaryCodePoint(uc)
    private static boolean isMalformed4(int b2, int b3, int b4) {
        return (b2 & 0xc0) != 0x80 || (b3 & 0xc0) != 0x80
                || (b4 & 0xc0) != 0x80;
    }

    private static CoderResult lookupN(final ByteBuffer src, int n) {
        for (int i = 1; i < n; i++) {
            if (isNotContinuation(src.get())) {
                return CoderResult.malformedForLength(i);
            }
        }
        return CoderResult.malformedForLength(n);
    }

    private static CoderResult malformedN(final ByteBuffer src, int nb) {
        switch (nb) {
            case 1:
                int b1 = src.get();
                if ((b1 >> 2) == -2) {
                    // 5 bytes 111110xx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
                    if (src.remaining() < 4) {
                        return CoderResult.UNDERFLOW;
                    }
                    return lookupN(src, 5);
                }
                if ((b1 >> 1) == -2) {
                    // 6 bytes 1111110x 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
                    if (src.remaining() < 5) {
                        return CoderResult.UNDERFLOW;
                    }
                    return lookupN(src, 6);
                }
                return CoderResult.malformedForLength(1);
            case 2:                    // always 1
                return CoderResult.malformedForLength(1);
            case 3:
                b1 = src.get();
                int b2 = src.get();    // no need to lookup b3
                return CoderResult.malformedForLength(
                        ((b1 == (byte) 0xe0 && (b2 & 0xe0) == 0x80)
                        || isNotContinuation(b2)) ? 1 : 2);
            case 4:  // we don't care the speed here
                b1 = src.get() & 0xff;
                b2 = src.get() & 0xff;
                if (b1 > 0xf4
                        || (b1 == 0xf0 && (b2 < 0x90 || b2 > 0xbf))
                        || (b1 == 0xf4 && (b2 & 0xf0) != 0x80)
                        || isNotContinuation(b2)) {
                    return CoderResult.malformedForLength(1);
                }
                if (isNotContinuation(src.get())) {
                    return CoderResult.malformedForLength(2);
                }
                return CoderResult.malformedForLength(3);
            default:
                assert false;
                return null;
        }
    }

    private static ByteBuffer getByteBuffer(ByteBuffer bb, byte[] ba, int sp) {
        if (bb == null) {
            bb = ByteBuffer.wrap(ba);
        }
        bb.position(sp);
        return bb;
    }

    public static int encode(final char[] sa, int sp, int len, final byte[] da) {
        final int sl = sp + len;
        int dp = 0;
        int dlASCII = len <= da.length ? len : da.length;   //dp + Math.min(len, da.length);

        // ASCII only optimized loop
        while (dp < dlASCII && sa[sp] < '\u0080') {
            da[dp++] = (byte) sa[sp++];
        }

        char c;
        while (sp < sl) {
            c = sa[sp++];
            if (c < 0x80) {
                // Have at most seven bits
                da[dp++] = (byte) c;
            } else if (c < 0x800) {
                // 2 bytes, 11 bits
                da[dp++] = (byte) (0xc0 | (c >> 6));
                da[dp++] = (byte) (0x80 | (c & 0x3f));
            } else if (c >= Character.MIN_HIGH_SURROGATE && c <= Character.MAX_LOW_SURROGATE) {

                if (c <= Character.MAX_HIGH_SURROGATE && sp < sl) { // if is HIGH_SURROGATE && has next char
                    final char d;
                    if ((d = sa[sp++]) >= Character.MIN_LOW_SURROGATE && d <= Character.MAX_LOW_SURROGATE) { // if is LOW_SURROGATE
                        
                        final int uc = Character.toCodePoint(c, d);

                        da[dp++] = (byte) (0xf0 | ((uc >> 18)));
                        da[dp++] = (byte) (0x80 | ((uc >> 12) & 0x3f));
                        da[dp++] = (byte) (0x80 | ((uc >> 6) & 0x3f));
                        da[dp++] = (byte) (0x80 | (uc & 0x3f));

                    } else {
                        sp--; // back the LOW_SURROGATE char
                        da[dp++] = Encoder.REPLACEMENT;
                    }
                } else {
                    da[dp++] = Encoder.REPLACEMENT;
                }

            } else {
                // 3 bytes, 16 bits
                da[dp++] = (byte) (0xe0 | ((c >> 12)));
                da[dp++] = (byte) (0x80 | ((c >> 6) & 0x3f));
                da[dp++] = (byte) (0x80 | (c & 0x3f));
            }
        }
        return dp;
    }
}
