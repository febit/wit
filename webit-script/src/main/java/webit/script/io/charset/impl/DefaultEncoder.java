// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.UnsupportedCharsetException;
import webit.script.io.charset.Encoder;
import webit.script.util.BufferPeers;
//import sun.nio.cs.ArrayEncoder;

/**
 *
 * @author Zqq
 */
public class DefaultEncoder implements Encoder {

    private final CharsetEncoder charsetEncoder;
    private final double expansionFactor;
    private final BufferPeers bufferPeers;

    public DefaultEncoder(String encoding, BufferPeers bufferPeers) {
        this.expansionFactor = (double) (this.charsetEncoder = newEncoder(encoding)).maxBytesPerChar();
        this.bufferPeers = bufferPeers;
    }

    public void write(final String string, final int offset, final int length, final OutputStream out) throws IOException {
        char[] chars;
        string.getChars(offset, offset + length,
                chars = this.bufferPeers.getChars(length),
                0);
        write(chars, 0, length, out);
    }

    public void write(final char[] chars, final int offset, final int length, final OutputStream out) throws IOException {
        if (chars != null && length != 0) {
            final byte[] bytes; //new byte[new_len];
            final CharsetEncoder encoder;
            final ByteBuffer bb;
            (encoder = this.charsetEncoder).reset().encode(
                    CharBuffer.wrap(chars, offset, length),
                    bb = ByteBuffer.wrap(bytes = this.bufferPeers.getBytes((int) (length * this.expansionFactor))),
                    true);
            encoder.flush(bb);
            out.write(bytes, 0, bb.position());
        }
    }

    private static CharsetEncoder newEncoder(String csn) throws UnsupportedCharsetException {
        return Charset.forName(csn).newEncoder();
    }
}
