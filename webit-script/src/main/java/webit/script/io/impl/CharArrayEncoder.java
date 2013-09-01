// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.UnsupportedCharsetException;
//import sun.nio.cs.ArrayEncoder;

/**
 *
 * @author Zqq
 */
class CharArrayEncoder {

    private final CharsetEncoder charsetEncoder;
    //private final ArrayEncoder arrayEncoder;
    private final OutputStream outputStream;
    private final double expansionFactor;

    public CharArrayEncoder(String encoding, OutputStream outputStream) {
        CharsetEncoder ce = newEncoder(encoding);
        this.expansionFactor = (double) ce.maxBytesPerChar();
        this.outputStream = outputStream;
        this.charsetEncoder = ce;
//        if (ce instanceof ArrayEncoder) {
//            this.arrayEncoder = (ArrayEncoder) ce;
//        } else {
//            this.arrayEncoder = null;
//        }
    }

    public void write(char[] chars, int off, int len) throws IOException {
        if (chars == null || len == 0) {
            return;
        }
        int new_len = (int) (len * expansionFactor);
        byte[] bytes = new byte[new_len];
//        if (arrayEncoder != null) {
//            int blen = arrayEncoder.encode(chars, off, len, bytes);
//            outputStream.write(bytes, 0, blen);
//        } else {
            charsetEncoder.reset();
            ByteBuffer bb = ByteBuffer.wrap(bytes);
            CharBuffer cb = CharBuffer.wrap(chars, off, len);
            try {
                CoderResult cr = charsetEncoder.encode(cb, bb, true);
                if (!cr.isUnderflow()) {
                    cr.throwException();
                }
                cr = charsetEncoder.flush(bb);
                if (!cr.isUnderflow()) {
                    cr.throwException();
                }
            } catch (CharacterCodingException x) {
                // Substitution is always enabled,
                // so this shouldn't happen
                throw new Error(x);
            }
            outputStream.write(bytes, 0, bb.position());
        //}
    }

    private static CharsetEncoder newEncoder(String csn) throws UnsupportedCharsetException {
        return Charset.forName(csn).newEncoder();
    }
}
