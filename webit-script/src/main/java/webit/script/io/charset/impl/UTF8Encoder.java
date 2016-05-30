// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl;

import java.io.IOException;
import java.io.OutputStream;
import webit.script.io.Buffers;
import webit.script.io.charset.Encoder;
import webit.script.util.charset.UTF8;

/**
 *
 * @author Zqq
 */
public final class UTF8Encoder implements Encoder {

    private final Buffers buffers;

    public UTF8Encoder(Buffers buffers) {
        this.buffers = buffers;
    }

    @Override
    public void write(final char[] chars, final int off, final int len, final OutputStream out) throws IOException {
        if (chars != null && len != 0) {
            final byte[] bytes;
            int used = UTF8.encode(bytes = this.buffers.getBytes(len * UTF8.MAX_BYTES_PER_CHAR),
                    chars, off, off + len);
            out.write(bytes, 0, used);
        }
    }

    @Override
    public void write(final String string, final int off, final int len, final OutputStream out) throws IOException {
        if (string != null) {
            final char[] chars;
            final byte[] bytes;
            string.getChars(off, off + len,
                    chars = this.buffers.getChars(len),
                    0);
            int used = UTF8.encode(bytes = this.buffers.getBytes(len * UTF8.MAX_BYTES_PER_CHAR),
                    chars, off, off + len);
            out.write(bytes, 0, used);
        }
    }
}
