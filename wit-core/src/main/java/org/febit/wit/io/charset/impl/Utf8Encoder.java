// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.charset.impl;

import org.febit.wit.io.Buffers;
import org.febit.wit.io.charset.Encoder;
import org.febit.wit.util.charset.Utf8;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author zqq90
 */
public final class Utf8Encoder implements Encoder {

    private final Buffers buffers;

    public Utf8Encoder(Buffers buffers) {
        this.buffers = buffers;
    }

    @Override
    public void write(final char[] chars, final int off, final int len, final OutputStream out) throws IOException {
        if (chars == null || len == 0) {
            return;
        }
        final byte[] bytes = this.buffers.getBytes(len * Utf8.MAX_BYTES_PER_CHAR);
        int used = Utf8.encode(bytes, chars, off, off + len);
        out.write(bytes, 0, used);
    }

    @Override
    public void write(final String string, final int off, final int len, final OutputStream out) throws IOException {
        if (string == null) {
            return;
        }
        final char[] chars = this.buffers.getChars(len);
        string.getChars(off, off + len, chars, 0);
        final byte[] bytes = this.buffers.getBytes(len * Utf8.MAX_BYTES_PER_CHAR);
        int used = Utf8.encode(bytes, chars, off, off + len);
        out.write(bytes, 0, used);
    }
}
