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

import static org.junit.jupiter.api.Assertions.*;

class DiscardOutTest {

    @Test
    void testWrite() {
        var out = DiscardOut.get();
        assertDoesNotThrow(() -> {
            out.write("Hello, World!");
            out.write("Hello, World!", 0, 5);
            out.write("Hello, World!".toCharArray());
            out.write("Hello, World!".toCharArray(), 0, 5);
            out.write("Hello, World!".getBytes());
            out.write("Hello, World!".getBytes(), 0, 5);
        });
    }

}
