// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.io.charset.impl;

import java.io.IOException;
import java.io.Writer;
import org.febit.wit.io.Buffers;
import org.febit.wit.io.charset.Decoder;
import org.febit.wit.util.charset.UTF8;

/**
 *
 * @author zqq90
 */
public final class UTF8Decoder implements Decoder {

    private final Buffers buffers;

    public UTF8Decoder(Buffers buffers) {
        this.buffers = buffers;
    }

    @Override
    public void write(final byte[] bytes, final int off, final int len, final Writer writer) throws IOException {
        if (bytes == null || len == 0) {
            return;
        }
        final char[] chars;
        int used = UTF8.decode(bytes, off, len,
                chars = this.buffers.getChars(len));
        writer.write(chars, 0, used);
    }
}
