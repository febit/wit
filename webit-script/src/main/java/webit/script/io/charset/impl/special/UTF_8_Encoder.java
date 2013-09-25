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
        if (chars != null && len != 0) {
            final int bytes_len = len * UTF_8.MAX_BYTES_PER_CHAR;
            final byte[] bytes = bytes_len < ThreadLocalCache.CACH_MIN_LEN ? new byte[bytes_len] : ThreadLocalCache.getBytes(bytes_len);
            int used = UTF_8.encode(chars, off, len, bytes);
            out.write(bytes, 0, used);
        }
    }

    public void write(final String string, final int off, final int len, final OutputStream out) throws IOException {
        if (string != null) {
            final char[] chars = len < ThreadLocalCache.CACH_MIN_LEN ? new char[len] : ThreadLocalCache.getChars(len);
            string.getChars(off, off + len, chars, 0);
            final int bytes_len = len * UTF_8.MAX_BYTES_PER_CHAR;
            final byte[] bytes = bytes_len < ThreadLocalCache.CACH_MIN_LEN ? new byte[bytes_len] : ThreadLocalCache.getBytes(bytes_len);
            int used = UTF_8.encode(chars, off, len, bytes);
            out.write(bytes, 0, used);
        }
    }
}
