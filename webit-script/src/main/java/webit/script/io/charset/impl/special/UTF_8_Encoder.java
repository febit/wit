// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl.special;

import java.io.IOException;
import java.io.OutputStream;
import webit.script.io.charset.Encoder;
import webit.script.io.charset.impl.ThreadLocalCache;
import webit.script.util.charset.UTF_8;

/**
 *
 * @author Zqq
 */
public final class UTF_8_Encoder implements Encoder {

    public void write(final char[] chars, final int off, final int len, final OutputStream out) throws IOException {
        if (chars == null || len == 0) {
            return;
        }
        final byte[] bytes = ThreadLocalCache.getBytes((int) (len * UTF_8.MAX_BYTES_PER_CHAR));

        int used = UTF_8.encode(chars, off, len, bytes);

        out.write(bytes, 0, used);
    }

    public void write(final String string, final int off, final int len, final OutputStream out) throws IOException {
        if (string == null || len == 0) {
            return;
        }
        final char[] chars = ThreadLocalCache.getChars(len);

        string.getChars(off, off + len, chars, 0);

        write(chars, 0, len, out);
    }
}
