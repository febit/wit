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

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.febit.wit.io.codec.Samples.TEXT_UTF8;
import static org.junit.jupiter.api.Assertions.*;

class DefaultDecoderTest {

    @Test
    void empty() throws IOException {
        var decoder = new DefaultDecoder(StandardCharsets.UTF_8);
        var writer = new StringWriter();
        decoder.decode(new byte[0], 0, 0, writer);
        assertEquals("", writer.toString());
        decoder.decode(null, 0, 0, writer);
        assertEquals("", writer.toString());
    }

    @Test
    void smallBuffer() throws IOException {
        var decoder = new DefaultDecoder(
                StandardCharsets.UTF_8.newDecoder(),
                Buffers.of(17)
        );
        var writer = new StringWriter();
        var bytes = TEXT_UTF8.getBytes(StandardCharsets.UTF_8);
        decoder.decode(bytes, 0, bytes.length, writer);
        assertEquals(TEXT_UTF8, writer.toString());
    }

    @Test
    void largeBuffer() throws IOException {
        var decoder = new DefaultDecoder(
                StandardCharsets.UTF_8.newDecoder(),
                Buffers.of(TEXT_UTF8.length() * 4)
        );
        var writer = new StringWriter();
        var bytes = TEXT_UTF8.getBytes(StandardCharsets.UTF_8);
        decoder.decode(bytes, 0, bytes.length, writer);
        assertEquals(TEXT_UTF8, writer.toString());
    }
}
