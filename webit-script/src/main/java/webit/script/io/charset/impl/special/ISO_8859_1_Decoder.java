// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl.special;

import java.io.IOException;
import java.io.Writer;
import webit.script.io.charset.Decoder;
import webit.script.io.charset.impl.ThreadLocalCache;
import webit.script.util.charset.ISO_8859_1;

/**
 *
 * @author Zqq
 */
public final class ISO_8859_1_Decoder implements Decoder {
    
    public void write(final byte[] bytes, final int off, final int len, final Writer writer) throws IOException {
        if (bytes == null || len == 0) {
            return;
        }
        //int new_len = len;
        char[] chars = ThreadLocalCache.getChars(len);
        
        int used = ISO_8859_1.decode(bytes, off, len, chars);
        writer.write(chars, 0, used);
    }
}
