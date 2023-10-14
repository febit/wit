// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.charset.impl;

import org.febit.wit.io.Buffers;
import org.febit.wit.io.charset.Decoder;
import org.febit.wit.util.charset.Utf8;

import java.io.IOException;
import java.io.Writer;

/**
 * @author zqq90
 */
public final class Utf8Decoder implements Decoder {

    private final Buffers buffers;

    public Utf8Decoder(Buffers buffers) {
        this.buffers = buffers;
    }

    @Override
    public void write(final byte[] bytes, final int off, final int len, final Writer writer) throws IOException {
        if (bytes == null || len == 0) {
            return;
        }
        var chars = this.buffers.getChars(len);
        int used = Utf8.decode(bytes, off, len, chars);
        writer.write(chars, 0, used);
    }
}
