// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.UnsupportedCharsetException;
import webit.script.io.charset.Decoder;

/**
 *
 * @author Zqq
 */
public class DefaultDecoder implements Decoder {

    private final CharsetDecoder charsetDecoder;
    private final double expansionFactor;

    public DefaultDecoder(String encoding) {
        CharsetDecoder cd = newDecoder(encoding);
        this.expansionFactor = (double) cd.maxCharsPerByte();
        this.charsetDecoder = cd;
    }

    public void write(byte[] bytes, int offset, int length, Writer writer) throws IOException {
        if (bytes == null || length == 0) {
            return;
        }
        int new_len = (int) (length * expansionFactor);
        char[] chars = ThreadLocalCache.getChars(new_len); //new byte[new_len];

        charsetDecoder.reset();
        ByteBuffer bb = ByteBuffer.wrap(bytes, offset, length);
        CharBuffer cb = CharBuffer.wrap(chars);
        try {
            CoderResult cr = charsetDecoder.decode(bb, cb, true);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
            cr = charsetDecoder.flush(cb);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
        } catch (CharacterCodingException x) {
            // Substitution is always enabled,
            // so this shouldn't happen
            throw new Error(x);
        }

        writer.write(chars, 0, bb.position());
    }

    private static CharsetDecoder newDecoder(String csn) throws UnsupportedCharsetException {
        return Charset.forName(csn).newDecoder();
    }
}
