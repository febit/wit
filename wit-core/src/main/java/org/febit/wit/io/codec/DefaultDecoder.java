// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.codec;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultDecoder implements Decoder {

    private final CharsetDecoder charsetDecoder;
    private final Buffers buffers;

    public DefaultDecoder(Charset charset) {
        this.charsetDecoder = charset.newDecoder();
        this.buffers = Buffers.of();
    }

    @Override
    public void write(byte @Nullable [] bytes, int offset, int length, Writer writer) throws IOException {
        if (bytes == null || length == 0) {
            return;
        }
        var decoder = this.charsetDecoder.reset();
        var chars = this.buffers.chars();
        var in = ByteBuffer.wrap(bytes, offset, length);
        var out = CharBuffer.wrap(chars);
        for (; ; ) {
            var cr = in.hasRemaining()
                    ? decoder.decode(in, out, true)
                    : CoderResult.UNDERFLOW;
            if (cr.isUnderflow()) {
                cr = decoder.flush(out);
            }
            if (cr.isUnderflow()) {
                break;
            }
            if (cr.isOverflow()) {
                writer.write(chars, 0, out.position());
                out.clear();
            }
        }
        if (out.position() > 0) {
            writer.write(chars, 0, out.position());
            out.clear();
        }
    }
}
