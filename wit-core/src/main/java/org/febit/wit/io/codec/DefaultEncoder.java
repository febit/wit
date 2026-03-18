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
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultEncoder implements Encoder {

    private final CharsetEncoder charsetEncoder;
    private final Buffers buffers;

    public DefaultEncoder(Charset charset) {
        this.charsetEncoder = charset.newEncoder();
        this.buffers = Buffers.of();
    }

    @Override
    public void write(String string, int offset, int length, OutputStream out) throws IOException {
        char[] chars = this.buffers.chars();
        int copied = 0;
        while (copied < length) {
            int copyLen = Math.min(chars.length, length - copied);
            string.getChars(offset + copied, offset + copied + copyLen, chars, 0);
            write(chars, 0, copyLen, out);
            copied += copyLen;
        }
    }

    @Override
    public void write(char @Nullable [] chars, int offset, int length, OutputStream out) throws IOException {
        if (chars == null || length == 0) {
            return;
        }
        var encoder = this.charsetEncoder.reset();
        var bytes = this.buffers.bytes();
        var buf = ByteBuffer.wrap(bytes);
        var in = CharBuffer.wrap(chars, offset, length);
        for (; ; ) {
            var cr = in.hasRemaining()
                    ? encoder.encode(in, buf, true)
                    : CoderResult.UNDERFLOW;
            if (cr.isUnderflow()) {
                cr = encoder.flush(buf);
            }
            if (cr.isUnderflow()) {
                break;
            }
            if (cr.isOverflow()) {
                out.write(bytes, 0, buf.position());
                buf.clear();
                continue;
            }
            cr.throwException();
        }
        if (buf.position() > 0) {
            out.write(bytes, 0, buf.position());
            buf.clear();
        }
    }
}
