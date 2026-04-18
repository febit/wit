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
package org.febit.wit.util;

import org.junit.jupiter.api.Test;

import static org.febit.wit.ir.IRTestSupport.args;
import static org.febit.wit.util.Args.at;
import static org.junit.jupiter.api.Assertions.*;

class ArgsTest {

    @Test
    void testEmpty() {
        var empty = Args.empty();
        assertNotNull(empty);
        assertEquals(0, empty.length);
    }

    @Test
    void testAt() {
        assertNull(at(null, 0));
        assertNull(at(args(1, 2, 3), 3));
        assertEquals(2, at(args(1, 2, 3), 1));
    }

    @Test
    void testEnsureSize() {
        var arr = args(1, 2, 3);

        assertSame(arr, Args.ensureSize(arr, 2));
        assertSame(arr, Args.ensureSize(arr, 3));

        assertArrayEquals(new Object[5], Args.ensureSize(null, 5));
        assertArrayEquals(args(1, 2, 3, null, null), Args.ensureSize(arr, 5));
    }
}
