// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.charset.impl;

import lombok.val;
import org.febit.wit.io.Buffers;
import org.febit.wit.io.charset.Encoder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * @author zqq90
 */
public class DefaultEncoder implements Encoder {

    private final CharsetEncoder charsetEncoder;
    private final double expansionFactor;
    private final Buffers buffers;

    public DefaultEncoder(String encoding, Buffers buffers) {
        this.charsetEncoder = newEncoder(encoding);
        this.expansionFactor = (double) this.charsetEncoder.maxBytesPerChar();
        this.buffers = buffers;
    }

    @Override
    public void write(final String string, final int offset, final int length,
                      final OutputStream out) throws IOException {
        char[] chars;
        string.getChars(offset, offset + length,
                chars = this.buffers.getChars(length),
                0);
        write(chars, 0, length, out);
    }

    @Override
    public void write(final char[] chars, final int offset, final int length,
                      final OutputStream out) throws IOException {
        if (chars == null || length == 0) {
            return;
        }
        val encoder = this.charsetEncoder.reset();
        val bytes = this.buffers.getBytes((int) (length * this.expansionFactor));
        val bb = ByteBuffer.wrap(bytes);
        encoder.encode(CharBuffer.wrap(chars, offset, length), bb, true);
        encoder.flush(bb);
        out.write(bytes, 0, bb.position());
    }

    private static CharsetEncoder newEncoder(String csn) {
        return Charset.forName(csn).newEncoder();
    }
}
