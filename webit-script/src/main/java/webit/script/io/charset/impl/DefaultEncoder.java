// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.UnsupportedCharsetException;
import webit.script.io.Buffers;
import webit.script.io.charset.Encoder;

/**
 *
 * @author zqq90
 */
public class DefaultEncoder implements Encoder {

    private final CharsetEncoder charsetEncoder;
    private final double expansionFactor;
    private final Buffers buffers;

    public DefaultEncoder(String encoding, Buffers buffers) {
        this.expansionFactor = (double) (this.charsetEncoder = newEncoder(encoding)).maxBytesPerChar();
        this.buffers = buffers;
    }

    @Override
    public void write(final String string, final int offset, final int length, final OutputStream out) throws IOException {
        char[] chars;
        string.getChars(offset, offset + length,
                chars = this.buffers.getChars(length),
                0);
        write(chars, 0, length, out);
    }

    @Override
    public void write(final char[] chars, final int offset, final int length, final OutputStream out) throws IOException {
        if (chars != null && length != 0) {
            final byte[] bytes;
            final CharsetEncoder encoder;
            final ByteBuffer bb;
            (encoder = this.charsetEncoder).reset().encode(
                    CharBuffer.wrap(chars, offset, length),
                    bb = ByteBuffer.wrap(bytes = this.buffers.getBytes((int) (length * this.expansionFactor))),
                    true);
            encoder.flush(bb);
            out.write(bytes, 0, bb.position());
        }
    }

    private static CharsetEncoder newEncoder(String csn) throws UnsupportedCharsetException {
        return Charset.forName(csn).newEncoder();
    }
}
