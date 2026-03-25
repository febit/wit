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

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class DefaultEncoderTest {

    @Test
    void empty() throws IOException {
        var encoder = new DefaultEncoder(StandardCharsets.UTF_8);

        var out = new ByteArrayOutputStream();

        encoder.encode("", 0, 0, out);
        assertArrayEquals(new byte[0], out.toByteArray());

        encoder.encode((char[]) null, 0, 0, out);
        assertArrayEquals(new byte[0], out.toByteArray());

        encoder.encode(new char[0], 0, 0, out);
        assertArrayEquals(new byte[0], out.toByteArray());

        encoder.encode(CharBuffer.wrap(new char[0]), out);
        assertArrayEquals(new byte[0], out.toByteArray());
    }

    @Test
    void highSurrogateIssues() {
        var encoder = new DefaultEncoder(StandardCharsets.UTF_8);
        var out = new ByteArrayOutputStream();

        // At offset 0
        var ex = assertThrows(IllegalStateException.class, () -> {
            encoder.encode("\uD800", 0, 1, out);
        });
        assertEquals("High surrogate char at offset 0 cannot be encoded", ex.getMessage());

        // At offset 1
        ex = assertThrows(IllegalStateException.class, () -> {
            encoder.encode("A\uD800", 0, 2, out);
        });
        assertEquals("High surrogate char at offset 1 cannot be encoded", ex.getMessage());
    }

    @Test
    void malformedInputException() {
        var encoder = new DefaultEncoder(StandardCharsets.UTF_8);
        var out = new ByteArrayOutputStream();

        assertThrows(MalformedInputException.class, () -> {
            var chars = "\uD800A".toCharArray();
            encoder.encode(CharBuffer.wrap(chars), out);
        });
    }

    @Test
    void smallBuffer() throws IOException {
        var encoder = new DefaultEncoder(
                StandardCharsets.UTF_8.newEncoder(),
                Buffers.of(17)
        );
        var out = new ByteArrayOutputStream();
        encoder.encode(Samples.TEXT_UTF8, 0, Samples.TEXT_UTF8.length(), out);
        assertArrayEquals(Samples.TEXT_UTF8.getBytes(StandardCharsets.UTF_8), out.toByteArray());
    }

    @Test
    void smallBufferChars() throws IOException {
        var encoder = new DefaultEncoder(
                StandardCharsets.UTF_8.newEncoder(),
                Buffers.of(17)
        );
        var out = new ByteArrayOutputStream();
        var chars = Samples.TEXT_UTF8.toCharArray();
        encoder.encode(chars, 0, chars.length, out);
        assertArrayEquals(Samples.TEXT_UTF8.getBytes(StandardCharsets.UTF_8), out.toByteArray());
    }

    @Test
    void largeBuffer() throws IOException {
        var encoder = new DefaultEncoder(
                StandardCharsets.UTF_8.newEncoder(),
                Buffers.of(Samples.TEXT_UTF8.length() * 4)
        );
        var out = new ByteArrayOutputStream();
        encoder.encode(Samples.TEXT_UTF8, 0, Samples.TEXT_UTF8.length(), out);
        assertArrayEquals(Samples.TEXT_UTF8.getBytes(StandardCharsets.UTF_8), out.toByteArray());
    }

    @Test
    void largeBufferChars() throws IOException {
        var encoder = new DefaultEncoder(
                StandardCharsets.UTF_8.newEncoder(),
                Buffers.of(Samples.TEXT_UTF8.length() * 4)
        );
        var out = new ByteArrayOutputStream();
        var chars = Samples.TEXT_UTF8.toCharArray();
        encoder.encode(chars, 0, chars.length, out);
        assertArrayEquals(Samples.TEXT_UTF8.getBytes(StandardCharsets.UTF_8), out.toByteArray());
    }
}
