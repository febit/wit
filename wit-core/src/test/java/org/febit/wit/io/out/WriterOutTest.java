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
package org.febit.wit.io.out;

import org.junit.jupiter.api.Test;

import static org.febit.wit.io.OutTestUtils.wrapAsWriterOut;
import static org.junit.jupiter.api.Assertions.*;

class WriterOutTest {

    static final String HELLO_WORLD = "Hello, World!";

    @Test
    void testWrite() {
        var buffer = new StringBuilder();
        var out = wrapAsWriterOut(buffer);

        out.write(HELLO_WORLD);
        assertEquals(HELLO_WORLD, buffer.toString());

        buffer.setLength(0);
        out.write(HELLO_WORLD, 0, 5);
        assertEquals("Hello", buffer.toString());

        buffer.setLength(0);
        out.write(HELLO_WORLD.toCharArray());
        assertEquals(HELLO_WORLD, buffer.toString());

        buffer.setLength(0);
        out.write(HELLO_WORLD.toCharArray(), 0, 5);
        assertEquals("Hello", buffer.toString());

        buffer.setLength(0);
        out.write(HELLO_WORLD.getBytes());
        assertEquals(HELLO_WORLD, buffer.toString());

        buffer.setLength(0);
        out.write(HELLO_WORLD.getBytes(), 0, 5);
        assertEquals("Hello", buffer.toString());
    }

}
