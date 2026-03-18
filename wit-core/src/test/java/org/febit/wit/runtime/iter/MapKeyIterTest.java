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
package org.febit.wit.runtime.iter;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class MapKeyIterTest {

    @Test
    void empty() {
        var iter = MapKeyIter.of(Map.of());

        assertEquals(-1, iter.index());
        assertFalse(iter.hasNext());
        assertThrowsExactly(NoSuchElementException.class, iter::next);
        assertEquals(-1, iter.index());
    }

    @Test
    void test() {
        var iter = MapKeyIter.of(new TreeMap<>(Map.of(
                "a", 1,
                "b", 2,
                "c", 3
        )));

        assertEquals(-1, iter.index());
        assertThrowsExactly(IllegalStateException.class, iter::value);

        assertTrue(iter.hasNext());
        assertEquals("a", iter.next());
        assertEquals(0, iter.index());
        assertEquals(1, iter.value());

        assertTrue(iter.hasNext());
        assertEquals("b", iter.next());
        assertEquals(1, iter.index());
        assertEquals(2, iter.value());

        assertTrue(iter.hasNext());
        assertEquals("c", iter.next());
        assertEquals(2, iter.index());
        assertEquals(3, iter.value());

        assertFalse(iter.hasNext());
        assertThrowsExactly(NoSuchElementException.class, iter::next);
        assertEquals(2, iter.index());
        assertEquals(3, iter.value());

        assertFalse(iter.hasNext());
        assertThrowsExactly(NoSuchElementException.class, iter::next);
        assertEquals(2, iter.index());
        assertEquals(3, iter.value());
    }

}
