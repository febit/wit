// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl.special;

import java.io.IOException;
import java.io.Writer;
import webit.script.io.charset.Decoder;
import webit.script.io.charset.impl.ThreadLocalCache;
import webit.script.util.charset.UTF_8;

/**
 *
 * @author Zqq
 */
public class UTF_8_Decoder implements Decoder {

    public void write(final byte[] bytes, final int off, final int len, final Writer writer) throws IOException {
        if (bytes == null || len == 0) {
            return;
        }
        
        final char[] chars = ThreadLocalCache.getChars((int) (len * UTF_8.MAX_CHARS_PER_BYTE));

        int used = UTF_8.decode(bytes, off, len, chars);
        writer.write(chars, 0, used);
    }
}
