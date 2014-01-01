// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl.special;

import java.io.IOException;
import java.io.OutputStream;
import webit.script.io.charset.Encoder;
import webit.script.util.BufferPeers;
import webit.script.util.charset.UTF_8;

/**
 *
 * @author Zqq
 */
public final class UTF_8_Encoder implements Encoder {

    private final BufferPeers bufferPeers;

    public UTF_8_Encoder(BufferPeers bufferPeers) {
        this.bufferPeers = bufferPeers;
    }

    public void write(final char[] chars, final int off, final int len, final OutputStream out) throws IOException {
        if (chars != null && len != 0) {
            final byte[] bytes;
            int used = UTF_8.encode(bytes = this.bufferPeers.getBytes(len * UTF_8.MAX_BYTES_PER_CHAR),
                    chars, off, off + len);
            out.write(bytes, 0, used);
        }
    }

    public void write(final String string, final int off, final int len, final OutputStream out) throws IOException {
        if (string != null) {
            final char[] chars;
            final byte[] bytes;
            string.getChars(off, off + len,
                    chars = this.bufferPeers.getChars(len),
                    0);
            int used = UTF_8.encode(bytes = this.bufferPeers.getBytes(len * UTF_8.MAX_BYTES_PER_CHAR),
                    chars, off, off + len);
            out.write(bytes, 0, used);
        }
    }
}
