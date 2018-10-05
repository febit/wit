// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.charset.impl;

import org.febit.wit.io.Buffers;
import org.febit.wit.io.charset.Decoder;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * @author zqq90
 */
public class DefaultDecoder implements Decoder {

    private final CharsetDecoder charsetDecoder;
    private final double expansionFactor;
    private final Buffers buffers;

    public DefaultDecoder(String encoding, Buffers buffers) {
        this.charsetDecoder = newDecoder(encoding);
        this.expansionFactor = (double) this.charsetDecoder.maxCharsPerByte();
        this.buffers = buffers;
    }

    @Override
    public void write(final byte[] bytes, final int offset, final int length, final Writer writer) throws IOException {
        if (bytes == null || length == 0) {
            return;
        }
        final CharsetDecoder decoder = this.charsetDecoder.reset();
        final char[] chars = this.buffers.getChars((int) (length * this.expansionFactor));
        final CharBuffer cb = CharBuffer.wrap(chars);
        decoder.decode(ByteBuffer.wrap(bytes, offset, length), cb, true);
        decoder.flush(cb);
        writer.write(chars, 0, cb.position());
    }

    private static CharsetDecoder newDecoder(String csn) {
        return Charset.forName(csn).newDecoder();
    }
}
