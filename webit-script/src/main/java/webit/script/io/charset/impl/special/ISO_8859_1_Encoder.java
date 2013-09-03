// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl.special;

import java.io.IOException;
import java.io.OutputStream;
import webit.script.io.charset.Encoder;
import webit.script.io.charset.impl.ThreadLocalCache;
import webit.script.util.charset.ISO_8859_1;

/**
 *
 * @author Zqq
 */
public class ISO_8859_1_Encoder implements Encoder {


    public final void write(char[] chars, int off, int len, OutputStream out) throws IOException {
        if (chars == null || len == 0) {
            return;
        }
        //int new_len = len;
        byte[] bytes = ThreadLocalCache.getBytes(len);

        int used = ISO_8859_1.encode(chars, off, len, bytes);

        out.write(bytes, 0, used);
    }

    public void write(String string, int off, int len, OutputStream out) throws IOException {
        if (string == null || len == 0) {
            return;
        }
        char[] chars = ThreadLocalCache.getChars(len);

        string.getChars(off, off + len, chars, 0);

        write(chars, 0, len, out);
    }
}
