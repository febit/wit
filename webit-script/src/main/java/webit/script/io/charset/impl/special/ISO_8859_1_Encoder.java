// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl.special;

import java.io.IOException;
import java.io.OutputStream;
import webit.script.io.charset.Encoder;
import webit.script.util.BufferPeers;
import webit.script.util.charset.ISO_8859_1;

/**
 *
 * @author Zqq
 */
public final class ISO_8859_1_Encoder implements Encoder {

    private final BufferPeers bufferPeers;

    public ISO_8859_1_Encoder(BufferPeers bufferPeers) {
        this.bufferPeers = bufferPeers;
    }

    public void write(final char[] chars, final int off, final int len, final OutputStream out) throws IOException {
        if (chars == null || len == 0) {
            return;
        }
        //int new_len = len;
        final byte[] bytes;

        int used = ISO_8859_1.encode(chars, off, len,
                bytes = this.bufferPeers.getBytes(len));

        out.write(bytes, 0, used);
    }

    public void write(final String string, final int off, final int len, final OutputStream out) throws IOException {
        if (string == null || len == 0) {
            return;
        }
        final char[] chars;

        string.getChars(off, off + len,
                chars = this.bufferPeers.getChars(len),
                0);

        write(chars, 0, len, out);
    }
}
