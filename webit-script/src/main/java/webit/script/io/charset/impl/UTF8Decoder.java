// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl;

import java.io.IOException;
import java.io.Writer;
import webit.script.io.Buffers;
import webit.script.io.charset.Decoder;
import webit.script.util.charset.UTF8;

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
