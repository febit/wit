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
package org.febit.wit;

import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.io.out.OutputStreamOut;
import org.febit.wit.io.out.WriterOut;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import static org.febit.wit.WitTestSupport.WIT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class WitTest {

    @Test
    void isEnabled() {
        assertFalse(WIT.isEnabled(Feature.LOOSE_VAR));
        assertTrue(WIT.isEnabled(Feature.TRIM_CODE_BLOCK_BLANK_LINE));
    }

    @Test
    void scriptCaches() throws NoSuchSourceException {
        assertNotSame(
                WIT.script("string: hello"),
                WIT.script("string: hello")
        );
        assertNotSame(
                WIT.script("string: hello"),
                WIT.script("string: x", "string: hello")
        );
        assertEquals(
                WIT.script("string: hello"),
                WIT.script("string: hello")
        );
        assertEquals(
                WIT.script("string: hello"),
                WIT.script("string: x", "string: hello")
        );

        assertSame(
                WIT.script("cached-string: hello"),
                WIT.script("cached-string: hello")
        );
        assertSame(
                WIT.script("cached-string: hello"),
                WIT.script("x", "cached-string: hello")
        );
    }

    @Test
    void scriptNormalizedCaches() throws NoSuchSourceException {

        assertNotSame(
                WIT.script("classpath:/a.wit"),
                WIT.script("classpath:/x/../a.wit")
        );
        assertNotSame(
                WIT.script("classpath:/a.wit"),
                WIT.script("classpath:/x/b.wit", "a.wit")
        );
        assertEquals(
                WIT.script("classpath:/a.wit"),
                WIT.script("classpath:/x/../a.wit")
        );
        assertEquals(
                WIT.script("classpath:/a.wit"),
                WIT.script("classpath:/x/b.wit", "../a.wit")
        );

        assertSame(
                WIT.script("cached:/a.wit"),
                WIT.script("cached:/x/../a.wit")
        );
        assertSame(
                WIT.script("cached:/a.wit"),
                WIT.script("cached:/x/b.wit", "../a.wit")
        );
    }

    @Test
    void scriptIllegal() {
        assertThrows(NoSuchSourceException.class,
                () -> WIT.script("../../a.wit")
        );

        assertThrows(NoSuchSourceException.class,
                () -> WIT.script("classpath:/no-such-path/x", "../../../b.wit")
        );
    }

    @Test
    void asOut() {
        var writer = mock(Writer.class);
        var output = mock(OutputStream.class);

        var out = WIT.asOut(output);

        assertNotNull(out);
        assertInstanceOf(OutputStreamOut.class, out);
        assertTrue(out.preferBytes());
        assertEquals(WIT.charset(), out.charset());

        out = WIT.asOut(output, StandardCharsets.US_ASCII);
        assertNotNull(out);
        assertInstanceOf(OutputStreamOut.class, out);
        assertTrue(out.preferBytes());
        assertEquals(StandardCharsets.US_ASCII, out.charset());

        out = WIT.asOut(writer);
        assertNotNull(out);
        assertInstanceOf(WriterOut.class, out);
        assertFalse(out.preferBytes());
        assertEquals(WIT.charset(), out.charset());

        out = WIT.asOut(writer, StandardCharsets.US_ASCII);
        assertNotNull(out);
        assertInstanceOf(WriterOut.class, out);
        assertFalse(out.preferBytes());
        assertEquals(StandardCharsets.US_ASCII, out.charset());

    }
}
