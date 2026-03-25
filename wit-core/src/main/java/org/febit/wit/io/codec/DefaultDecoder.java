/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    public void decode(byte @Nullable [] bytes, int offset, int length, Writer writer) throws IOException {
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
