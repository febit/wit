// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.UnsupportedCharsetException;
import webit.script.io.charset.Encoder;
//import sun.nio.cs.ArrayEncoder;

/**
 *
 * @author Zqq
 */
public class DefaultEncoder implements Encoder {

    private final CharsetEncoder charsetEncoder;
    private final double expansionFactor;

    public DefaultEncoder(String encoding) {
        CharsetEncoder ce = newEncoder(encoding);
        this.expansionFactor = (double) ce.maxBytesPerChar();
        this.charsetEncoder = ce;
//        if (ce instanceof ArrayEncoder) {
//            this.arrayEncoder = (ArrayEncoder) ce;
//        } else {
//            this.arrayEncoder = null;
//        }
    }

    public void write(final String string, final int offset, final int length, final OutputStream out) throws IOException {
        char[] chars = length < ThreadLocalCache.CACH_MIN_LEN ? new char[length] : ThreadLocalCache.getChars(length);
        string.getChars(offset, offset + length, chars, 0);
        write(chars, 0, length, out);
    }

    public void write(final char[] chars, final int offset, final int length, final OutputStream out) throws IOException {
        if (chars == null || length == 0) {
            return;
        }
        final int bytes_len = (int) (length * expansionFactor);
        final byte[] bytes = bytes_len < ThreadLocalCache.CACH_MIN_LEN ? new byte[bytes_len] : ThreadLocalCache.getBytes(bytes_len); //new byte[new_len];
//        if (arrayEncoder != null) {
//            int blen = arrayEncoder.encode(chars, off, len, bytes);
//            out.write(bytes, 0, blen);
//        } else {
        charsetEncoder.reset();
        final ByteBuffer bb = ByteBuffer.wrap(bytes);
        final CharBuffer cb = CharBuffer.wrap(chars, offset, length);
        try {
            CoderResult cr = charsetEncoder.encode(cb, bb, true);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
            cr = charsetEncoder.flush(bb);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
        } catch (CharacterCodingException x) {
            // Substitution is always enabled,
            // so this shouldn't happen
            throw new Error(x);
        }
        out.write(bytes, 0, bb.position());
        //}
    }

    private static CharsetEncoder newEncoder(String csn) throws UnsupportedCharsetException {
        return Charset.forName(csn).newEncoder();
    }
}
