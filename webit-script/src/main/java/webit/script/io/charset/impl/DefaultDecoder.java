// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.UnsupportedCharsetException;
import webit.script.io.charset.Decoder;
import webit.script.util.BufferPeers;

/**
 *
 * @author Zqq
 */
public class DefaultDecoder implements Decoder {

    private final CharsetDecoder charsetDecoder;
    private final double expansionFactor;
    private final BufferPeers bufferPeers;

    public DefaultDecoder(String encoding, BufferPeers bufferPeers) {
        this.expansionFactor = (double) (this.charsetDecoder = newDecoder(encoding)).maxCharsPerByte();
        this.bufferPeers = bufferPeers;
    }

    public void write(final byte[] bytes, final int offset, final int length, final Writer writer) throws IOException {
        if (bytes != null && length != 0) {
            final char[] chars; //new byte[new_len];
            final CharsetDecoder decoder;
            final CharBuffer cb;
            (decoder = this.charsetDecoder).reset().decode(
                    ByteBuffer.wrap(bytes, offset, length),
                    cb = CharBuffer.wrap(chars = this.bufferPeers.getChars((int) (length * this.expansionFactor))),
                    true);
            decoder.flush(cb);
            writer.write(chars, 0, cb.position());
        }
    }

    private static CharsetDecoder newDecoder(String csn) throws UnsupportedCharsetException {
        return Charset.forName(csn).newDecoder();
    }
}
